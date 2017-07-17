package gameObjects;

import org.joml.Vector3f;

import rendering.Model;
import static org.lwjgl.glfw.GLFW.*;

public class ProjectileLauncher extends GameObject {
	
	double lastShot;
	
	public ProjectileLauncher(Vector3f position, Vector3f color, float rotation) {
		super(position, color, new Vector3f(.5f), rotation);
	}
	
	@Override
	public void init() {
		super.initRenderer(defaultShapes.Square.getInstance());
		lastShot = glfwGetTime();
	}
	
	@Override
	public void update(Map map) {
//		position = Player.currentPlayer.player.position;
		if (glfwGetTime() - lastShot > 2) {
			lastShot = glfwGetTime();
			Projectile projectile = new Projectile(position, new Vector3f(0), new Vector3f(0.25f), rotation);
			map.rooms.get(0).addObjects.add(projectile);
			projectile.initRenderer(defaultShapes.Square.getInstance());
		}
	}
}
