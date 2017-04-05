package gameObjects;

import java.util.ArrayList;

import org.joml.Matrix4f;

public class Map {
	
	public ArrayList<Room> rooms;
	
	public Map() {
		this(new ArrayList<Room>());
	}
	
	public Map(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}
	
	public void renderMap(Matrix4f viewMatrix) {
		for (int i = 0; i < rooms.size(); i++) {
			rooms.get(i).renderObject(viewMatrix);
		}
	}
	
	public void updateMap() {
		for (Room room: rooms) {
			room.updateRoom(this);
		}
	}

}
