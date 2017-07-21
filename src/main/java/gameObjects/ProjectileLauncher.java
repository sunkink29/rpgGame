package gameObjects;

import org.joml.Vector2f;
import org.joml.Vector3f;

import rendering.Model;
import static org.lwjgl.glfw.GLFW.*;

import components.Transform;

public class ProjectileLauncher extends GameObject {
	
	double lastShot;
	
	public ProjectileLauncher(Vector3f position, Vector3f color, float rotation) {
		super(new Transform(position,new Vector2f(0.5f),rotation), defaultShapes.Square.getInstance());
		renderer.setColor(color);
	}
	
	@Override
	public void init() {
		lastShot = glfwGetTime();
	}
	
	@Override
	public void update(Map map) {
//		position = Player.currentPlayer.player.position;
		if (glfwGetTime() - lastShot > 2) {
			lastShot = glfwGetTime();
			Projectile projectile = new Projectile(new Transform(transform.getPosition(), new Vector2f(0.25f), 0), defaultShapes.Square.getInstance());
			map.addObject(projectile);
		}
	}
}
