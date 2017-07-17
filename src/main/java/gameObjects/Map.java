package gameObjects;

import java.util.ArrayList;

import org.joml.Matrix4f;

public class Map {
	
	ArrayList<Room> rooms;
	ArrayList<GameObject> objects = new ArrayList<GameObject>();
	ArrayList<GameObject> addObjects = new ArrayList<GameObject>();
	ArrayList<GameObject> removeObjects = new ArrayList<GameObject>();
	
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
			room.updateRoom(this);
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
	}

}
