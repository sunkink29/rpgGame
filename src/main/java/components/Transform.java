package components;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;


public class Transform extends Component {

	private Vector2f position = new Vector2f();
	private float layer;
	private Vector2f scale = new Vector2f();
	private float rotation;
	private Matrix4f transformMatrix;
	
	public Vector2f getPosition() {
		if (getGameObject() != null && getGameObject().parentObject != null) {
			if (getGameObject().parentObject.transform.transformMatrix == null) {
				getGameObject().parentObject.transform.updateMatrix();
			}
			Vector4f temp = new Vector4f(position, 1, 1).mul(getGameObject().parentObject.transform.transformMatrix);
			return new Vector2f(temp.x, temp.y);
		}
		return new Vector2f(position);}
	public Vector2f getLocalPosition() {return new Vector2f(position);}
	public void setPosition (Vector2f newPos) {position.set(newPos); updateMatrix();}
	
	public float getLayer() {return layer;}
	public void setLayer(float newLayer) {layer = newLayer;}
	
	public Vector2f getScale() {if (getGameObject() != null && getGameObject().parentObject != null) {return new Vector2f(scale).mul(getGameObject().parentObject.transform.getScale());} return new Vector2f(scale);}
	public Vector2f getLocalScale() {return new Vector2f(scale);}
	public void setScale(Vector2f newScale) {scale.set(newScale); updateMatrix();}
	
	public float getRotation() {if (getGameObject() != null && getGameObject().parentObject != null) { return rotation + getGameObject().parentObject.transform.getRotation();} return rotation;}
	public float getLocalRotation() {return rotation;}
	public void setRotation(float newRotation) {rotation = newRotation; updateMatrix();}
	
	private void updateMatrix() {
		if (getGameObject() != null)
			transformMatrix = new Matrix4f().translate(new Vector3f(getPosition(), 1)).rotate(getRotation(), new Vector3f(0, 0, 1)).scale(new Vector3f(getScale(), 1));
	}
	
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
