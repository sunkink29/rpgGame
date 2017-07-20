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
		Vector3f newPos = transform.getPosition();
		newPos.z = -0.2f;
		transform.setPosition(newPos);
		super.init();
		Vector2f scale = new Vector2f(4, 4).mul(direction);
		Vector2f floorScale = floor.transform.getScale();
		scale.x += floorScale.x;
		scale.y += floorScale.y;
		floor.transform.setScale(scale);
	}

	@Override
	public boolean isHallway() {
		return true;
	}
}
