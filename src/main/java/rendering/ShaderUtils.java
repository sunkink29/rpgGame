package rendering;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

public class ShaderUtils {
	
	public static char[] fileToByteArray(String source) throws FileNotFoundException {
		String output = "";
		URL urlDir = ShaderUtils.class.getResource(source);
		URI uriDir = null;
		try {
			uriDir = urlDir.toURI();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file = new File(uriDir);
		Scanner scanner = new Scanner(file);

		output = scanner.nextLine();
		while (scanner.hasNextLine()) {
		       output = output + "\n" + scanner.nextLine();
		}

		return output.toCharArray();
	}
	
	static int LoadShaders(String vertex_file_path,String fragment_file_path) throws FileNotFoundException{

		// Create the shaders
		int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
		int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);

		// Read the Vertex Shader code from the file
		char[] vertexShaderCode = ShaderUtils.fileToByteArray(vertex_file_path);

		// Read the Fragment Shader code from the file
		char[] fragmentShaderCode = ShaderUtils.fileToByteArray(fragment_file_path);
		
		// Compile Vertex Shader
		System.out.printf("Compiling shader : %s\n", vertex_file_path);
		glShaderSource(vertexShaderID, java.nio.CharBuffer.wrap(vertexShaderCode));
		glCompileShader(vertexShaderID);

		// Check Vertex Shader
//		IntBuffer Result = null;
//		IntBuffer InfoLogLength = null;
//		glGetShaderiv(VertexShaderID, GL_COMPILE_STATUS, Result);
//		glGetShaderiv(VertexShaderID, GL_INFO_LOG_LENGTH, InfoLogLength);
//		if ( InfoLogLength.get(0) > 0 ){
			System.out.println(glGetError());
			System.out.println(glGetShaderInfoLog(vertexShaderID));
//		}



		// Compile Fragment Shader
		System.out.printf("Compiling shader : %s\n", fragment_file_path);
		glShaderSource(fragmentShaderID, java.nio.CharBuffer.wrap(fragmentShaderCode));
		glCompileShader(fragmentShaderID);

		// Check Fragment Shader
//		glGetShaderiv(FragmentShaderID, GL_COMPILE_STATUS, Result);
//		glGetShaderiv(FragmentShaderID, GL_INFO_LOG_LENGTH, InfoLogLength);
//		if ( InfoLogLength.get(0) > 0 ){
			System.out.println(glGetError());
			System.out.println(glGetShaderInfoLog(fragmentShaderID));
//		}



		// Link the program
		System.out.printf("Linking program\n");
		int ProgramID = glCreateProgram();
		glAttachShader(ProgramID, vertexShaderID);
		glAttachShader(ProgramID, fragmentShaderID);
		glLinkProgram(ProgramID);

		// Check the program
//		glGetProgramiv(ProgramID, GL_LINK_STATUS, Result);
//		glGetProgramiv(ProgramID, GL_INFO_LOG_LENGTH, InfoLogLength);
//		if ( InfoLogLength.get(0) > 0 ){
			System.out.println(glGetError());
			System.out.println(glGetProgramInfoLog(ProgramID));
//		}

		
		glDetachShader(ProgramID, vertexShaderID);
		glDetachShader(ProgramID, fragmentShaderID);
		
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);

		return ProgramID;
	}
}
