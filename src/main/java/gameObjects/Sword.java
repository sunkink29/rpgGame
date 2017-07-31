package gameObjects;

import components.Collider;
import components.Transform;

public class Sword extends GameObject {
	
	public int damageObjects; // a bit field that specifys what the sword damages
	public int damage = 1;

	public Sword(Transform transform, int damageObjs) {
		super(transform, defaultShapes.Triangle.getInstance());
		damageObjects = damageObjs;
		addComponent(new Collider(false, true, CollisionObjs.SWORD));
	}
	
	public Sword(Transform transform, int damageObjs, int damage) {
		this(transform, damageObjs);
		this.damage = damage;
	}
	
	public void objectCollided(Collider otherObject) {
		if ((otherObject.collisionObjType & damageObjects) != 0 &&
				otherObject.getGameObject() instanceof Damageable) {
			Damageable otherGameObject = (Damageable) otherObject.getGameObject();
			otherGameObject.damageObject(damage);
		}
	}
}
