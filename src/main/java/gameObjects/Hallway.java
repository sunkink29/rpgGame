package gameObjects;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Hallway extends Room {
	
	public Vector2f direction;

	public Hallway(Vector3f position, Vector2f size, Vector2f direction) {
		super(position, size);
		this.direction = direction;
	}
	public Hallway(Vector3f position, float length, float width) {
		super(position, length, width);
	}
	
	@Override
	public void init() {
		super.init();
		Vector2f scale = new Vector2f(4, 4).mul(direction);
		floor.scale.add(new Vector3f(scale, 0));
	}

	@Override
	public boolean isHallway() {
		return true;
	}
}
