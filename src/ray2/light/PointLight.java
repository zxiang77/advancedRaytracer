package ray2.light;


import egl.math.Vector3d;

/**
 * This class represents a basic point light which is infinitely small and emits
 * a constant power in all directions. This is a useful idealization of a small
 * light emitter.
 *
 * @author ags, zechenz
 */
public class PointLight extends Light {
	
	/** Where the light is located in space. */
	public final Vector3d position = new Vector3d();
	public void setPosition(Vector3d position) { this.position.set(position); }

	public void init() {
		// do nothing
	}

	@Override
	public void sample(LightSamplingRecord lRec, Vector3d shadingPoint) {
		lRec.direction.set(position).sub(shadingPoint);
		lRec.attenuation = 1.0 / shadingPoint.distSq(this.position);
		lRec.distance = lRec.direction.len();
		lRec.probability = 1.0;
	}
	
	/**
	 * Default constructor.  Produces a unit intensity light at the origin.
	 */
	public PointLight() { }
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "PointLight: " + position + " " + intensity;
	}
}