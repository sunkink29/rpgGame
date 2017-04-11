package gameObjects;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import org.joml.*;

import java.lang.Math;

import rendering.Controls;
import rendering.Model;

public class Enemy extends GameObject {

	Vector3f normalColor = new Vector3f(0.1f, 0.5f, 0.1f);
	Vector3f hurtColor = new Vector3f(1, 0, 0);
	Vector3f StartPosition;
	int enemyState = 0;
	float speed = 2f;
	float rotationSpeed = (float) (2 * Math.PI);
	int health;
	public boolean dead = false;
	public boolean isHit = false;
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

	
	public Enemy(Vector3f position, Vector3f color, Vector3f scale) {
		this(position, color, scale, 1);
	}
	
	public Enemy(Vector3f position, Vector3f color, Vector3f scale, int health) {
		this(position, color, scale, health, new Vector2f[]{new Vector2f(position.x,position.y)}, 0);
	}
	
	public Enemy(Vector3f position, Vector3f color, Vector3f scale, int health, Vector2f[] path, int pathStartIndex) {
		this(position, color, scale, health, path, pathStartIndex, 5);
	}
	
	public Enemy(Vector3f position, Vector3f color, Vector3f scale, int health, Vector2f[] path, int pathStartIndex, float detectionDistance) {
		super(position, color, scale);
		currentPathPoint = pathStartIndex;
		this.position.z = -1;
		StartPosition = position;
		this.health = health;
		this.path = new Vector2f[path.length];
		for(int i = 0; i < path.length; i++){
			this.path[i] = new Vector2f(path[i]);
//			this.path[i].add(position.x,position.y);
		}
		this.detectionDistance = detectionDistance;
	}
	
	public void init() {
		float vertices[] = {
			    -0.4f,  0.5f, // Top-left
			     0.4f,  0.5f, // Top-right
			     0.5f, -0.5f, // Bottom-right
			    -0.5f, -0.5f  // Bottom-left
		};
		
		int elements[] = {
			    0, 1, 2,
			    2, 3, 0
		};
		int[] square = Model.getModelIds("enemy", "general", vertices, elements);
		int squareVao = square[0];
		int squareProgramId = square[1];
		init(squareVao, squareProgramId);
		
		float vertices2[] = {
			     0.0f,  0.7f, // Top-left
			     0.5f, -0.5f, // Top-right
			    -0.5f, -0.5f, // Bottom-right
		};
		
		int elements2[] = {
			    2, 1, 0,
		};
		int[] triangle = Model.getModelIds("triangle", "general", vertices2, elements2);
		int triangleVao = triangle[0];
		int triangleProgramID = triangle[1];
		sword = new GameObject(position, new Vector3f(0.88f, 0.46f, 0.46f), new Vector3f(0.2f, 0.4f, 1));
		sword.init(triangleVao, triangleProgramID);
	}
	
	@Override
	public void renderObject(Matrix4f viewMatrix) {
		sword.renderObject(viewMatrix);
		super.renderObject(viewMatrix);
	}
	
	public void updateEnemy(Map map) {
		Vector3f collisionDirection = Collision.isEnemyColliding(map, this);
		Vector3f targetDirection = new Vector3f();
//		System.out.println(health);
		if (collisionDirection.z < 0) {
			if (!isHit) {
				health += collisionDirection.z;
			}
			isHit = true;
		} else {
			isHit = false;
		}
		if (health <= 0) {
			dead = true;
		}
		if (!dead) {
			float distance = position.distance(Player.currentPlayer.player.position);
			boolean applyMovement = false;
			Vector3f targetPoint = null;
//			System.out.println(distance);
			if (distance < detectionDistance) {
				targetPoint = Player.currentPlayer.player.position;
				if (distance > 1) {
					applyMovement = true;
//					Player.currentPlayer.player.position.sub(position, movementDirection).normalize().mul(1.5f);
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
					Vector2f position = new Vector2f(this.position.x, this.position.y);
					for (int i=0; i < path.length; i++) {
						if (position.distance(path[i]) < position.distance(path[closestWayPoint])) {
							closestWayPoint = i;
						}
//						System.out.println(position.distance(path[closestWayPoint]));
					}
					currentPathPoint = closestWayPoint;
				}
				applyMovement = true;
//				Vector2f temp = new Vector2f();
//				path[currentPathPoint].sub( new Vector2f(position.x, position.y), temp).normalize();
				targetPoint = new Vector3f(path[currentPathPoint], 0);
//				movementDirection = new Vector3f(temp, 0);
			}
			
			// movement calculation
			targetPoint.sub(position, targetDirection).normalize();
			targetDirection.z = 0;
			
//			// rotation calculation
			double playerDirection = Math.atan2(targetDirection.y, targetDirection.x);
			playerDirection += -Math.PI/2;
			
			double rotation = (float) (this.rotation%(Math.PI*2));
			
			double rDistance =	playerDirection - rotation;
			rDistance = mod((rDistance + Math.PI), Math.PI * 2) - Math.PI;
			
			rDistance = rDistance>=0?rDistance:Math.PI + (Math.PI + rDistance);
			
			float rDirection = -(rDistance>Math.PI?1:-1)/*-1*/;
						
			if (Math.abs(rDistance) > Math.PI / 128) {
				this.rotation += rotationSpeed * Controls.deltaTime * rDirection;
			}
//			System.out.println(rDistance + " " + playerDirection + " " +  rotation + " " + rDirection + " " + this.rotation);

			
			// color calculation
			if (collisionDirection.z < 0) {
				color = new Vector3f(hurtColor);
			} else {
				color = new Vector3f(normalColor);
			}
			
			// collision detection
			targetDirection.sub(collisionDirection).normalize();
//			if (targetDirection.x != 0){
//				targetDirection.x /= targetDirection.x * (targetDirection.x<0?-1:1);
//			}
//			if (targetDirection.y != 0){
//				targetDirection.y /= targetDirection.y * (targetDirection.y<0?-1:1);
//			}
			targetDirection.z = 0;
			
//			System.out.println(targetDirection);
			
			// apply movement
			if (applyMovement)
				position.add(targetDirection.mul(Controls.deltaTime).mul(speed));
			
			// calculate sword position
			attackAnimation();
			Vector3f swordPosition = new Vector3f(position);
			float swordOffsetAngle = (float) Math.atan2(currentSwordOffset.y, currentSwordOffset.x);
			float swordOffsetRadius = (float) Math.sqrt(Math.pow(currentSwordOffset.x, 2) + Math.pow(currentSwordOffset.y, 2));
			swordPosition.add((float)Math.cos(rotation + swordOffsetAngle) * -swordOffsetRadius, (float)Math.sin(rotation + swordOffsetAngle) * -swordOffsetRadius,0);
			sword.position = swordPosition;
			sword.rotation = (float) rotation;

			
			// go to next wayPoint
			if(path[currentPathPoint].distance(new Vector2f(position.x, position.y)) < .05) {
				position = new Vector3f(path[currentPathPoint], position.z);
				if (currentPathPoint + 1 >= path.length) {
					currentPathPoint = 0;
				} else {
					currentPathPoint++;
				}
			}
		}
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
