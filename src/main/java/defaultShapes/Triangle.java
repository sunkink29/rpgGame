package defaultShapes;

import rendering.Model;

public class Triangle extends Shape {
	
	static Triangle instance;
	
	public static Triangle getInstance() {
		if (instance == null) {
			instance = new Triangle();
		}
		return instance;
	}
	
	static float[] vertices = {
		     0.0f,  0.7f, // Top-left
		     0.5f, -0.5f, // Top-right
		    -0.5f, -0.5f, // Bottom-right
	};
	
	static int[] elements = {
		    2, 1, 0,
	};
	
	int[] getModelData() {
		return Model.getModelIds("triangle", "general", vertices, elements);
	}
}
