package gameObjects;

import java.util.ArrayList;

import org.joml.*;

import java.lang.Math;

public class Collision {
	
	public static Vector3f isPlayerColliding(Map map, Player player) {
		Vector3f collisionDirection = isGameObjectColliding(map, player.player);
		Vector3f position = player.player.position;
		Vector3f size = player.player.scale;
		for (Room room: map.rooms) {
			for (Enemy enemy: room.enemies) {
				if (position.x - size.x/2 < enemy.sword.position.x + enemy.sword.scale.x/2 &&
						position.x + size.x/2 > enemy.sword.position.x - enemy.sword.scale.x/2 &&
						position.y - size.y/2 < enemy.sword.position.y + enemy.sword.scale.y/2 &&
						position.y + size.y/2 > enemy.sword.position.y - enemy.sword.scale.y/2) {
				collisionDirection.add(0, 0, -1);
				}
			}
		}
		return  collisionDirection;
	}
	
	public static Vector3f isGameObjectColliding(Map map, GameObject object) {
		return isObjectColliding(map, object.position, object.scale);
	}
	
	public static Vector3f isEnemyColliding(Map map, Enemy enemy) {
		Vector3f position = enemy.position;
		Vector3f size = enemy.scale;
		Vector3f collisionDirection = isGameObjectColliding(map, enemy);

		GameObject sword = Player.currentPlayer.sword;
		if (position.x - size.x/2 < sword.position.x + sword.scale.x/2 &&
					position.x + size.x/2 > sword.position.x - sword.scale.x/2 &&
					position.y - size.y/2 < sword.position.y + sword.scale.y/2 &&
					position.y + size.y/2 > sword.position.y - sword.scale.y/2) {
			collisionDirection.add(0, 0, -1);
		}
		return collisionDirection;
	}
	
	public static Vector3f isObjectColliding(Map map, Vector3f position, Vector3f size) {
		ArrayList<Room> rooms = map.rooms;
		Vector3f collisionDirection = new Vector3f();
		boolean inHallway = false;
		for (int i = 0; i < rooms.size(); i++) {
			Room room = rooms.get(i);
			if (position.x - size.x < room.position.x + room.wall.scale.x/2 &&
					position.x + size.x > room.position.x - room.wall.scale.x/2 &&
					position.y - size.y < room.position.y + room.wall.scale.y/2 &&
					position.y + size.y > room.position.y - room.wall.scale.y/2 &&
					(!inHallway || room.isHallway())) {
//				System.out.println("in room");
				if (room.isHallway()) {
					Hallway hallway = (Hallway) room;
					Vector3f dest = new Vector3f(collisionDirection);
					dest.x = Math.abs(dest.x);
					dest.y = Math.abs(dest.y);
					dest.sub(new Vector3f(hallway.direction, 0));
					collisionDirection.mul(dest);
				}
//				collisionDirection = inHallway?new Vector2f():collisionDirection;
				if (position.y + 0.5f > room.position.y+room.floor.scale.y/2) {
					collisionDirection.add(0, 1, 0);
				} else if (position.y - 0.5f < room.position.y-room.floor.scale.y/2) {
					collisionDirection.add(0, -1, 0);
				}
				if (position.x + 0.5f > room.position.x+room.floor.scale.x/2) {
					collisionDirection.add(1, 0, 0);
				} else if (position.x - 0.5f < room.position.x-room.floor.scale.x/2) {
					collisionDirection.add(-1, 0, 0);
				}
			}
		}
		return collisionDirection;
	}

}
