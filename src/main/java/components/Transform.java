package components;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Transform extends Component {

	private Vector3f position;
	private Vector2f scale;
	private float rotation;
	
	public Vector3f getPosition() {return new Vector3f(position);}
	public void setPosition (Vector3f newPos) {position = new Vector3f(newPos);}
	
	public Vector2f getScale() {return new Vector2f(scale);}
	public void setScale(Vector2f newScale) {scale = new Vector2f(newScale);}
	
	public float getRotation() {return rotation;}
	public void setRotation(float newRotation) {rotation = newRotation;}
	
	public Transform() {
		position = new Vector3f(0);
		scale = new Vector2f(1);
		rotation = 0;
	}
	
	public Transform(Vector3f position, Vector2f scale, float rotation) {
		setPosition(position);
		setScale(scale);
		setRotation(rotation);
	}
}
