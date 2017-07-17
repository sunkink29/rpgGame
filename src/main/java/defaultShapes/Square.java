package defaultShapes;

import rendering.Model;

public class Square extends Shape {
	
	static Square instance;
	
	static float vertices[] = {
		    -0.5f,  0.5f, // Top-left
		     0.5f,  0.5f, // Top-right
		     0.5f, -0.5f, // Bottom-right
		    -0.5f, -0.5f  // Bottom-left
	};
	
	static int elements[] = {
		    0, 1, 2,
		    2, 3, 0
	};
	
	static int vao = -1;
	static int programId = -1;
	
	@Override
	public int getVAO() {
		if (vao == -1) {
			int[] square = Model.getModelIds("square", "general", vertices, elements);
			vao = square[0];
			programId = square[1];
		}
		return vao;
	}

	@Override
	public int getProgramId() {
		if (programId == -1) {
			int[] square = Model.getModelIds("square", "general", vertices, elements);
			vao = square[0];
			programId = square[1];
		}
		return programId;
	}
	
	public static Square getInstance() {
		if (instance == null) {
			instance = new Square();
		}
		return instance;
	}

}
