package gameObjects;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import components.Collider;
import components.Transform;
import main.Controls;
import main.Input;

import java.lang.Math;

public class Player extends GameObject implements Damageable {
	
	public static Player currentPlayer;
	GameObject sword;
	Vector2f swordOffset = new Vector2f(0.4f, -0.2f);
	Vector2f currentSwordOffset = new Vector2f(swordOffset);
	float attackduration = 0.5f;
	boolean isAttacking = false;
	double attackStartTime = 0;
	Vector2f attackOffset = new Vector2f(swordOffset.x, swordOffset.y + 1/transform.getScale().y);
	int attackState = 1;
	float speed = 5.0f; // 3 units / second
	public Vector3f movementDirection = new Vector3f();
	float health = 10;
	boolean damaged = false;
	boolean isHit = false;
	USP[] shaderProperties;
	Vector2f swordTranslation;
	float rotationSpeed = (float) (Math.PI*1.5);

	public Player(Vector2f position, float rotation) {
		super(new Transform(position, 0, new Vector2f(0.5f), rotation), defaultShapes.Triangle.getInstance());
		shaderProperties = new USP[] {new USP("shaderPosition", new Vector2f(0, 0))};
		renderer.setShaderProperties(shaderProperties);
		addComponent(new Collider(false, CollisionObjs.PLAYER));
		
		sword = new Sword(new Transform(position, -0.1f, new Vector2f(0.4f, 0.8f), rotation), (CollisionObjs.ENEMY | CollisionObjs.DESTRUCTIBLEOBEJECT));
		addChildObject(sword);
		sword.renderer.setColor(new Vector3f(0.88f, 0.46f, 0.46f));
		currentPlayer = this;
	}
	
	public void renderPlayer(Matrix4f viewMatrix){
		super.renderObject(viewMatrix);
	}
		
	public void updatePlayer(long window , Map map) {
		if (!isHit && damaged) {
			renderer.setColor(new Vector3f(0));
			damaged = false;
		}
		super.update(map);
		Vector3f direction = new Vector3f(0, 0, 1);
		Vector3f right = new Vector3f(-1, 0, 0);
		Vector3f dest = new Vector3f();
		Vector3f up = right.cross(direction, dest);
		Vector3f movementDirection = new Vector3f(0);
		this.movementDirection = movementDirection;
		
		if (Input.getButtonDown("forwardsMove")){
			movementDirection.add(up);
		}
		// Move backward
		if (Input.getButtonDown("backwardsMove")){
			movementDirection.sub(up);
		}
		// Strafe right
		if (Input.getButtonDown("rightMove")){
			movementDirection.add(right);
		}
		// Strafe left
		if (Input.getButtonDown("leftMove")){
			movementDirection.sub(right);
		}
		
		float rotation = transform.getRotation();
		if (Input.getButtonDown("leftRot")) {
			rotation -= rotationSpeed * Controls.deltaTime;
		}
		if (Input.getButtonDown("rightRot")) {
			rotation += rotationSpeed * Controls.deltaTime;
		}
		
		if (Input.getButtonDown("attack")) {
			if (!isAttacking) {
				startAttackAnimation();
			}
		}
		
		if (movementDirection.x != 0){
			movementDirection.x /= movementDirection.x * movementDirection.x<0?-1:1;
		}
		if (movementDirection.y != 0){
			movementDirection.y /= movementDirection.y * movementDirection.y<0?-1:1;
		}
		Vector3f movement = movementDirection.mul(Controls.deltaTime,dest).mul(speed);
		Vector2f playerPos = transform.getPosition();
		
		transform.setPosition(playerPos.add(movement.x, movement.y));
		transform.setRotation(rotation);
		
		if (Input.getButtonDown("printPlayerPos")){
			System.out.println(playerPos);
		}
		
		attackAnimation();
		sword.transform.setPosition(currentSwordOffset);
		
		isHit = false;
	}
	
	public void damageObject(int damage) {
		if (damage > 0) {
			if (!damaged) {
				health -= damage;
			}
			renderer.setColor(new Vector3f(1, 0, 0));
			damaged = true;
		}
	}
	
	public void objectCollided(Collider otherObject) {
		if (otherObject.getGameObject() instanceof Sword) {
			Sword sword = (Sword) otherObject.getGameObject();
			if ((sword.damageObjects & CollisionObjs.PLAYER) != 0) {
				if (!isHit) {
					isHit = true;
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
}
