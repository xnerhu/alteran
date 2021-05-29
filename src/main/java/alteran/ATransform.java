package alteran;

import net.minecraft.util.math.vector.Quaternion;

public class ATransform {
	public AQuaternion rotation = AQuaternion.IDENTITY;
	public AVector3f position = new AVector3f();

	public ATransform() {
	}

	public ATransform(AVector3f position) {
		this.position = position;
	}

	public ATransform(AVector3f position, AQuaternion rotation) {
		this.position = position;
		this.rotation = rotation;
	}

	public ATransform(AQuaternion rotation) {
		this.rotation = rotation;
	}

	public ATransform rotateAround(AVector3f center, AVector3f axis, float angle) {
		AQuaternion rot = AQuaternion.angleAxis(angle, axis); // get the desired rotation
		AVector3f dir = position.substract(center); // find current direction relative to center
		dir = rot.mul(dir); // rotate the direction
		position = center.add(dir); // define new position
		// rotate object to keep looking at the center:
		rotation = rotation.mul(rotation.inverse(rotation).mul(rot).mul(rotation));
		return this;
	}
}
