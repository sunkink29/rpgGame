package gameObjects;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import org.joml.*;

import components.Collider;
import components.Renderer;
import components.Transform;
import main.Controls;

import java.lang.Math;

import rendering.Model;

public class Enemy extends GameObject implements Damageable {

	Vector3f normalColor = new Vector3f(0.1f, 0.5f, 0.1f);
	Vector3f hurtColor = new Vector3f(1, 0, 0);
	Vector2f StartPosition;
	int enemyState = 0;
	float speed = 2f;
	float rotationSpeed = (float) (2 * Math.PI);
	int health;
	public boolean dead = false;
	public boolean isHit = false;
	public boolean damaged = false;
	Vector2f[] path;
	int currentPathPoint = 0;
	float detectionDistance;
	boolean followingPlayer = false;
	GameObject sword;
	Vector2f swordOffset = new Vector2f(0.2f, -0.1f);
	Vector2f currentSwordOffset = new Vector2f(swordOffset);
	float attackduration = 0.5f;
	boolean isAttacking = false;
	double attackStartTime = 0;
	Vector2f attackOffset = new Vector2f(swordOffset.x, swordOffset.y - 1f);
	int attackState = 1;

	
	public Enemy(Vector2f position, Vector2f scale) {
		this(position, scale, 1);
	}
	
	public Enemy(Vector2f position, Vector2f scale, int health) {
		this(position, scale, health, new Vector2f[]{new Vector2f(position.x,position.y)}, 0);
	}
	
	public Enemy(Vector2f position, Vector2f scale, int health, Vector2f[] path, int pathStartIndex) {
		this(position, scale, health, path, pathStartIndex, 5);
	}
	
	public Enemy(Vector2f position, Vector2f scale, int health, Vector2f[] path, int pathStartIndex, float detectionDistance) {
		super(new Transform(position,0 , scale, 0),defaultShapes.Square.getInstance());
		renderer.setColor(new Vector3f(normalColor));
		currentPathPoint = pathStartIndex;
		StartPosition = position;
		this.health = health;
		this.path = new Vector2f[path.length];
		for(int i = 0; i < path.length; i++){
			this.path[i] = new Vector2f(path[i]);
//			this.path[i].add(position.x,position.y);
		}
		this.detectionDistance = detectionDistance;
		addComponent(new Collider(false, CollisionObjs.ENEMY));
	}
	
	@Override
	public void init() {
		sword = new Sword(new Transform(transform.getPosition(), -0.1f, new Vector2f(0.2f, 0.4f), 0), (CollisionObjs.PLAYER | CollisionObjs.DESTRUCTIBLEOBEJECT));
		sword.renderer.setColor( new Vector3f(0.88f, 0.46f, 0.46f));
	}
	//transform.getPosition(), new Vector3f(0.88f, 0.46f, 0.46f), new Vector2f(0.2f, 0.4f)
	
	@Override
	public void renderObject(Matrix4f viewMatrix) {
		sword.renderObject(viewMatrix);
		super.renderObject(viewMatrix);
	}
	
	@Override
	public void update(Map map) {
		super.update(map);
		sword.update(map);

		if (!isHit && damaged) {
			renderer.setColor(new Vector3f(normalColor));
			damaged = false;
		}
		if (health <= 0) {
			dead = true;
			map.removeObject(this);
		}
		
		if (!dead) {
			Vector2f targetDirection;
			float distance = transform.getPosition().distance(Player.currentPlayer.transform.getPosition());
			boolean applyMovement = false;
			Vector2f targetPoint = null;
			
			if (distance < detectionDistance) {
				targetPoint = Player.currentPlayer.transform.getPosition();
				if (distance > 1) {
					applyMovement = true;
					followingPlayer = true;
				} else {
					if (!isAttacking) {
						startAttackAnimation();
					}
				}
			} else {
				if (followingPlayer) {
					followingPlayer = false;
					int closestWayPoint = 0;
					Vector2f position = new Vector2f(transform.getPosition().x, transform.getPosition().y);
					for (int i=0; i < path.length; i++) {
						if (position.distance(path[i]) < position.distance(path[closestWayPoint])) {
							closestWayPoint = i;
						}
					}
					currentPathPoint = closestWayPoint;
				}
				applyMovement = true;
//				Vector2f temp = new Vector2f();
//				path[currentPathPoint].sub( new Vector2f(position.x, position.y), temp).normalize();
				targetPoint = path[currentPathPoint];
//				movementDirection = new Vector3f(temp, 0);
			}
			
			targetDirection = moveToPoint(targetPoint);
						
			// apply movement
			Vector2f newPos = transform.getPosition();
			if (applyMovement) {
				newPos.add(targetDirection.mul(Controls.deltaTime).mul(speed));
				transform.setPosition(newPos);
			}
			
			// calculate sword position
			attackAnimation();
			Vector2f swordPosition = newPos;
			float swordOffsetAngle = (float) Math.atan2(currentSwordOffset.y, currentSwordOffset.x);
			float swordOffsetRadius = (float) Math.sqrt(Math.pow(currentSwordOffset.x, 2) + Math.pow(currentSwordOffset.y, 2));
			swordPosition.add((float)Math.cos(transform.getRotation() + swordOffsetAngle) * -swordOffsetRadius, (float)Math.sin(transform.getRotation() + swordOffsetAngle) * -swordOffsetRadius);
			sword.transform.setPosition(swordPosition);
			sword.transform.setRotation(transform.getRotation());

			
			// go to next wayPoint
			if(path[currentPathPoint].distance(new Vector2f(transform.getPosition().x, transform.getPosition().y)) < .05) {
				transform.setPosition(path[currentPathPoint]);
				if (currentPathPoint + 1 >= path.length) {
					currentPathPoint = 0;
				} else {
					currentPathPoint++;
				}
			}
		}
		isHit = false;
	}
	
