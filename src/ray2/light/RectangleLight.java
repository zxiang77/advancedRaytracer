package ray2.light;


import egl.math.Vector3d;

/**
 * This class represents an area source that is rectangular, specified by a
 * frame in the same way as a camera.  It has constant radiance across the
 * whole surface.
 *
 * @author srm, zechenz
 */
public class RectangleLight extends Light {
	
	/** Where the light is located in space. */
	public final Vector3d position = new Vector3d();
	public void setPosition(Vector3d position) { this.position.set(position); }

	/** The direction the light is facing. */
	protected final Vector3d normalDir = new Vector3d(0, 0, -1);
	public void setNormalDir(Vector3d normalDir) { this.normalDir.set(normalDir); }
	
	/** The upwards direction, which is aligned with the light's height axis. */
	protected final Vector3d upDir = new Vector3d(0, 1, 0);
	public void setUpDir(Vector3d upDir) { this.upDir.set(upDir); }
	
	/** The height of the source, in world units. */
	protected double height = 1.0;
	public void setHeight(double height) { this.height = height; }
	
	/** The width of the source, in world units. */
	protected double width = 1.0;
	public void setWidth(double width) { this.width = width; }
	
	/*
	 * TODO#A7: declare necessary variables 
	 * e.g. the orthonormal basis vectors for the rect area light
	 */
	protected Vector3d u = new Vector3d();
	protected Vector3d v = new Vector3d();
	protected Vector3d w = new Vector3d();
	/**
	 * Initialize the derived view variables to prepare for using the camera.
	 */
	public void init() {
		// TODO#A7: Fill in this function
		// 1) Set the 3 basis vectors in the orthonormal basis, 
        //    based on normalDir and upDir
        // 2) Set up the helper variables if needed
		w = normalDir.set(normalDir).negate();
		u = w.clone().cross(upDir);
		v = w.clone().cross(u);
	}

	@Override
	public void sample(LightSamplingRecord lRec, Vector3d shadingPoint) {
		// TODO#A7: Fill in this function
		// 1. sample light source point on the rectangle area light in uniform-random fashion
		// 2. compute the l vector, i.e. the direction the light incidents on the shading point
		// 3. compute the distance between light point and shading point, and get attenuation
		// 4. compute the probability this light point is sampled, which is used for Monte-Carlo integration
		// 5. write relevant info to LightSamplingRecord object
		Vector3d randU = u.clone().mul((Math.random() - 0.5d) * this.width);
		Vector3d randV = v.clone().mul((Math.random() - 0.5d) * this.height);
		Vector3d randPos = randU.add(randV).add(this.position);
		
		Vector3d L = randPos.clone().sub(shadingPoint);
		lRec.direction.set(L);
		lRec.distance = L.len();
		lRec.attenuation = L.len() * L.len();
		lRec.probability = 1d / (this.height * this.width);
	}

	/**
	 * Default constructor.  Produces a unit square light at the origin facing -z.
	 */
	public RectangleLight() { }
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "RectangleLight: " + width + "x" + height + " @ " + position + " " + intensity + "; normal " + normalDir + "; up " + upDir;
	}
}