package gameObjects;

import org.joml.*;

public class UniformShaderProperty {

	public String name;
	public int ID;
	public Vector2f vector2fObject;
	
	public UniformShaderProperty(String name, Vector2f property) {
		this.name = name;
		vector2fObject = property;
	}
}
