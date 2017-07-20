package gameObjects;

import org.joml.*;

import rendering.Controls;

public class Projectile extends GameObject {
	
	public float speed = 3;
	
	public Projectile(Vector3f position, Vector3f color, Vector2f scale,
			float rotation) {
		super(position, color, scale, rotation);
	}
	
	@Override
	public void update(Map map) {
		Vector3f targetDirection = new Vector3f(0, 1, 0);
		Vector3f newPos = transform.getPosition().add(targetDirection.mul(Controls.deltaTime).mul(speed));
		transform.setPosition(newPos);
		Vector3f collision = Collision.isGameObjectColliding(map, this);
//		System.out.println(collision);
		if (!collision.equals(new Vector3f())) {
			map.removeObject(this);
		}
	}

}
