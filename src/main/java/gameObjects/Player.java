package gameObjects;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.lang.Math;

import javax.swing.text.Position;

import rendering.Controls;
import rendering.Model;

public class Player {
	
	public static Player currentPlayer;
	GameObject player;
	GameObject sword;
	Vector2f swordOffset = new Vector2f(0.2f, -0.1f);
	Vector2f currentSwordOffset = new Vector2f(swordOffset);
	float attackduration = 0.5f;
	boolean isAttacking = false;
	double attackStartTime = 0;
	Vector2f attackOffset = new Vector2f(swordOffset.x, swordOffset.y - 1f);
	int attackState = 1;
	private Vector3f playerPosition = new Vector3f(0,0,-1f);
	float speed = 5.0f; // 3 units / second
	public Vector3f movementDirection = new Vector3f();
	float health = 10;
	UniformShaderProperty[] shaderProperties;

	public Player() {
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
		
		shaderProperties = new UniformShaderProperty[] {new UniformShaderProperty("shaderPosition", new Vector2f(0, 0))};
		player = new GameObject(playerPosition, new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.5f),"general",shaderProperties);
		sword = new GameObject(playerPosition, new Vector3f(0.88f, 0.46f, 0.46f), new Vector3f(0.2f, 0.4f, 1));
		player.init(triangleVao, triangleProgramID);
		sword.init(triangleVao, triangleProgramID);
		currentPlayer = this;
	}
	
	public void renderPlayer(Matrix4f viewMatrix){
		player.renderObject(viewMatrix);
		sword.renderObject(viewMatrix);
	}
	
	public void updatePlayer(long window , Map map) {
		Vector3f direction = new Vector3f(0, 0, 1);
		Vector3f right = new Vector3f(-1, 0, 0);
		Vector3f dest = new Vector3f();
		Vector3f up = right.cross(direction, dest);
		Vector3f movementDirection = new Vector3f(0);
		this.movementDirection = movementDirection;
		
		if (glfwGetKey(window, GLFW_KEY_W ) == GLFW_PRESS){
			movementDirection.add(up);
		}
		// Move backward
		if (glfwGetKey(window, GLFW_KEY_S ) == GLFW_PRESS){
			movementDirection.sub(up);
		}
		// Strafe right
		if (glfwGetKey(window, GLFW_KEY_D ) == GLFW_PRESS){
			movementDirection.add(right);
		}
		// Strafe left
		if (glfwGetKey(window, GLFW_KEY_A ) == GLFW_PRESS){
			movementDirection.sub(right);
		}
		
		if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT ) == GLFW_PRESS){
			if (!isAttacking) {
				startAttackAnimation();
			}
		}
		
		DoubleBuffer xpos = BufferUtils.createDoubleBuffer(1), ypos = BufferUtils.createDoubleBuffer(1); // create buffers for cursor position
		glfwGetCursorPos(window, xpos, ypos); // get cursor position
		Vector2f cursorPos = new Vector2f((float)xpos.get(0), (float)ypos.get(0)); // put the cursor position into a vector object
		IntBuffer width = BufferUtils.createIntBuffer(1), height = BufferUtils.createIntBuffer(1); // create buffers for window size
		glfwGetWindowSize(window, width, height); // get the window size
		Vector2f windowSize = new Vector2f(width.get(0),height.get(0)); // put the window size into a vector
		cursorPos.sub(windowSize.mul(0.5f)); // get the cursor position
		cursorPos.y = -cursorPos.y; // invert the y axis
		cursorPos.x /= windowSize.x; // transform the cursor so that at the edge of the screen the cursor position is 1
		cursorPos.y /= windowSize.y;
		cursorPos.mul((Controls.zoom*((float)width.get(0)/height.get(0)))*0.5f, Controls.zoom*0.5f) // scale the cursor into world coordinates  
		.add(-Controls.cameraPosition.x, Controls.cameraPosition.y) // transform the cursor position to be relative to the world orgin
		.add(getPlayerPosition().x, -getPlayerPosition().y); // transform the cursor position to be relative to the player orgin
		double rotation = Math.atan2(cursorPos.x, cursorPos.y); // get an angle from the x and y coordinates
		
		Vector3f playerCollision = Collision.isPlayerColliding(map, this);
		movementDirection.sub(playerCollision);
		if (movementDirection.x != 0){
			movementDirection.x /= movementDirection.x * movementDirection.x<0?-1:1;
		}
		if (movementDirection.y != 0){
			movementDirection.y /= movementDirection.y * movementDirection.y<0?-1:1;
		}
		health += movementDirection.z;
		movementDirection.z = 0;
		setPlayerPosition(getPlayerPosition().add(movementDirection.mul(Controls.deltaTime,dest).mul(speed)));
//		System.out.println(playerCollision);
		
		attackAnimation();
		Vector3f swordPosition = getPlayerPosition();
		float swordOffsetAngle = (float) Math.atan2(currentSwordOffset.y, currentSwordOffset.x);
		float swordOffsetRadius = (float) Math.sqrt(Math.pow(currentSwordOffset.x, 2) + Math.pow(currentSwordOffset.y, 2));
		swordPosition.add((float)Math.cos(rotation + swordOffsetAngle) * -swordOffsetRadius, (float)Math.sin(rotation + swordOffsetAngle) * -swordOffsetRadius,0);
		
		player.position = getPlayerPosition();
		player.rotation = (float) rotation;
		sword.position = swordPosition;
		sword.rotation = (float) rotation;
		
		if (glfwGetKey(window, GLFW_KEY_F ) == GLFW_PRESS){
			System.out.println(playerPosition);
		}
	}

	public Vector3f getPlayerPosition() {
		return new Vector3f(playerPosition);
	}

	public void setPlayerPosition(Vector3f playerPosition) {
		this.playerPosition = playerPosition;
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
