package main;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import gameObjects.Enemy;
import gameObjects.GameObject;
import gameObjects.Hallway;
import gameObjects.Map;
import gameObjects.Player;
import gameObjects.Room;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.stb.STBEasyFont;

import rendering.Controls;
import rendering.Model;

public class Main {

	public static long init() {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) {
			throw new IllegalStateException("Faild to initilize GLFW");
		}
		
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		glfwWindowHint(GLFW_SAMPLES, 8);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		long window = glfwCreateWindow(480*2, 300*2, "RPG Game", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
		
		glfwSetInputMode(window, GLFW_STICKY_KEYS, GL_TRUE);
		
		return window;
	}
	
	public static void main(String[] args) {
		long window = Main.init();
		
		GL.createCapabilities();
		
		glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		
		Vector2f[] enemyPath = {
				new Vector2f(0, 12),
				new Vector2f(12, 12),
				new Vector2f(12, 0),
				new Vector2f(12, 12),
				new Vector2f(0, 12),
				new Vector2f(0, 0)
		};
		
		Map map = new Map();
		map.rooms.add(new Room(new Vector3f(), new Vector2f(10)));
		map.rooms.get(0).enemies.add(new Enemy(new Vector3f(12, 0, 0), new Vector3f(0), new Vector3f(0.5f),5, enemyPath, 3));
		map.rooms.get(0).enemies.add(new Enemy(new Vector3f(12, 12, 0), new Vector3f(0), new Vector3f(0.5f),5, enemyPath, 2));
		map.rooms.get(0).enemies.add(new Enemy(new Vector3f(0, 12, 0), new Vector3f(0), new Vector3f(0.5f),5, enemyPath, 1));
		map.rooms.get(0).enemies.add(new Enemy(new Vector3f(0, 0, 0), new Vector3f(0), new Vector3f(0.5f),5, enemyPath, 0));
		map.rooms.add(new Room(new Vector3f(12, 0, 0), new Vector2f(10)));
		map.rooms.add(new Room(new Vector3f(-12, 12, 0), new Vector2f(10, 10)));
		map.rooms.add(new Room(new Vector3f(6, 12, 0), new Vector2f(22, 10)));
		map.rooms.add(new Hallway(new Vector3f(6, 0, 0), new Vector2f(3, 3), new Vector2f(1, 0)));
		map.rooms.add(new Hallway(new Vector3f(0, 6, 0), new Vector2f(3, 3), new Vector2f(0, 1)));
		map.rooms.add(new Hallway(new Vector3f(12, 6, 0), new Vector2f(3, 3), new Vector2f(0, 1)));
		map.rooms.add(new Hallway(new Vector3f(-6, 12, 0), new Vector2f(3, 3), new Vector2f(1, 0)));
		map.rooms.add(new Hallway(new Vector3f(-21, 12, 0), new Vector2f(9, 3), new Vector2f(1, 0)));
		map.rooms.add(new Hallway(new Vector3f(-21, 15, 0), new Vector2f(3, 3), new Vector2f(0, 1)));
		
		for (Room room: map.rooms) {
			room.init();
		}

		Player player = new Player();
		
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		
		int frames = 0;
		double lastfCountTime = glfwGetTime();
		do{
			frames++;
			double startTime = glfwGetTime();
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			Controls.computeMatricesFromInputs(window);
			Matrix4f mvp = Controls.getProjectionMatrix().mul(Controls.getViewMatrix());
			
			map.updateMap();
			map.renderMap(mvp);
			player.updatePlayer(window, map);
			player.renderPlayer(mvp);
		    glfwSwapBuffers(window);
		    glfwPollEvents();
		    while (glfwGetTime() - startTime < 1/60.0f){}
		    if (glfwGetTime() - lastfCountTime >= 3) {
		    	System.out.println(frames/(glfwGetTime() - lastfCountTime));
		    	lastfCountTime = glfwGetTime();
		    	frames = 0;
		    }
		} // Check if the ESC key was pressed or the window was closed
		while( glfwGetKey(window, GLFW_KEY_ESCAPE ) != GLFW_PRESS &&
		!glfwWindowShouldClose(window));
		
		glDeleteBuffers(1);
		glDeleteVertexArrays(1);
		
		glfwTerminate();
	}
}
