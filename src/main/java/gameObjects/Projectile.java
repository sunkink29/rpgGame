package gameObjects;

import org.joml.*;

import components.Collider;
import components.Transform;
import defaultShapes.Shape;
import defaultShapes.Square;
import rendering.Controls;

public class Projectile extends GameObject {
	public float speed = 3;
	public Map map;
	int startCollision = 2;
	Vector3f targetDirection = new Vector3f(0, 1, 0);
	
	public Projectile(Transform transform) {
		super(transform);
	}
	
	public Projectile(Transform transform, Shape shape) {
		super(transform, shape);
		addComponent(new Collider(false, true));
	}
	
	public Projectile(Transform transform, Shape shape, Vector3f targetDirection) {
		this(transform, shape);
		this.targetDirection = targetDirection;
	}
	
	@Override
	public void init() {
	}

	@Override
	public void update(Map map) {
		super.update(map);
		Vector3f targetDirection = new Vector3f(this.targetDirection);
		Vector3f newPos = transform.getPosition().add(targetDirection.mul(Controls.deltaTime).mul(speed));
		transform.setPosition(newPos);
		this.map = map;
		if (startCollision > 0) {
			startCollision--;
		}
//		Vector3f collision = Collision.isGameObjectColliding(map, this);
//		if (!collision.equals(new Vector3f())) {
//			map.removeObject(this);
//		}
	}
	
	@Override
	public void objectCollided(Collider otherObject) {
		if (startCollision > 0) {
			startCollision++;
		} else {
			map.removeObject(this);
			Collider type = new Collider(false);
			Collider collider = getComponent(type);
			map.removeCollider(collider);
		}
	}
}
