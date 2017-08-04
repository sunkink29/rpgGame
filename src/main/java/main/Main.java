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
import gameObjects.Projectile;
import gameObjects.ProjectileLauncher;
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

import components.Collider;
import components.Transform;

import org.lwjgl.stb.STBEasyFont;

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
		map.addRoom(new Room(new Vector2f(), new Vector2f(10)));
		map.addRoom(new Room(new Vector2f(12, 0), new Vector2f(10)));
		map.addRoom(new Room(new Vector2f(-12, 12), new Vector2f(10, 10)));
		map.addRoom(new Room(new Vector2f(6, 12), new Vector2f(22, 10)));
		map.addRoom(new Hallway(new Vector2f(6, 0), new Vector2f(3, 3), new Vector2f(1, 0)));
		map.addRoom(new Hallway(new Vector2f(0, 6), new Vector2f(3, 3), new Vector2f(0, 1)));
		map.addRoom(new Hallway(new Vector2f(12, 6), new Vector2f(3, 3), new Vector2f(0, 1)));
		map.addRoom(new Hallway(new Vector2f(-6, 12), new Vector2f(3, 3), new Vector2f(1, 0)));
		map.addRoom(new Hallway(new Vector2f(-21, 12), new Vector2f(9, 3), new Vector2f(1, 0)));
		map.addRoom(new Hallway(new Vector2f(-21, 15), new Vector2f(3, 3), new Vector2f(0, 1)));
		map.addObject(new Enemy(new Vector2f(12, 0), new Vector2f(0.5f),5, enemyPath, 3));
//		map.addObject(new Enemy(new Vector3f(12, 12, 0), new Vector2f(0.5f),5, enemyPath, 2));
//		map.addObject(new Enemy(new Vector3f(0, 12, 0), new Vector2f(0.5f),5, enemyPath, 1));
//		map.addObject(new Enemy(new Vector3f(0, 0, 0), new Vector2f(0.5f),5, enemyPath, 0));
//		map.addObject(new Enemy(new Vector3f(0, 0, 0), new Vector2f(0.5f),5));
//		map.addObject(new ProjectileLauncher(new Vector2f(0f, 0), new Vector3f(1)));
//		map.addObject(new ProjectileLauncher(new Vector3f(-12, 12, -1), new Vector3f(1), new Vector3f(1,0,0)));
		
		Player player = new Player(new Vector2f(), 0);
		
		Input.initalizeInput(window);
		Input.addButtonBinding("forwardsMove", GLFW_KEY_W);
		Input.addButtonBinding("backwardsMove", GLFW_KEY_S);
		Input.addButtonBinding("leftMove", GLFW_KEY_A);
		Input.addButtonBinding("rightMove", GLFW_KEY_D);
		Input.addButtonBinding("leftRot", GLFW_KEY_J);
		Input.addButtonBinding("rightRot", GLFW_KEY_L);
		Input.addButtonBinding("attack", GLFW_KEY_K);
		Input.addButtonBinding("printPlayerPos", GLFW_KEY_F);
		
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		
		int frames = 0;
		double lastfCountTime = glfwGetTime();
		do{
			frames++;
			double startTime = glfwGetTime();
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		    glfwPollEvents();
			
			Controls.computeMatricesFromInputs(window);
			Matrix4f mvp = Controls.getProjectionMatrix().mul(Controls.getViewMatrix());
			
			player.updatePlayer(window, map);
			map.updateMap();
			Collider.checkCollisions(map);
			player.renderPlayer(mvp);
			map.renderMap(mvp);
		    glfwSwapBuffers(window);
//		    while (glfwGetTime() - startTime < 1/30.0f){}
		    if (glfwGetTime() - lastfCountTime >= 3) {
		    	System.out.println(frames/(glfwGetTime() - lastfCountTime));
		    	lastfCountTime = glfwGetTime();
		    	frames = 0;
		    }
		} // Check if the ESC key was pressed or the window was closed
		while( glfwGetKey(window, GLFW_KEY_ESCAPE ) != GLFW_PRESS &&
		!glfwWindowShouldClose(window));
		
		glfwTerminate();
	}
}
