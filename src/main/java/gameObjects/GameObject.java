package gameObjects;

import java.util.ArrayList;

import org.joml.*;

import components.Collider;
import components.Component;
import components.Renderer;
import components.Transform;
import defaultShapes.Shape;
import defaultShapes.Square;

public class GameObject {
	
	public String name;
	public String[] tags;
	public Transform transform;
	public Renderer renderer;
	public ArrayList<Component> components = new ArrayList<Component>();
	public GameObject parentObject;
	public ArrayList<GameObject> childObjects = new ArrayList<GameObject>();
	
	public GameObject(Vector2f position, float layer, Vector2f scale, float rotation) {
		this(new Transform(position, layer, scale, rotation));
	}
	
	public GameObject(Transform transform) {
		this.transform = (Transform) addComponent(transform);
	}
	
	public GameObject(Transform transform, Shape shape) {
		this(transform);
		renderer = (Renderer) addComponent(new Renderer());
		renderer.setVaoAndProgramId(shape);
	}
	
	public void addChildObject(GameObject childObject) {
		childObject.parentObject = this;
		childObjects.add(childObject);
	}

	public Component addComponent(Component newComponent) {
		components.add(newComponent);
		newComponent.setGameObject(this);
		newComponent.init();
		return newComponent;
	}
	
	public <T extends Component> T getComponent(T type) {
		for (Component component: components) {
			if (component.getClass() == type.getClass()) {
				return (T)component;
			}
		}
		return null;
	}
	
	public void init() {}
	public void update(Map map){
		for(Component component: components) {
			component.update(map);
		}
		for (GameObject obj: childObjects) {
			obj.update(map);
		}
	}
	public void renderObject(Matrix4f viewMatrix) {
		if (renderer != null) {
			renderer.render(viewMatrix);
		}
		for (GameObject obj: childObjects) {
			obj.renderObject(viewMatrix);
		}
	}
	
	public void objectCollided(Collider otherObject) {}
	
}
