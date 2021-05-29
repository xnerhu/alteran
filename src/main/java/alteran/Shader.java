package alteran;

import alteran.common.AlteranCommon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

public class Shader {
	public final int programId;
	private final int vertexShaderID;
	private final int fragmentShaderID;

	public static int loadShader(String filepath, int type) {
		//String path = ClassLoader.getSystemClassLoader().getResource(filepath).getPath();
		String path = filepath;

		StringBuilder result = new StringBuilder();

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
			String buffer = "";
			while ((buffer = bufferedReader.readLine()) != null) result.append(buffer + "\n");
			bufferedReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, result.toString());
		GL20.glCompileShader(shaderID);
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
			int maxLength = GL20.glGetShaderi(shaderID, GL20.GL_INFO_LOG_LENGTH);

			System.err.println(GL20.glGetShaderInfoLog(shaderID, maxLength));
			System.err.println("Could not compile shader.");
			System.err.println(-1);

			GL20.glDeleteShader(shaderID);
		}
		return shaderID;
	}

	public Shader(String vertexFile, String fragmentFile) {
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		programId = GL20.glCreateProgram();
		GL20.glAttachShader(programId, vertexShaderID);
		GL20.glAttachShader(programId, fragmentShaderID);
		GL20.glLinkProgram(programId);
		GL20.glValidateProgram(programId);

//		int vPosition = GL20.glGetAttribLocation(vertexShaderID, "aPos");
		//		GL20.glEnableVertexAttribArray(vPosition);
		//		GL20.glVertexAttribPointer(vPosition, 3, GL20.GL_FLOAT, false, 3 * 4, (long) 0);
	}

	public void start() {
		GL20.glUseProgram(programId);
	}

	public void stop() {
		GL20.glUseProgram(0);
	}

	public int getID() {
		return this.programId;
	}

	public int getUniform(String name) {
		int result = GL20.glGetUniformLocation(programId, name);
		if (result == -1) System.err.println("Could not find uniform variable '" + name + "'!");
		return GL20.glGetUniformLocation(programId, name);
	}

	public void setUniform3f(String name, AVector3f vector) {
		GL20.glUniform3f(getUniform(name), vector.x, vector.y, vector.z);
	}

	public void delete() {
		stop();
		GL20.glDetachShader(programId, vertexShaderID);
		GL20.glDetachShader(programId, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programId);
	}
}