package gameObjects;

import java.util.ArrayList;
import java.util.Iterator;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import rendering.Model;

public class Room extends GameObject {
	
	Vector2f size;
	GameObject floor;
	GameObject wall;
	public ArrayList<GameObject> objects = new ArrayList<GameObject>();
	public ArrayList<GameObject> addObjects = new ArrayList<GameObject>();
	public ArrayList<GameObject> removeObjects = new ArrayList<GameObject>();
	public ArrayList<Enemy> enemies = new ArrayList<Enemy>();

	public Room(Vector3f position, float length, float width) {
		this(position, new Vector2f(length, width));
	}
	
	public Room(Vector3f position, Vector2f size){
		super(position, new Vector3f());
		this.size = new Vector2f(size);
		position.z = 1;
	}
	
	public void init() {
		float vertices[] = {
			    -0.5f,  0.5f, // Top-left
			     0.5f,  0.5f, // Top-right
			     0.5f, -0.5f, // Bottom-right
			    -0.5f, -0.5f  // Bottom-left
		};
		
		int elements[] = {
			    0, 1, 2,
			    2, 3, 0
		};
		int[] square = Model.getModelIds("square", "general", vertices, elements);
		int squareVao = square[0];
		int squareProgramId = square[1];
		
		wall = new GameObject(position, new Vector3f(0), new Vector3f(size, 0));
		floor = new GameObject(position, new Vector3f(0.8f), new Vector3f(size, 0).sub(1, 1, 0));
		floor.position.z -= 0.1f;
		wall.init(squareVao, squareProgramId);
		floor.init(squareVao, squareProgramId);
		for(Enemy enemy: enemies) {
			enemy.init();
		}
		for(GameObject object: objects) {
			object.init(squareVao, squareProgramId);
		}
	};
	
	@Override
	public void renderObject(Matrix4f viewMatrix){
		wall.renderObject(viewMatrix);
		floor.renderObject(viewMatrix);
		for (int i = 0; i < objects.size(); i++) {
			objects.get(i).renderObject(viewMatrix);
		}
		for (Enemy enemy: enemies) {
			if (!enemy.dead) {
				enemy.renderObject(viewMatrix);
			}
		}
	}
	
	public boolean isHallway(){
		return false;
	}
	
	public void updateRoom(Map map){
		Iterator<Enemy> iter = enemies.iterator();

		while (iter.hasNext()) {
		    Enemy enemy = iter.next();

		    if (!enemy.dead){
		    	enemy.update(map);
		    } else {
		        iter.remove();
		    }
		}
		for (GameObject object: objects){
			object.update(map);
		}
		objects.addAll(addObjects);
		objects.removeAll(removeObjects);
		addObjects = new ArrayList<GameObject>();
		removeObjects = new ArrayList<GameObject>();
	}

}