	public void damageObject(int damage) {
		if (damage > 0) {
			if (!damaged) {
				health -= damage;
			}
			renderer.setColor(new Vector3f(hurtColor));
			damaged = true;
		}
	}
	
	public void objectCollided(Collider otherObject) {
		if (otherObject.getGameObject() instanceof Sword) {
			Sword sword = (Sword) otherObject.getGameObject();
			if ((sword.damageObjects & CollisionObjs.ENEMY) != 0) {
				if (!isHit) {
					isHit = true;
				}
			}
		}
	}
	
	// returns movement direction
	Vector2f moveToPoint(Vector2f targetPoint) {
		Vector2f targetDirection = new Vector2f();
		// movement calculation
		targetPoint.sub(transform.getPosition(),targetDirection).normalize();		
		
		// rotation calculation
		double targetDirectionDegree = Math.atan2(targetDirection.y, targetDirection.x);
		targetDirectionDegree += -Math.PI/2;
		
		double rotation = (float) (transform.getRotation()%(Math.PI*2));
		
		double rDistance =	targetDirectionDegree - rotation;
		rDistance = mod((rDistance + Math.PI), Math.PI * 2) - Math.PI;
		
		rDistance = rDistance>=0?rDistance:Math.PI + (Math.PI + rDistance);
		
		float rDirection = -(rDistance>Math.PI?1:-1)/*-1*/;
					
		if (Math.abs(rDistance) > Math.PI / 128) {
			float newRot = transform.getRotation() + rotationSpeed * Controls.deltaTime * rDirection;
			transform.setRotation(newRot);
		}
		return targetDirection;
	}
	
	void startAttackAnimation(){
		isAttacking = true;
		attackStartTime = glfwGetTime();
	}
	
	void attackAnimation() {
		if (isAttacking) {
//			System.out.println(attackState);
			if (Math.abs(currentSwordOffset.distance(attackOffset)) <= 0.1){
				attackState = 2;
//				attackStartTime = glfwGetTime();
			} else if (Math.abs(currentSwordOffset.distance(swordOffset)) <= 0.1 && attackState == 2) {
				attackState = 3;
			} else if (attackState == 3) {
				attackState = 1;
				isAttacking = false;
			}
			
			float IFactor = 0;
			if (attackState == 1) {
				IFactor = (float) ((glfwGetTime() - attackStartTime) / (attackduration / 2));
//				IFactor = (float) ( 1 - ((glfwGetTime() - attackStartTime) / (attackduration / 2)));
			} else if (attackState == 2) {
				IFactor = (float) ( 1 - ((glfwGetTime() - attackStartTime - (attackduration / 2)) / (attackduration / 2)));
			}
//			System.out.println(IFactor);
//			System.out.println(currentSwordOffset);
//			System.out.println(swordOffset);
			swordOffset.lerp(attackOffset, IFactor, currentSwordOffset);
			if (!isAttacking) {
				currentSwordOffset = new Vector2f(swordOffset);
			}
		}
	}
	
	double mod(double a, double n) {
		return (a % n + n) % n;
	}
}
