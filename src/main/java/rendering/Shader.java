package rendering;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Shader {
	
	static String[][] shaders = {
		{"general",
			"generalShader.vert",
			"generalShader.frag"},
		{"test",
				"generalShader.vert",
				"testShader.frag"}
			};
	static ArrayList<ArrayList<String>> initializedShaders = new ArrayList<ArrayList<String>>();
	
	public static int getProgramId(String name) {
		for (int i = 0; i < shaders.length; i++) {
			if (shaders[i][0].equals(name)) {
				for (int j = 0; j < initializedShaders.size(); j++) {
					if (initializedShaders.get(j).get(0).equals(name)) {
						return Integer.parseInt(initializedShaders.get(j).get(1));
					}
				}
				try {
					int programId = ShaderUtils.LoadShaders(shaders[i][1], shaders[i][2]);
					String[] list = {name,programId+""};
					List<String> temp = Arrays.asList(list);
					ArrayList<String> list2 = new ArrayList<String>();
					list2.addAll(temp);
					initializedShaders.add(list2);
					return programId;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return -1;
	}

}
