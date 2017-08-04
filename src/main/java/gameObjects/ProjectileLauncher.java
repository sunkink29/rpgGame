package gameObjects;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

import components.Collider;
import components.Transform;

public class ProjectileLauncher extends GameObject {
	
	double lastShot;
	Vector2f targetDirection = new Vector2f(0,1);
	
	public ProjectileLauncher(Vector2f position, Vector3f color) {
		super(new Transform(position, -0.1f,new Vector2f(0.5f),0), defaultShapes.Square.getInstance());
		addComponent(new Collider(true, CollisionObjs.STATICOBJECT));
		renderer.setColor(color);
	}
	
	public ProjectileLauncher(Vector2f position, Vector3f color, Vector2f targetDirection) {
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
			Projectile projectile = new Projectile(new Transform(transform.getPosition().add(0, 0f), 0, new Vector2f(0.25f), 0), defaultShapes.Square.getInstance(), targetDirection);
			map.addObject(projectile);
		}
	}
}
