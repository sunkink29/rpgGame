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
	public void init(int vao, int programID) {
		super.init(vao, programID);
		lastShot = glfwGetTime();
	}
	
	@Override
	public void update(Map map) {
//		position = Player.currentPlayer.player.position;
		if (glfwGetTime() - lastShot > 2) {
			lastShot = glfwGetTime();
			Projectile projectile = new Projectile(position, new Vector3f(0), new Vector3f(0.25f), rotation);
			map.rooms.get(0).addObjects.add(projectile);
			float vertices[] = {
				    -0.5f,  0.5f, // Top-left
				     0.5f,  0.5f, // Top-right
				     0.5f, -0.5f, // Bottom-right
				    -0.5f, -0.5f  // Bottom-left
			};
			
			int elements[] = {
				    0, 1, 2,
				    2, 3, 0
			};
			int[] square = Model.getModelIds("square", "general", vertices, elements);
			int squareVao = square[0];
			int squareProgramId = square[1];
			projectile.init(squareVao, squareProgramId);
		}
	}
}
