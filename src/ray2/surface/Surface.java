package ray2.surface;

import java.util.ArrayList;

import ray2.IntersectionRecord;
import ray2.Ray;
import ray2.shader.Shader;
import egl.math.Matrix4d;
import egl.math.Vector3d;

/**
 * Abstract base class for all surfaces.  Provides access for shader and
 * intersection uniformly for all surfaces.
 *
 * @author ags, ss932
 */
public abstract class Surface {
	/* tMat, tMatInv, tMatTInv are calculated and stored in each instance to avoid recomputing */
	
	/** The transformation matrix. */
	protected Matrix4d tMat;
	
	/** The inverse of the transformation matrix. */
	protected Matrix4d tMatInv;
	
	/** The inverse of the transpose of the transformation matrix. */
	protected Matrix4d tMatTInv;
	
	/** The average position of the surface. Usually calculated by taking the average of 
	 * all the vertices. This point will be used in AABB tree construction. */
	protected Vector3d averagePosition;
	
	/** The smaller coordinate (x, y, z) of the bounding box of this surface */
	protected Vector3d minBound;
	
	/** The larger coordinate (x, y, z) of the bounding box of this surface */
	protected Vector3d maxBound; 
	
	/** Shader to be used to shade this surface. */
	protected Shader shader = Shader.DEFAULT_SHADER;
	public void setShader(Shader shader) { this.shader = shader; }
	public Shader getShader() { return shader; }
	
	public Vector3d getAveragePosition() { return averagePosition; } 
	public Vector3d getMinBound() { return minBound; }
	public Vector3d getMaxBound() { return maxBound; }	
	
	// initialization method
	public void init() {
		// do nothing
	}

	/**
	 * Un-transform rayIn using tMatInv 
	 * @param rayIn Input ray
	 * @return tMatInv * rayIn
	 */
	public Ray untransformRay(Ray rayIn) {
		Ray ray = new Ray(rayIn.origin, rayIn.direction);
		ray.start = rayIn.start;
		ray.end = rayIn.end;

		tMatInv.mulDir(ray.direction);
		tMatInv.mulPos(ray.origin);
		return ray;
	}
	
	public void setTransformation(Matrix4d a, Matrix4d aInv, Matrix4d aTInv) {
		tMat = a;
		tMatInv = aInv;
		tMatTInv = aTInv;
		computeBoundingBox();
	}
	
	/**
	 * Tests this surface for intersection with ray. If an intersection is found
	 * record is filled out with the information about the intersection and the
	 * method returns true. It returns false otherwise and the information in
	 * outRecord is not modified.
	 *
	 * @param outRecord the output IntersectionRecord
	 * @param ray the ray to intersect
	 * @return true if the surface intersects the ray
	 */
	public abstract boolean intersect(IntersectionRecord outRecord, Ray ray);

	/**
	 * Compute the bounding box and store the result in
	 * averagePosition, minBound, and maxBound.
	 */
	public abstract void computeBoundingBox();
	
	/**
	 * Add this surface to the array list in. This array list will be used
	 * in the AABB tree construction.
	 */
	public void appendRenderableSurfaces(ArrayList<Surface> in) {
		in.add(this);
	}
	
	protected Vector3d minVec(Vector3d v1, Vector3d v2) {
		Vector3d ret = new Vector3d();
		ret.x = v1.x < v2.x ? v1.x : v2.x;
		ret.y = v1.y < v2.y ? v1.y : v2.y;
		ret.z = v1.z < v2.z ? v1.z : v2.z;
		return ret;
	}
	
	protected Vector3d maxVec(Vector3d v1, Vector3d v2) {
		Vector3d ret = new Vector3d();
		ret.x = v1.x > v2.x ? v1.x : v2.x;
		ret.y = v1.y > v2.y ? v1.y : v2.y;
		ret.z = v1.z > v2.z ? v1.z : v2.z;
		return ret;
	}
	
	protected void getTransformedBoundingBox(Vector3d minPt, Vector3d maxPt, Matrix4d tMat,
			Vector3d minBound, Vector3d maxBound, Vector3d averagePos) {
		Vector3d p1 = tMat.clone().mulPos(minPt.clone());
		Vector3d p2 = tMat.clone().mulPos(new Vector3d(minPt.x, minPt.y, maxPt.z));
		Vector3d p3 = tMat.clone().mulPos(new Vector3d(minPt.x, maxPt.y, minPt.z));
		Vector3d p4 = tMat.clone().mulPos(new Vector3d(minPt.x, maxPt.y, maxPt.z));
		Vector3d p5 = tMat.clone().mulPos(new Vector3d(maxPt.x, minPt.y, minPt.z));
		Vector3d p6 = tMat.clone().mulPos(new Vector3d(maxPt.x, minPt.y, maxPt.z));
		Vector3d p7 = tMat.clone().mulPos(new Vector3d(maxPt.x, maxPt.y, minPt.z));
		Vector3d p8 = tMat.clone().mulPos(maxPt.clone());
		Vector3d[] points = {p1, p2, p3, p4, p5, p6, p7, p8};
		minBound.set(p1);
		maxBound.set(p1);
		for (Vector3d p : points) {
			minBound.set(minVec(minBound, p));
			maxBound.set(maxVec(maxBound, p));
		}
		averagePos.set(minBound.clone().add(maxBound).div(2d));
	}
	
}
