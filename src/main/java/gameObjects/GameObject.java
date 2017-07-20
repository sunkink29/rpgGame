package gameObjects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.joml.*;
import org.lwjgl.BufferUtils;

import compoents.Component;
import compoents.Transform;
import defaultShapes.Shape;
import rendering.Shader;

public class GameObject {
	
	public String name;
	public String[] tags;
	public Transform transform = new Transform();
	public ArrayList<Component> components = new ArrayList<Component>();
	public GameObject[] childObjects;
	Vector3f color;
	int vao;
	int programId = -1;
	int matrixId;
	int wMatrixId;
	int colorId;
	UniformShaderProperty[] shaderProperties;
	
	public GameObject(Vector3f position, Vector3f color) {
		this(position, color, new Vector2f(), 0);
	}
	
	public GameObject(Vector3f position, Vector3f color, Vector2f scale) {
		this(position, color, scale, 0);
	}
	
	public GameObject(Vector3f position, Vector3f color, Vector2f scale, float rotation) {
		components.add(transform);
		transform.setPosition(position);
		transform.setScale(scale);
		transform.setRotation(rotation);;
		this.color = new Vector3f(color);
	}
	
	public GameObject(Vector3f position, Vector3f color, Vector2f scale, String programName, UniformShaderProperty[] shaderProperties) {
		this(position, color, scale);
		this.shaderProperties = shaderProperties;
		programId = Shader.getProgramId(programName);
	}
	
	public void init() {
		
	}
	
	public void initRenderer(Shape shape) {
		initRenderer(shape.getVAO(), shape.getProgramId());
	}
	
	public void initRenderer(int vao, int programID) {
		this.vao = vao;
		if (programId == -1)
			programId = programID;
		matrixId = glGetUniformLocation(programID, "MVP");
		wMatrixId = glGetUniformLocation(programID, "worldMatrix");
		colorId = glGetUniformLocation(programID, "color");
		if (shaderProperties != null) {
			for (UniformShaderProperty shaderProperty: shaderProperties){
				shaderProperty.ID = glGetUniformLocation(programID, shaderProperty.name);
			}
		}
	}
		
	public void renderObject(Matrix4f viewMatrix) {
		glUseProgram(programId);
		glBindVertexArray(vao);
		FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
		FloatBuffer positionMatrix = BufferUtils.createFloatBuffer(16);
		Matrix4f dest = new Matrix4f();
		positionMatrix = new Matrix4f().translate(transform.getPosition()).rotate(transform.getRotation(), new Vector3f(0, 0, 1)).scale(new Vector3f(transform.getScale(), 0)).get(positionMatrix);
		floatBuffer =  viewMatrix.get(floatBuffer);
		glUniformMatrix4fv(matrixId, false, floatBuffer);
		glUniformMatrix4fv(wMatrixId, false, positionMatrix);
		glUniform3f(colorId, color.x, color.y, color.z);
		if (shaderProperties != null) {
			for (UniformShaderProperty shaderProperty: shaderProperties) {
				if (shaderProperty.vector2fObject != null){
					FloatBuffer vector2floatBuffer = BufferUtils.createFloatBuffer(2);
					glUniform2fv(shaderProperty.ID, shaderProperty.vector2fObject.get(vector2floatBuffer));
				}
			}
		}
		glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);
	}
	
	public void update(Map map){}
	
}
