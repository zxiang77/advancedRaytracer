package ray2.accel;

import ray2.Ray;
import egl.math.Vector3d;

/**
 * A class representing a node in a bounding volume hierarchy.
 * 
 * @author pramook 
 */
public class BvhNode {

	/** The current bounding box for this tree node.
	 *  The bounding box is described by 
	 *  (minPt.x, minPt.y, minPt.z) - (maxBound.x, maxBound.y, maxBound.z).
	 */
	public final Vector3d minBound, maxBound;
	
	/**
	 * The array of children.
	 * child[0] is the left child.
	 * child[1] is the right child.
	 */
	public final BvhNode child[];

	/**
	 * The index of the first surface under this node. 
	 */
	public int surfaceIndexStart;
	
	/**
	 * The index of the surface next to the last surface under this node.	 
	 */
	public int surfaceIndexEnd; 
	
	/**
	 * Default constructor
	 */
	public BvhNode()
	{
		minBound = new Vector3d();
		maxBound = new Vector3d();
		child = new BvhNode[2];
		child[0] = null;
		child[1] = null;		
		surfaceIndexStart = -1;
		surfaceIndexEnd = -1;
	}
	
	/**
	 * Constructor where the user can specify the fields.
	 * @param minBound
	 * @param maxBound
	 * @param leftChild
	 * @param rightChild
	 * @param start
	 * @param end
	 */
	public BvhNode(Vector3d minBound, Vector3d maxBound, BvhNode leftChild, BvhNode rightChild, int start, int end) 
	{
		this.minBound = new Vector3d();
		this.minBound.set(minBound);
		this.maxBound = new Vector3d();
		this.maxBound.set(maxBound);
		this.child = new BvhNode[2];
		this.child[0] = leftChild;
		this.child[1] = rightChild;		   
		this.surfaceIndexStart = start;
		this.surfaceIndexEnd = end;
	}
	
	/**
	 * @return true if this node is a leaf node
	 */
	public boolean isLeaf()
	{
		return child[0] == null && child[1] == null; 
	}
		
	/** 
	 * Check if the ray intersects the bounding box.
	 * @param ray
	 * @return true if ray intersects the bounding box
	 */
	public boolean intersects(Ray ray) {
		// TODO#A7: fill in this function.
		// looks like the tmp is not necessary -- no need to have it ordered
		double tEnterXtmp = (this.minBound.x - ray.origin.x) / ray.direction.x;
		double tExitXtmp = (this.maxBound.x - ray.origin.x) / ray.direction.x;
		double tEnterX = tEnterXtmp < tExitXtmp ? tEnterXtmp : tExitXtmp;
		double tExitX = tEnterXtmp > tExitXtmp ? tEnterXtmp : tExitXtmp;
		
		double tEnterYtmp = (this.minBound.y - ray.origin.y) / ray.direction.y;
		double tExitYtmp = (this.maxBound.y - ray.origin.y) / ray.direction.y;
		double tEnterY = tEnterYtmp < tExitYtmp ? tEnterYtmp : tExitYtmp;
		double tExitY = tEnterYtmp > tExitYtmp ? tEnterYtmp : tExitYtmp;
		
		double tEnterZtmp = (this.minBound.z - ray.origin.z) / ray.direction.z;
		double tExitZtmp = (this.maxBound.z - ray.origin.z) / ray.direction.z;
		double tEnterZ = tEnterZtmp < tExitZtmp ? tEnterZtmp : tExitZtmp;
		double tExitZ = tEnterZtmp > tExitZtmp ? tEnterZtmp : tExitZtmp;
		
		double tEnter = tEnterY > tEnterZ ? tEnterY : tEnterZ;
		tEnter = tEnter > tEnterX ? tEnter : tEnterX;
		double tExit = tExitY < tExitZ ? tExitY : tExitZ;
		tExit = tExit < tExitX ? tExit : tExitX;
		return tExit >= tEnter && tEnter <= ray.end && tExit >= ray.start;
	}
	
}
