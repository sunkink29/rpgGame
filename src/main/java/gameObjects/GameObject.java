package gameObjects;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

import java.nio.FloatBuffer;

import main.Main;

import org.joml.*;
import org.lwjgl.BufferUtils;

import rendering.Controls;
import rendering.Model;
import rendering.Shader;


public class GameObject {
	
	public Vector3f position;
	Vector3f color;
	Vector3f scale;
	float rotation;
	int vao;
	int programId;
	int matrixId;
	int colorId;
	
	public GameObject(Vector3f position, Vector3f color) {
		this(position, color, new Vector3f(), 0);
	}
	
	public GameObject(Vector3f position, Vector3f color, Vector3f scale) {
		this(position, color, scale, 0);
	}
	
	public GameObject(Vector3f position, Vector3f color, Vector3f scale, float rotation) {
		this.position = new Vector3f(position);
		this.color = new Vector3f(color);
		this.scale = scale;
		this.rotation = rotation;
	}
	
	public void init(int vao, int programID) {
		this.vao = vao;
		this.programId = programID;
		matrixId = glGetUniformLocation(programID, "MVP");
		colorId = glGetUniformLocation(programID, "color");
	}
	
	public void renderObject(Matrix4f viewMatrix) {
		glUseProgram(programId);
		glBindVertexArray(vao);
		FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
		Matrix4f dest = new Matrix4f();
		floatBuffer =  viewMatrix.translate(position,dest).rotate(rotation, new Vector3f(0, 0, 1)).scale(scale).get(floatBuffer);
		glUniformMatrix4fv(matrixId, false, floatBuffer);
		glUniform3f(colorId, color.x, color.y, color.z);
		glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);
	}
	
}
