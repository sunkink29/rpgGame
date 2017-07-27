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
	public GameObject[] childObjects;
	
	public GameObject(Vector3f position, Vector2f scale, float rotation) {
		this(new Transform(position, scale, rotation));
	}
	
	public GameObject(Transform transform) {
		this.transform = (Transform) addComponent(transform);
	}
	
	public GameObject(Transform transform, Shape shape) {
		this(transform);
		renderer = (Renderer) addComponent(new Renderer());
		renderer.setVaoAndProgramId(shape);
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
	}
	public void renderObject(Matrix4f viewMatrix) {
		if (renderer != null) {
			renderer.render(viewMatrix);
		}
	}
	
	public void objectCollided(Collider otherObject) {}
	
}
