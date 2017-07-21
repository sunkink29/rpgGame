package gameObjects;

import org.joml.*;

import components.Transform;
import defaultShapes.Shape;
import defaultShapes.Square;
import rendering.Controls;

public class Projectile extends GameObject {
	public float speed = 3;
	
	public Projectile(Transform transform) {
		super(transform);
	}
	
	public Projectile(Transform transform, Shape shape) {
		super(transform, shape);
	}

	@Override
	public void update(Map map) {
		Vector3f targetDirection = new Vector3f(0, 1, 0);
		Vector3f newPos = transform.getPosition().add(targetDirection.mul(Controls.deltaTime).mul(speed));
		transform.setPosition(newPos);
		Vector3f collision = Collision.isGameObjectColliding(map, this);
		if (!collision.equals(new Vector3f())) {
			map.removeObject(this);
		}
	}

}
