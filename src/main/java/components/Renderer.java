package components;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform2fv;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import defaultShapes.Shape;
import gameObjects.GameObject;
import gameObjects.USP;

public class Renderer extends Component {
	
	private Vector3f color = new Vector3f(0);
	private int vao;
	private int programId;
	private int matrixId;
	private int wMatrixId;
	private int colorId;
	private USP[] shaderProperties;
	
	public Vector3f getColor() {return new Vector3f(color);}
	public void setColor(Vector3f newColor) {if (!newColor.equals(color)) color = new Vector3f(newColor);}
	
	public int getVao() {return vao;}
	public void setVao(int newVao) {vao = newVao;}
	
	public int getProgramId() {return programId;}
	public void setProgramId(int newProgramId) {programId = newProgramId; init();}
	
	public void setVaoAndProgramId(Shape shape) {setVaoAndProgramId(shape.getVAO(), shape.getProgramId());}
	public void setVaoAndProgramId(int newVao, int newProgramId) {setVao(newVao);setProgramId(newProgramId);}
	
	public int getMatrixId() {return matrixId;}
	public void setMatrixId(int newMatrixId) {matrixId = newMatrixId;}
	
	public int getWMatrixId() {return wMatrixId;}
	public void setWMatrixId(int newWMatrixId) {wMatrixId = newWMatrixId;}
	
	public int getColorId() {return colorId;}
	public void setColorId(int newColorId) {colorId = newColorId;}
	
	private USP[] copyShaderProperties(USP[] input) {USP[] output = new USP[input.length];
		for (int i=0;i<input.length;i++) {output[i] = new USP(input[i]);}return output;}
	public USP[] getShaderProperties() {return copyShaderProperties(shaderProperties);}
	public void setShaderProperties(USP[] newShaderProperties) {shaderProperties = copyShaderProperties(newShaderProperties); init();}
	
	public void init() {
		setMatrixId(glGetUniformLocation(getProgramId(), "MVP"));
		setWMatrixId(glGetUniformLocation(getProgramId(), "worldMatrix"));
		setColorId(glGetUniformLocation(getProgramId(), "color"));
		if (shaderProperties != null) {
			for (USP shaderProperty: shaderProperties){
				shaderProperty.ID = glGetUniformLocation(getProgramId(), shaderProperty.name);
			}
		}
	}
	
	public void render(Matrix4f viewMatrix) {
		glUseProgram(getProgramId());
		glBindVertexArray(getVao());
		FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
		FloatBuffer positionMatrix = BufferUtils.createFloatBuffer(16);
		Matrix4f dest = new Matrix4f();
		GameObject gameObject = getGameObject();
		positionMatrix = new Matrix4f().translate(gameObject.transform.getPosition()).rotate(gameObject.transform.getRotation(), new Vector3f(0, 0, 1)).scale(new Vector3f(gameObject.transform.getScale(), 0)).get(positionMatrix);
		floatBuffer =  viewMatrix.get(floatBuffer);
		glUniformMatrix4fv(getMatrixId(), false, floatBuffer);
		glUniformMatrix4fv(getWMatrixId(), false, positionMatrix);
		glUniform3f(getColorId(), color.x, color.y, color.z);
		if (shaderProperties != null) {
			for (USP shaderProperty: shaderProperties) {
				if (shaderProperty.vector2fObject != null){
					FloatBuffer vector2floatBuffer = BufferUtils.createFloatBuffer(2);
					glUniform2fv(shaderProperty.ID, shaderProperty.vector2fObject.get(vector2floatBuffer));
				}
			}
		}
		glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);
	}
}
