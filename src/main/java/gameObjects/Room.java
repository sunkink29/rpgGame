package gameObjects;

import java.util.ArrayList;
import java.util.Iterator;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import rendering.Model;

public class Room extends GameObject {
	
	Vector2f size;
	GameObject floor;
	GameObject wall;

	public Room(Vector3f position, float length, float width) {
		this(position, new Vector2f(length, width));
	}
	
	public Room(Vector3f position, Vector2f size){
		super(position, new Vector3f());
		this.size = new Vector2f(size);
		position.z = 1;
	}
	
	public void init() {
		wall = new GameObject(transform.getPosition(), new Vector3f(0), size);
		Vector3f floorPos = transform.getPosition();
		floorPos.z += -0.1f;
		floor = new GameObject(floorPos, new Vector3f(0.8f), new Vector2f(size).sub(1, 1));
		wall.initRenderer(defaultShapes.Square.getInstance());
		floor.initRenderer(defaultShapes.Square.getInstance());
	};
	
	@Override
	public void renderObject(Matrix4f viewMatrix){
		wall.renderObject(viewMatrix);
		floor.renderObject(viewMatrix);
	}
	
	public boolean isHallway(){
		return false;
	}
	
	public void updateRoom(Map map){
		
	}

}
