package defaultShapes;

import rendering.Model;

public class Square extends Shape {
	
	static Square instance;
	
	public static Square getInstance() {
		if (instance == null) {
			instance = new Square();
		}
		return instance;
	}
	
	static float[] vertices = {
		    -0.5f,  0.5f, // Top-left
		     0.5f,  0.5f, // Top-right
		     0.5f, -0.5f, // Bottom-right
		    -0.5f, -0.5f  // Bottom-left
	};
	
	static int[] elements = {
		    0, 1, 2,
		    2, 3, 0
	};
	
	int[] getModelData() {
		return Model.getModelIds("square", "general", vertices, elements);
	}
}
