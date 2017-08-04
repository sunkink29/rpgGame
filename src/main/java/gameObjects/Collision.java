package gameObjects;

import java.util.ArrayList;

import org.joml.*;

import java.lang.Math;

public class Collision {
	
	public static Vector3f isPlayerColliding(Map map, Player player) {
		Vector3f collisionDirection = isGameObjectColliding(map, player);
		Vector2f position = player.transform.getPosition();
		Vector2f size = player.transform.getScale();
		for(GameObject object: map.objects) {
			if (object.getClass() == Enemy.class) {
				Enemy enemy = (Enemy) object;
				Vector2f enemyPosition = enemy.sword.transform.getPosition();
				Vector2f enemyScale = enemy.sword.transform.getScale();
				if (position.x - size.x/2 < enemyPosition.x + enemyScale.x/2 &&
						position.x + size.x/2 > enemyPosition.x - enemyScale.x/2 &&
						position.y - size.y/2 < enemyPosition.y + enemyScale.y/2 &&
						position.y + size.y/2 > enemyPosition.y - enemyScale.y/2) {
				collisionDirection.add(0, 0, -1);
				}
			}
		}
		return  collisionDirection;
	}
	
	public static Vector3f isGameObjectColliding(Map map, GameObject object) {
		return isObjectColliding(map, object.transform.getPosition(), object.transform.getScale());
	}
	
	public static Vector3f isEnemyColliding(Map map, Enemy enemy) {
		Vector2f position = enemy.transform.getPosition();
		Vector2f size = enemy.transform.getScale();
		Vector3f collisionDirection = isGameObjectColliding(map, enemy);

		GameObject sword = Player.currentPlayer.sword;
		Vector2f swordPosition = sword.transform.getPosition();
		Vector2f swordScale = sword.transform.getScale();
		if (position.x - size.x/2 < swordPosition.x + swordScale.x/2 &&
					position.x + size.x/2 > swordPosition.x - swordScale.x/2 &&
					position.y - size.y/2 < swordPosition.y + swordScale.y/2 &&
					position.y + size.y/2 > swordPosition.y - swordScale.y/2) {
			collisionDirection.add(0, 0, -1);
		}
		return collisionDirection;
	}
	
	public static Vector3f isObjectColliding(Map map, Vector2f position, Vector2f size) {
		ArrayList<Room> rooms = map.rooms;
		Vector3f collisionDirection = new Vector3f();
		boolean inHallway = false;
		for (int i = 0; i < rooms.size(); i++) {
			Room room = rooms.get(i);
			Vector2f roomPosition = room.transform.getPosition();
			Vector2f roomWallScale = room.wall.transform.getScale();
			Vector2f roomFloorScale = room.floor.transform.getScale();
			if (position.x - size.x < roomPosition.x + roomWallScale.x/2 &&
					position.x + size.x > roomPosition.x - roomWallScale.x/2 &&
					position.y - size.y < roomPosition.y + roomWallScale.y/2 &&
					position.y + size.y > roomPosition.y - roomWallScale.y/2 &&
					(!inHallway || room.isHallway())) {
				if (room.isHallway()) {
					Hallway hallway = (Hallway) room;
					Vector3f dest = new Vector3f(collisionDirection);
					dest.x = Math.abs(dest.x);
					dest.y = Math.abs(dest.y);
					dest.sub(new Vector3f(hallway.direction, 0));
					collisionDirection.mul(dest);
				}
//				collisionDirection = inHallway?new Vector2f():collisionDirection;
				if (position.y + 0.5f > roomPosition.y+roomFloorScale.y/2) {
					collisionDirection.add(0, 1, 0);
				} else if (position.y - 0.5f < roomPosition.y-roomFloorScale.y/2) {
					collisionDirection.add(0, -1, 0);
				}
				if (position.x + 0.5f > roomPosition.x+roomFloorScale.x/2) {
					collisionDirection.add(1, 0, 0);
				} else if (position.x - 0.5f < roomPosition.x-roomFloorScale.x/2) {
					collisionDirection.add(-1, 0, 0);
				}
			}
		}
		return collisionDirection;
	}

}
