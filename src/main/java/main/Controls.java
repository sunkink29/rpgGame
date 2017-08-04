package main;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import gameObjects.Player;

import java.io.FileNotFoundException;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public class Controls {
	
	static Matrix4f ViewMatrix;
	static Matrix4f ProjectionMatrix;
	
	public static Matrix4f getViewMatrix(){
		return ViewMatrix;
	}
	public static Matrix4f getProjectionMatrix(){
		return ProjectionMatrix;
	}
	
	public static Vector3f cameraPosition = new Vector3f(0, 0, 2f);
	// Initial Field of View
	public static float zoom = 15f;

	static float cameraSpeed = 5.0f;
	static float mouseSpeed = 1f;
	static float zoomSpeed = 5f;
	static float scrollDistance = 0.5f;
	static boolean init = false;
	static double lastTime;
	public static float deltaTime;
	
	public static void computeMatricesFromInputs(long window) {
		
		if (!init) {
			lastTime = glfwGetTime();
			init = true;
		}
		
		double currentTime = glfwGetTime();
		deltaTime = (float) ((float)currentTime - lastTime);
		
		Vector3f direction = new Vector3f(0, 0, -1);
		Vector3f right = new Vector3f(1, 0, 0);
		Vector3f dest = new Vector3f();
		Vector3f up = right.cross(direction, dest);
				
		
		dest = new Vector3f();
		if (glfwGetKey(window, GLFW_KEY_UP ) == GLFW_PRESS) {
			cameraPosition = cameraPosition.add(up.mul(deltaTime,dest).mul(cameraSpeed),dest);
		}
		dest = new Vector3f();
		if (glfwGetKey(window, GLFW_KEY_DOWN ) == GLFW_PRESS) {
			cameraPosition = cameraPosition.sub(up.mul(deltaTime,dest).mul(cameraSpeed),dest);
		}
		dest = new Vector3f();
		if (glfwGetKey(window, GLFW_KEY_RIGHT ) == GLFW_PRESS){
			cameraPosition = cameraPosition.add(right.mul(deltaTime,dest).mul(cameraSpeed),dest);
		}
		dest = new Vector3f();
		if (glfwGetKey(window, GLFW_KEY_LEFT ) == GLFW_PRESS){
			cameraPosition = cameraPosition.sub(right.mul(deltaTime,dest).mul(cameraSpeed),dest);
		}
		if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS){
			zoom += zoomSpeed*deltaTime;
		}
		if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS){
			zoom -= zoomSpeed*deltaTime;
		}
		
		
		Vector2f playerPos = Player.currentPlayer.transform.getPosition();
		dest = new Vector3f();
		Vector3f cPDistance = new Vector3f();
		if (Math.abs(playerPos.x - cameraPosition.x) > zoom/2 * scrollDistance) {
			cPDistance.x = playerPos.x - cameraPosition.x;
		}
		if (Math.abs(playerPos.y - cameraPosition.y) > zoom/2 * scrollDistance) {
			cPDistance.y = playerPos.y  - cameraPosition.y;
		}
		cPDistance.z = 0;
		dest = new Vector3f();
		if (cPDistance.x != 0) {
			cPDistance.x = cPDistance.x<0?-1:1;
			dest.x = 1;
		}
		if (cPDistance.y != 0) {
			cPDistance.y = cPDistance.y<0?-1:1;
			dest.y = 1;
		}
		Vector3f dest2 = new Vector3f(zoom/2 * scrollDistance).mul(dest);
		Vector3f distance = new Vector3f(playerPos, 0).sub(cameraPosition);
		distance.x = Math.abs(distance.x);
		distance.y = Math.abs(distance.y);
		cPDistance.mul(distance.sub(dest2));
		cameraPosition.add(cPDistance);
		
		IntBuffer pWidth = BufferUtils.createIntBuffer(1); // int*
		IntBuffer pHeight = BufferUtils.createIntBuffer(1); // int*

		glfwGetWindowSize(window, pWidth, pHeight);
		
		dest = new Vector3f();
		cameraPosition.add(direction,dest);
		ProjectionMatrix = new Matrix4f().orthoSymmetric(zoom*((float)pWidth.get(0)/pHeight.get(0)), zoom, 0.1f, 100);
		ViewMatrix       = new Matrix4f().lookAt(
				cameraPosition,           // Camera is here
				dest, // and looks here : at the same position, plus "direction"
				up                  // Head is up (set to 0,-1,0 to look upside-down)
		   );

		lastTime = currentTime;
	}
}
