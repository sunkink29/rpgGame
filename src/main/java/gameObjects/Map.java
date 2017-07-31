package gameObjects;

import java.util.ArrayList;

import org.joml.Matrix4f;

import components.Collider;

public class Map {
	
	ArrayList<Room> rooms;
	ArrayList<GameObject> objects = new ArrayList<GameObject>();
	ArrayList<GameObject> addObjects = new ArrayList<GameObject>();
	ArrayList<GameObject> removeObjects = new ArrayList<GameObject>();
	ArrayList<Collider> staticObjects = new ArrayList<Collider>();
	ArrayList<Collider> dynObjects = new ArrayList<Collider>();
	
	public Map() {
		this(new ArrayList<Room>());
	}
	
	public Map(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}
	
	public void renderMap(Matrix4f viewMatrix) {
		for (Room room: rooms) {
			room.renderObject(viewMatrix);
		}
		for (GameObject object: objects) {
			object.renderObject(viewMatrix);
		}
	}
	
	public void updateMap() {
		for (Room room: rooms) {
			room.update(this);
		}
		for (GameObject object: objects){
			object.update(this);
		}
		objects.addAll(addObjects);
		objects.removeAll(removeObjects);
		addObjects = new ArrayList<GameObject>();
		removeObjects = new ArrayList<GameObject>();
	}
	
	public void addRoom(Room room) {
		rooms.add(room);
		room.init();
	}
	
	public void removeRoom(Room room) {
		rooms.remove(room);
	}
	
	public void addObject(GameObject object) {
		addObjects.add(object);
		object.init();
	}
	
	public void removeObject(GameObject object) {
		removeObjects.add(object);
		Collider type = new Collider(true, CollisionObjs.STATICOBJECT);
		Collider collider = object.getComponent(type);
		if (collider == null) {
			removeCollider(collider);
		}
	}
	
	public Collider[] getdynColliders() {
		Collider[] output = new Collider[dynObjects.size()];
		return (Collider[]) dynObjects.toArray(output);
	}
	
	public Collider[] getColliders() {
		ArrayList<Collider> colliders = new ArrayList<Collider>();
		colliders.addAll(dynObjects);
		colliders.addAll(staticObjects);
		Collider[] output = new Collider[colliders.size()];
		return (Collider[]) colliders.toArray(output);
	}
	
	public void addCollider(Collider collider) {
		if (collider.staticObject) {
			staticObjects.add(collider);
		} else {
			dynObjects.add(collider);
		}
	}
	
	public void removeCollider(Collider collider) {
		if (collider.staticObject) {
			staticObjects.remove(collider);
		} else {
			dynObjects.remove(collider);
		}
	}

}
