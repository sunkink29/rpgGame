package components;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

import gameObjects.GameObject;
import gameObjects.Hallway;
import gameObjects.Map;
import gameObjects.Room;

public class Collider extends Component {
	
	public boolean staticObject;
	public boolean isTrigger;
	public short collisionObjType;
	ArrayList<Collider> dynCollisionObjs = new ArrayList<Collider>();
	Vector2f ignoreDirection = new Vector2f(1);
	boolean pushedByWall = false;
	boolean addedToMap = false;
	
	public Collider(boolean staticObject, short collisionObjType) {
		this.staticObject = staticObject;
		this.collisionObjType = collisionObjType;
	}
	
	public Collider(boolean staticObject, boolean isTrigger, short collisionObjType) {
		this(staticObject, collisionObjType);
		this.isTrigger = isTrigger;
	}
	
	@Override
	public void update(Map map) {
		if (!addedToMap) {
			map.addCollider(this);
			addedToMap = true;
		}
	}
	
	public static void checkCollisions (Map map) {
		Collider[] dynColliders = map.getdynColliders();
		Collider[] colliders = map.getColliders();
		for (int i=0; i<dynColliders.length; i++) {
			ArrayList<Collider> collisions = new ArrayList<Collider>();
			Collider obj1 = dynColliders[i];
			Transform obj1Trans = obj1.getGameObject().transform;
			obj1.pushedByWall = false;
			obj1.ignoreDirection = new Vector2f(1);
			for (int j=i+1; j<colliders.length;j++) {
				Collider obj2 = colliders[j];
				if (obj2.getGameObject().getClass() == Room.class || obj2.getGameObject().getClass() == Hallway.class) {
					Room room = (Room)obj2.getGameObject();
					if(checkIfTransformsCollide(obj1.getGameObject().transform, room.wall.transform)) {
						Vector3f posDiff = obj1Trans.getPosition().sub(obj2.getGameObject().transform.getPosition());
						Vector2f obj1Scale = obj1Trans.getScale().mul(0.5f);
						Vector3f movementTemp = new Vector3f(obj1Scale.x, obj1Scale.x,0).add(Math.abs(posDiff.x), Math.abs(posDiff.y), 0);
						Vector2f movement = new Vector2f(movementTemp.x, movementTemp.y).sub(room.floor.transform.getScale().mul(0.5f));
						if (movement.x>0 || movement.y>0) { // if obj1 is not completely within the boundary of obj2 floor
							collisions.add(obj2);
						}
					}
					if (obj2.getGameObject().getClass() == Hallway.class) {
						Hallway hallway = (Hallway) obj2.getGameObject();
						if (checkIfTransformsCollide(obj1.getGameObject().transform, hallway.floor.transform)) {
							obj1.ignoreDirection = new Vector2f(hallway.direction).perpendicular();
						}
					}
				} else if (checkIfTransformsCollide(obj1.getGameObject().transform, obj2.getGameObject().transform)) {
					Transform obj2Trans = obj2.getGameObject().transform;
					Vector3f posDiff = obj1Trans.getPosition().sub(obj2Trans.getPosition());
					Vector2f movementTemp = obj1Trans.getScale().mul(0.5f).add(obj2Trans.getScale().mul(0.5f));
					Vector3f movement = new Vector3f(movementTemp.x, movementTemp.y, 0).sub(Math.abs(posDiff.x),Math.abs(posDiff.y), 0);
					Collider objMoved = null;
					if (Math.abs(posDiff.x) > Math.abs(posDiff.y)) {
						movement.mul(Math.copySign(1, posDiff.x), 0, 0);
					} else {
						movement.mul(0, Math.copySign(1, posDiff.y), 0);
					}
					if (!obj2.staticObject) {
						if (obj1Trans.getScale().length() > obj2Trans.getScale().length()) {
							objMoved = obj2;
							movement.mul(-1);
						} else if (obj1Trans.getScale().length() <= obj2Trans.getScale().length()) {
							objMoved = obj1;
						}
						obj1.dynCollisionObjs.add(obj2);
					} else {
						objMoved = obj1;
					}
					if (!obj1.isTrigger && !obj2.isTrigger) {
						objMoved.getGameObject().transform.setPosition(objMoved.getGameObject().transform.getPosition().add(movement));
					}
					obj1.objectCollided(obj2);
					obj2.objectCollided(obj1);
				}
			}
			for (int j=collisions.size()-1; j>=0; j--) {
				Collider obj2 = collisions.get(j);
				Room room = (Room) obj2.getGameObject();
				Transform obj2Trans = obj2.getGameObject().transform;
				Vector3f posDiff = obj1Trans.getPosition().sub(obj2Trans.getPosition());
				Vector2f obj1Scale = obj1Trans.getScale().mul(0.5f);
				Vector3f movementTemp = new Vector3f(obj1Scale.x, obj1Scale.y,0).add(Math.abs(posDiff.x), Math.abs(posDiff.y), 0);
				Vector2f movement = new Vector2f(movementTemp.x, movementTemp.y).sub(room.floor.transform.getScale().mul(0.5f));
				if (movement.x < 0) {
					movement.x = 0;
				} else if (movement.y < 0) {
					movement.y = 0;
				}
				movement.mul(obj1.ignoreDirection);
				Vector3f newMovement = new Vector3f(Math.copySign(movement.x, posDiff.x), Math.copySign(movement.y, posDiff.y), 0);
				if (!obj1.isTrigger) {
					obj1Trans.setPosition(obj1Trans.getPosition().sub(newMovement));
				}
				obj1.pushedByWall = true;
				if (movement.x != 0 || movement.y != 0) {
					obj1.objectCollided(obj2);
					obj2.objectCollided(obj1);
				}
			}
		}
		for (int i=0; i <dynColliders.length; i++) {
			Collider obj1 = dynColliders[i];
			for (int j=0; j < obj1.dynCollisionObjs.size(); j++) {
				Transform obj1Trans = obj1.getGameObject().transform;
				Collider obj2 = obj1.dynCollisionObjs.get(j);
				Transform obj2Trans = obj2.getGameObject().transform;
				if (checkIfTransformsCollide(obj1Trans, obj2Trans)) {
					Vector3f posDiff = obj1Trans.getPosition().sub(obj2Trans.getPosition());
					Vector2f movementTemp = obj1Trans.getScale().mul(0.5f).add(obj2Trans.getScale().mul(0.5f));
					Vector3f movement = new Vector3f(movementTemp.x, movementTemp.y, 0).sub(Math.abs(posDiff.x),Math.abs(posDiff.y), 0);
					if (Math.abs(posDiff.x) > Math.abs(posDiff.y)) {
						movement.mul(1, 0, 0);
					} else {
						movement.mul(0, 1, 0);
					}
					Transform objMoved = new Transform();
					if (obj1.pushedByWall) {
						objMoved = obj2Trans;
					} else {
						objMoved = obj1Trans;
						movement.mul(-1);
					}
					if (!obj1.isTrigger && !obj2.isTrigger) {
						objMoved.setPosition(objMoved.getPosition().add(movement));
					}
				}
			}
		}
	}
	
