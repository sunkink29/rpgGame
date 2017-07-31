package gameObjects;

import org.joml.Vector2f;
import org.joml.Vector3f;

import rendering.Model;
import static org.lwjgl.glfw.GLFW.*;

import components.Collider;
import components.Transform;

public class ProjectileLauncher extends GameObject {
	
	double lastShot;
	Vector3f targetDirection = new Vector3f(0,1,0);
	
	public ProjectileLauncher(Vector3f position, Vector3f color) {
		super(new Transform(position,new Vector2f(0.5f),0), defaultShapes.Square.getInstance());
		addComponent(new Collider(true, CollisionObjs.STATICOBJECT));
		renderer.setColor(color);
	}
	
	public ProjectileLauncher(Vector3f position, Vector3f color, Vector3f targetDirection) {
		this(position,color);
		this.targetDirection = targetDirection;
	}
	
	@Override
	public void init() {
		lastShot = glfwGetTime();
	}
	
	@Override
	public void update(Map map) {
//		position = Player.currentPlayer.player.position;
		super.update(map);
		if (glfwGetTime() - lastShot > 2) {
			lastShot = glfwGetTime();
			Projectile projectile = new Projectile(new Transform(transform.getPosition().add(0, 0f, 0), new Vector2f(0.25f), 0), defaultShapes.Square.getInstance(), targetDirection);
			map.addObject(projectile);
		}
	}
}
