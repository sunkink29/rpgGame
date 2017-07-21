package components;

import gameObjects.GameObject;
import gameObjects.Map;

public abstract class Component {
	
	private GameObject gameObject;
	
	public GameObject getGameObject() {return gameObject;}
	public void setGameObject(GameObject newGameObject) {if (gameObject==null)gameObject = newGameObject;}
	
	public void init() {}
	public void update(Map map) {}
	
	

}
