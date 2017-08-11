package gameObjects;

import org.joml.Vector2f;
import org.joml.Vector3f;

import components.Renderer;
import components.Transform;
import rendering.Model;

public class GuiObjectTest extends GameObject {
	
	static float[] v = {
		      0.0f,  0.25f, // middle
		      0.35f, 0.5f,  // top right
		      0.5f,  0.25f, // right
		      0.0f, -0.5f,	// bottem
		     -0.5f,  0.25f,	// left
		     -0.35f, 0.5f	// top left
	};
	
	static float offset = 0.2f;
	
	static float[] v2 = {
		      v[0]		   ,  v[1]  + offset, // middle
		      v[2]		   ,  v[3]  + offset, // top right
		      v[4] + offset,  v[5]		 	, // right
		      v[6] 		   ,  v[7]  - offset, // bottem
		      v[8] - offset,  v[9]		 	, // left
		      v[10]		   ,  v[11] + offset  // top left
	};
	
	static int[] elements = {
		    0, 1, 2,
		    0, 2, 3,
		    0, 3, 4,
		    0, 4, 5
	};

	public GuiObjectTest(Transform transform) {
		super(transform);
		renderer = (Renderer) addComponent(new Renderer());
		int[] ids = Model.getModelIds("heart", "gui", v, elements);
		renderer.setVaoAndProgramId(ids[0],ids[1]);
		renderer.setColor(new Vector3f(1f,0.3f,0.3f));
		GameObject background = new GameObject(new Transform(new Vector2f(0), -1f, new Vector2f(1f), 0));
		background.renderer = (Renderer) background.addComponent(new Renderer());
		ids = Model.getModelIds("heartBack", "gui", v2, elements);
		background.renderer.setVaoAndProgramId(ids[0], ids[1]);
		background.renderer.setColor(new Vector3f(0.1f));
		addChildObject(background);
	}

}
