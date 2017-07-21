package gameObjects;

import java.util.ArrayList;
import java.util.Iterator;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import components.Transform;

public class Room extends GameObject {
	
	Vector2f size;
	GameObject floor;
	GameObject wall;

	public Room(Vector3f position, float length, float width) {
		this(position, new Vector2f(length, width));
	}
	
	public Room(Vector3f position, Vector2f size){
		super(new Transform(position, new Vector2f(1), 0));
		this.size = new Vector2f(size);
		position.z = 1;
	}
	
	public void init() {
		wall = new GameObject(new Transform(transform.getPosition(), size, 0), defaultShapes.Square.getInstance());
		Vector3f floorPos = transform.getPosition();
		floorPos.z += -0.1f;
		floor = new GameObject(new Transform(floorPos, new Vector2f(size).sub(1,1), 0), defaultShapes.Square.getInstance());
		floor.renderer.setColor(new Vector3f(0.8f));
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
