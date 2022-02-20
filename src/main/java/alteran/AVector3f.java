package alteran;

import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class AVector3f {
	public float x;
	public float y;
	public float z;

	public static AVector3f forward = new AVector3f(0, 0, 1f);
	public static AVector3f up = new AVector3f(0, 1f, 0);

	public AVector3f() {
	}

	public AVector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public AVector3f(double x, double y, double z) {
		this.x = (float) x;
		this.y = (float) y;
		this.z = (float) z;
	}

	public AVector3f(AVector3f vector) {
		this.x = vector.x;
		this.y = vector.y;
		this.z = vector.z;
	}

	public AVector3f(Vector3f vector) {
		this.x = vector.x();
		this.y = vector.y();
		this.z = vector.z();
	}

	public AVector3f(Vector3d vector) {
		this.x = (float) vector.x;
		this.y = (float) vector.y;
		this.z = (float) vector.z;
	}

	public AVector3f substract(AVector3f vector) {
		return new AVector3f(x - vector.x, y - vector.y, z - vector.z);
	}

	public AVector3f add(AVector3f vector) {
		return new AVector3f(x + vector.x, y + vector.y, z + vector.z);
	}

	public AVector3f normalize() {
		float length = length();
		if (length == 0) return new AVector3f();
		return new AVector3f(x / length, y / length, z / length);
	}

	public AVector3f cross(AVector3f vector) {
		return new AVector3f(y * vector.z - z * vector.y, z * vector.x - x * vector.z, x * vector.y - y * vector.x);
	}

	public float dot(AVector3f vector) {
		return x * vector.x + y * vector.y + z * vector.z;
	}

	public AVector3f mul(float scale) {
		return new AVector3f(x * scale, y * scale, z * scale);
	}

	public float squaredLength() {
		return x * x + y * y + z * z;
	}

	public float length() {
		return (float) Math.sqrt(squaredLength());
	}

	public float distance(AVector3f vector) {
		AVector3f difference = substract(vector);
		return (float) Math.sqrt(Math.pow(difference.x, 2f) + Math.pow(difference.y, 2f) + Math.pow(difference.z, 2f));
	}

	@Override
	public String toString() {
		return String.format("AVector3f:   X: %f   Y: %f   Z: %f", x, y, z);
	}
}
