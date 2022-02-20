package alteran;

import net.minecraft.util.math.vector.Quaternion;

public class AQuaternion {
	public float w;
	public float x;
	public float y;
	public float z;

	public static AQuaternion IDENTITY = new AQuaternion(0, 0, 0, 1f);

	public static AQuaternion angleAxis(float degress, AVector3f axis) {
		if (axis.squaredLength() == 0.0f) return AQuaternion.IDENTITY;

		float radians = (float) Math.toRadians(degress) * 0.5f;
		axis = axis.normalize().mul((float) Math.sin(radians));

		return new AQuaternion(axis.x, axis.y, axis.z, (float) Math.cos(radians)).normalize();
	}

	public AQuaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public AQuaternion(Quaternion quaternion) {
		this.w = quaternion.r();
		this.x = quaternion.i();
		this.y = quaternion.j();
		this.z = quaternion.k();
	}

	public AQuaternion(AVector3f vector, float w) {
		this.x = vector.x;
		this.y = vector.y;
		this.z = vector.z;
		this.w = w;
	}

	public AQuaternion() {
	}

	public double dot(AQuaternion q) {
		return x * q.x + y * q.y + z * q.z + w * q.w;
	}

	public AQuaternion mul(float scale) {
		if (scale == 1) return this;
		return new AQuaternion(x * scale, y * scale, z * scale, w * scale);
	}

	public AVector3f mul(AVector3f point) {
		float num = x * 2f;
		float num2 = y * 2f;
		float num3 = z * 2f;
		float num4 = x * num;
		float num5 = y * num2;
		float num6 = z * num3;
		float num7 = x * num2;
		float num8 = x * num3;
		float num9 = y * num3;
		float num10 = w * num;
		float num11 = w * num2;
		float num12 = w * num3;
		return new AVector3f((1f - (num5 + num6)) * point.x + (num7 - num12) * point.y + (num8 + num11) * point.z,
			(num7 + num12) * point.x + (1f - (num4 + num6)) * point.y + (num9 - num10) * point.z,
			(num8 - num11) * point.x + (num9 + num10) * point.y + (1f - (num4 + num5)) * point.z);
	}

	public AQuaternion mul(AQuaternion q) {
		return new AQuaternion(w * q.x + x * q.w + y * q.z - z * q.y, w * q.y + y * q.w + z * q.x - x * q.z,
			w * q.z + z * q.w + x * q.y - y * q.x, w * q.w - x * q.x - y * q.y - z * q.z);
	}

	public AQuaternion div(float scale) {
		return mul(1 / scale);
	}

	public float normal() {
		return (float) Math.sqrt(dot(this));
	}

	public AQuaternion normalize() {
		return div(normal());
	}

	public float lengthSquared() {
		return x * x + y * y + z * z + w * w;
	}

	public float length() {
		return (float) Math.sqrt(lengthSquared());
	}

	public AVector3f xyz() {
		return new AVector3f(x, y, z);
	}

	public AQuaternion inverse(AQuaternion rotation) {
		float lengthSq = rotation.lengthSquared();
		if (lengthSq != 0.0) {
			float i = 1.0f / lengthSq;
			return new AQuaternion(rotation.xyz().mul(-i), rotation.w * i);
		}
		return rotation;
	}

	public Quaternion toQuaternion() {
		return new Quaternion(x, y, z, w);
	}

	public float[][] toRotationMatrix() {

		float norm = (float) dot(this);
		// we explicitly test norm against one here, saving a division
		// at the cost of a test and branch. Is it worth it?
		float s = (norm == 1f) ? 2f : (norm > 0f) ? 2f / norm : 0;

		// compute xs/ys/zs first to save 6 multiplications, since xs/ys/zs
		// will be used 2-4 times each.
		float xs = x * s;
		float ys = y * s;
		float zs = z * s;
		float xx = x * xs;
		float xy = x * ys;
		float xz = x * zs;
		float xw = w * xs;
		float yy = y * ys;
		float yz = y * zs;
		float yw = w * ys;
		float zz = z * zs;
		float zw = w * zs;

		// using s=2/norm (instead of 1/norm) saves 9 multiplications by 2 here
		return new float[][]{{1 - (yy + zz), (xy - zw), (xz + yw)}, {(xy + zw), 1 - (xx + zz), (yz - xw)}, {(xz - yw), (yz + xw), 1 - (xx + yy)}};
	}
}
