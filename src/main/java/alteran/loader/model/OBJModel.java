package alteran.loader.model;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import alteran.AQuaternion;
import alteran.AVector3f;
import alteran.Shader;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class OBJModel {

	private int drawCount;
	public boolean modelInitialized;

	private int vId;
	private int tId;
	private int nId;
	private int iId;
	private boolean hasTex;
	private int vao;

	public float[] vertices;
	public float[] textureCoords;
	public float[] normals;
	public int[] indices;


	public OBJModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices, boolean hasTex) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.indices = indices;
		this.hasTex = hasTex;

		modelInitialized = false;
	}

	private int createAndBindBuffer(float[] data) {
		int id = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
		return id;
	}

	private int createAndBindBuffer(int[] data) {
		int id = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, id);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, createIntBuffer(data), GL15.GL_STATIC_DRAW);
		return id;
	}

	private void storeDataInAttributeList(int attributeNumber, int elementsCount) {
		GL20.glEnableVertexAttribArray(attributeNumber);
		GL20.glVertexAttribPointer(attributeNumber, elementsCount, GL11.GL_FLOAT, false, 0, 0);
	}

	private void cleanUpBuffers() {
		GL30.glBindVertexArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	private void initializeModel() {
		drawCount = indices.length;

		vId = GL15.glGenBuffers();
		nId = GL15.glGenBuffers();
		iId = GL15.glGenBuffers();
		tId = GL15.glGenBuffers();

		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);

		//		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, );
		//		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, createIntBuffer(indices), GL15.GL_STATIC_DRAW);

		vId = this.createAndBindBuffer(vertices);
		this.storeDataInAttributeList(0, 3);

		nId = this.createAndBindBuffer(normals);
		this.storeDataInAttributeList(1, 3);

		iId = this.createAndBindBuffer(indices);

		if (hasTex) {
			tId = this.createAndBindBuffer(textureCoords);
			this.storeDataInAttributeList(2, 2);
		}

		cleanUpBuffers();

		modelInitialized = true;
	}

	public void render(MatrixStack ms) {
		if (!modelInitialized) {
			//this.initializeModel();
		}

		Matrix4f matrix = ms.last().pose();

		VertexFormat format = DefaultVertexFormats.POSITION_TEX;


		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		Minecraft mc = Minecraft.getInstance();

		for (int i = 0; i < vertices.length / 9; i++) {
			float x1 = vertices[i * 9];
			float y1 = vertices[i * 9 + 1];
			float z1 = vertices[i * 9 + 2];

			AVector3f v1 = new AVector3f(x1, y1, z1);

			float x2 = vertices[i * 9 + 3];
			float y2 = vertices[i * 9 + 4];
			float z2 = vertices[i * 9 + 5];

			AVector3f v2 = new AVector3f(x2, y2, z2);

			float x3 = vertices[i * 9 + 6];
			float y3 = vertices[i * 9 + 7];
			float z3 = vertices[i * 9 + 8];

			AVector3f v3 = new AVector3f(x3, y3, z3);

			float texU1 = textureCoords[i * 6];
			float texV1 = textureCoords[i * 6 + 1];

			float texU2 = textureCoords[i * 6 + 2];
			float texV2 = textureCoords[i * 6 + 3];

			float texU3 = textureCoords[i * 6 + 4];
			float texV3 = textureCoords[i * 6 + 5];

			float normalX1 = normals[i * 9];
			float normalY1 = normals[i * 9 + 1];
			float normalZ1 = normals[i * 9 + 2];

			float normalX2 = normals[i * 9 + 3];
			float normalY2 = normals[i * 9 + 4];
			float normalZ2 = normals[i * 9 + 5];

			float normalX3 = normals[i * 9 + 6];
			float normalY3 = normals[i * 9 + 7];
			float normalZ3 = normals[i * 9 + 8];

			float b = 1.0f;


			bufferbuilder.begin(GL11.GL_POLYGON, format);
			bufferbuilder.vertex(matrix, x1, y1, z1).uv(texU1, texV1).normal(normalX1, normalY1, normalZ1).color(b, b, b, 1f)
				.endVertex();
			bufferbuilder.vertex(matrix, x2, y2, z2).uv(texU2, texV2).normal(normalX2, normalY2, normalZ2).color(b, b, b, 1f)
				.endVertex();
			bufferbuilder.vertex(matrix, x3, y3, z3).uv(texU3, texV3).normal(normalX3, normalY3, normalZ3).color(b, b, b, 1f)
				.endVertex();
			tessellator.end();
		}
	}

	private FloatBuffer createFloatBuffer(float[] input) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(input.length);
		buffer.put(input).flip();

		return buffer;
	}

	private IntBuffer createIntBuffer(int[] input) {
		IntBuffer buffer = BufferUtils.createIntBuffer(input.length);
		buffer.put(input).flip();

		return buffer;
	}
}
