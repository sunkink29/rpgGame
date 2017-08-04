package components;

import org.joml.Vector2f;

public class Transform extends Component {

	private Vector2f position = new Vector2f();
	private float layer;
	private Vector2f scale = new Vector2f();
	private float rotation;
	
	public Vector2f getPosition() {return new Vector2f(position);}
	public void setPosition (Vector2f newPos) {position.set(newPos);}
	
	public float getLayer() {return layer;}
	public void setLayer(float newLayer) {layer = newLayer;}
	
	public Vector2f getScale() {return new Vector2f(scale);}
	public void setScale(Vector2f newScale) {scale.set(newScale);}
	
	public float getRotation() {return rotation;}
	public void setRotation(float newRotation) {rotation = newRotation;}
	
	public Transform() {
		position = new Vector2f(0);
		layer = 0;
		scale = new Vector2f(1);
		rotation = 0;
	}
	
	public Transform(Vector2f position, float layer, Vector2f scale, float rotation) {
		setPosition(position);
		setLayer(layer);
		setScale(scale);
		setRotation(rotation);
	}
}
