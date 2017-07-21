package gameObjects;

import org.joml.*;

public class USP {

	public String name;
	public int ID;
	public Vector2f vector2fObject;
	
	public USP(USP object) {
		this(object.name, object.vector2fObject);
	}
	
	public USP(String name, Vector2f property) {
		this.name = name;
		vector2fObject = new Vector2f(property);
	}
}
