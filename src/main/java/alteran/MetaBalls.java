package alteran;

import net.minecraft.util.math.vector.Vector4f;

import java.util.Random;

public class MetaBalls {
	private final Vector4f[] balls; // x, y, z, weight

	public MetaBalls(int size, Random random) {
		this.balls = new Vector4f[5 + random.nextInt(7)];

		for (int i = 0; i < balls.length; i++) {
			float ballSize = (0.1f + random.nextFloat() * 0.2f) * size;

			this.balls[i] = new Vector4f((random.nextFloat() - random.nextFloat()) * size * 0.5f,
				(random.nextFloat() - random.nextFloat()) * size * 0.8f,
				(random.nextFloat() - random.nextFloat()) * size * 0.8f, ballSize);
		}
	}

	public float noise(float x, float y, float z) {
		float f = 0;
		for (Vector4f ball : balls) {
			f += ball.w() * Math.abs(ball.w()) / ((x - ball.x()) * (x - ball.x()) + (y - ball.y()) * (y - ball
				.y()) + (z - ball.z()) * (z - ball.z()));
			if (f > 1) {
				return 1;
			}
		}
		return f > 1 ? 1 : 0;
	}
}