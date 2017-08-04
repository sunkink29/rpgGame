package main;

import static org.lwjgl.glfw.GLFW.*;

import java.util.HashMap;
import java.util.Map;

public class Input {
	
	private static Long window;
	private static boolean hasInitialized;
	private static Map<Integer, KeyObject> knownKeyObjects = new HashMap<Integer, KeyObject>();
	private static Map<String, KeyObject> keyBindings = new HashMap<String, KeyObject>();
	
	public static void initalizeInput(Long window) {
		Input.window = window;
		glfwSetKeyCallback(window, 
				(window2, key, scancode, action, mods)->keyCallback(window2, key, scancode, action, mods));
		hasInitialized = true;
	}
	
	static void keyCallback(long window, int key, int scancode, int action, int mods) {
		KeyObject keyObj = knownKeyObjects.get(key);
		if (keyObj != null && action != GLFW_REPEAT) {
			if (keyObj.currentState != action) {
				keyObj.currentState = action;
				keyObj.UncheckedHalfTransitions++;
			}
		}
	}
	
	public static boolean getKeyDown(int keyCode) {
		if (hasInitialized) {
			int status = glfwGetKey(window, keyCode);
			if (!knownKeyObjects.containsKey(keyCode)) {
				knownKeyObjects.put(keyCode, new KeyObject(keyCode, status));
			}
			return status == GLFW_PRESS;
		}
		return false;
	}
	
	public static void addButtonBinding(String binding, int keyCode) {
		getKeyDown(keyCode);
		KeyObject keyObj = knownKeyObjects.get(keyCode);
		keyBindings.put(binding, keyObj);
	}
	
	public static boolean getButtonDown(String binding) {
		if (keyBindings.containsKey(binding) && hasInitialized) {
			return getKeyDown(keyBindings.get(binding).keyCode);
		}
		return false;
	}

}

class KeyObject {
	int keyCode;
	int currentState;
	int UncheckedHalfTransitions;
	
	public KeyObject(int keyCode, int currentState) {
		this.keyCode = keyCode;
		this.currentState = currentState;
	}
}
