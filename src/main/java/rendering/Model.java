package rendering;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_STICKY_KEYS;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public class Model {

	static ArrayList<ArrayList<String>> initializedModels = new ArrayList<ArrayList<String>>();
	
	public static int[] getModelIds(String name, String shader, float[] vertices, int[] indices) {
		for (int j = 0; j < initializedModels.size(); j++) {
			if (initializedModels.get(j).get(0).equals(name)) {
				int[] output = {Integer.parseInt(initializedModels.get(j).get(1)),Shader.getProgramId(shader)};
				return output;
			}
		}
		int programID = Shader.getProgramId(shader);
		System.out.println(programID);
		
		int vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		int ebo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
		
		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER,vertices, GL_STATIC_DRAW);
		
		int posAttrib = glGetAttribLocation(programID, "position");
		glEnableVertexAttribArray(posAttrib);
		glVertexAttribPointer(posAttrib, 2, GL_FLOAT, false, 0, 0);
		
		
		String[] list = {name,vao+""}; 
		List<String> temp = Arrays.asList(list);
		ArrayList<String> list2 = new ArrayList<String>();
		list2.addAll(temp);
		initializedModels.add(list2);
		
		int[] output = {vao, programID};
		
		return output;
	}
	
	public static void main(String[] args) {
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
		long window = glfwCreateWindow(480, 300, "Tutorial 01", NULL, NULL);
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
		
		GL.createCapabilities();
		
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		float vertices[] = {
			    -0.5f,  0.5f, // Top-left
			     0.5f,  0.5f, // Top-right
			     0.5f, -0.5f, // Bottom-right
			    -0.5f, -0.5f  // Bottom-left
		};
		
		int elements[] = {
			    0, 1, 2,
			    2, 3, 0
		};
		
		float vertices2[] = {
			     0.0f,  0.5f, // Top-left
			     0.5f, -0.5f, // Top-right
			    -0.5f, -0.5f, // Bottom-right
		};
		
		int elements2[] = {
			    2, 1, 0,
		};
		
		int[] model = getModelIds("backGround", "general", vertices, elements);
		int vao1 = model[0];
		int programID = model[1];
		
		int[] model2 = getModelIds("player", "general", vertices2, elements2);
		int vao2 = model2[0];
		
		int matrixId = glGetUniformLocation(programID, "MVP");
		int colorId = glGetUniformLocation(programID, "color");
		
//		glEnable(GL_DEPTH_TEST);
//		glDepthFunc(GL_LESS);
		glUseProgram(programID);
		System.out.println(colorId);
		do{
			Controls.computeMatricesFromInputs(window);
			Matrix4f mvp = Controls.getProjectionMatrix().mul(Controls.getViewMatrix());
			FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
			floatBuffer =  mvp.get(floatBuffer);
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			// Draw the triangle !
//			glDrawArrays(GL_TRIANGLES, 0, 36); // Starting from vertex 0; 3 vertices total -> 1 triangle
			glBindVertexArray(vao1);
			glUniformMatrix4fv(matrixId, false, floatBuffer);
			glUniform3f(colorId, 0, 0, 0);
			glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);
			
			Matrix4f dest = new Matrix4f();
			floatBuffer =  mvp.scale(0.8f).get(floatBuffer);
			glBindVertexArray(vao1);
			glUniformMatrix4fv(matrixId, false, floatBuffer);
			glUniform3f(colorId, 1, 1, 1);
			glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);
			
			glBindVertexArray(vao2);
			floatBuffer =  mvp.scale(0.1f).get(floatBuffer);
			glUniformMatrix4fv(matrixId, false, floatBuffer);
			glUniform3f(colorId, 0, 0, 0);
			glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);

		    glfwSwapBuffers(window);
		    glfwPollEvents();

		} // Check if the ESC key was pressed or the window was closed
		while( glfwGetKey(window, GLFW_KEY_ESCAPE ) != GLFW_PRESS &&
		!glfwWindowShouldClose(window));
		
		glDeleteBuffers(1);
		glDeleteVertexArrays(1);
		glDeleteProgram(programID);
		
		glfwTerminate();
	}
}
