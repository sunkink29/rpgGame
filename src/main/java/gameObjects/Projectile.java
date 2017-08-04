package gameObjects;

import org.joml.*;

import components.Collider;
import components.Transform;
import defaultShapes.Shape;
import main.Controls;

public class Projectile extends GameObject {
	public float speed = 3;
	public Map map;
	int startCollision = 2;
	Vector2f targetDirection = new Vector2f(0, 1);
	
	public Projectile(Transform transform) {
		super(transform);
	}
	
	public Projectile(Transform transform, Shape shape) {
		super(transform, shape);
		addComponent(new Collider(false, true, CollisionObjs.PROJECTILE));
	}
	
	public Projectile(Transform transform, Shape shape, Vector2f targetDirection) {
		this(transform, shape);
		this.targetDirection = targetDirection;
	}
	
	@Override
	public void init() {
	}

	@Override
	public void update(Map map) {
		super.update(map);
		Vector2f targetDirection = new Vector2f(this.targetDirection);
		Vector2f newPos = transform.getPosition().add(targetDirection.mul(Controls.deltaTime).mul(speed));
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
			Collider type = new Collider(true, CollisionObjs.STATICOBJECT);
			Collider collider = getComponent(type);
			map.removeCollider(collider);
		}
	}
}