	public static boolean checkIfTransformsCollide(Transform object1, Transform object2) {
		Vector3f posDiff = object1.getPosition().sub(object2.getPosition());
		Vector2f movementTemp = object1.getScale().mul(0.5f).add(object2.getScale().mul(0.5f));
		Vector3f movement = new Vector3f(movementTemp.x, movementTemp.y, 0).sub(Math.abs(posDiff.x),Math.abs(posDiff.y), 0);
		return movement.x > 0 && movement.y > 0;
	}
	
	public void objectCollided(Collider otherObject) {
		getGameObject().objectCollided(otherObject);
	}
}

/* check collision method outline
 * 		class variable to hold collision move distance cdistance
 * 		class variable to hold non-static collision obj dynObj
 * illiterate through a list of non-static objects; current is obj1
 * 		variable to store all object collisions
 * 		illiterate through a list of the same objects followed by static objects followed by rooms; current is obj2
 * 			start variable of second loop is obj1 + 1
 * 			see if obj1 collides with obj2
 * 			if so add obj2 to a list outsite of the second loop
 * 				if boundry ouside room floor but part is inside room wall add to list
 * 					else if obj2 boundry is inside the floor and is a hallway
 * 						flag to ignore any other collison with rooms in a direction
 * 				if inside hallway wall add to list
 * 				if not hallway call objectCollied with other object
 *		illiterate through the list of object collisions from the end to the beginning
 *			if collision obj is a room then calculate amount to move the object to be completly inside the floor object
 *				add it to cdistance
 *				mark that it was pushed by a wall and static object
 *			if collision obj is static then compute the distance to not collide with object add to cdistance
 *				mark that it was pushed by a static object
 *			if collision obj is non-static add obj to dynObj
 * Illiterate through list of non-static objects
 * 		if obj has non-static collisions
 * 			which ever one is smaller
 * 				if small is not colliding with static object
 * 					add distance to not collide with bigger object to small object cdistance
 * 				if small is colliding with static object
 * 					add distance to not collide with smaller object to bigger object cdistance
 * Illiterate through list of non-static objects
 * 		if obj has a non zero cdistance
 * 			add to position
 * 			zero cdistance
 */

