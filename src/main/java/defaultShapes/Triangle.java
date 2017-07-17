package defaultShapes;

import rendering.Model;

public class Triangle extends Shape {
	
	static Triangle instance;
	
	static float vertices[] = {
		     0.0f,  0.7f, // Top-left
		     0.5f, -0.5f, // Top-right
		    -0.5f, -0.5f, // Bottom-right
	};
	
	static int elements[] = {
		    2, 1, 0,
	};
	
	static int vao = -1;
	static int programId = -1;
	
	public int getVAO() {
		if (vao == -1) {
			int[] triangle = Model.getModelIds("triangle", "general", vertices, elements);
			vao = triangle[0];
			programId = triangle[1];
		}
		return vao;
	}
	
	public int getProgramId() {
		if (vao == -1) {
			int[] triangle = Model.getModelIds("triangle", "general", vertices, elements);
			vao = triangle[0];
			programId = triangle[1];
		}
		return programId;
	}
	
	public static Triangle getInstance() {
		if (instance == null) {
			instance = new Triangle();
		}
		return instance;
	}

}
