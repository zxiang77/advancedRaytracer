package ray2.shader;

import ray2.RayTracer;
import ray2.IntersectionRecord;
import ray2.Ray;
import ray2.Scene;
import egl.math.Colord;
import egl.math.Vector3d;

/**
 * A Phong material.
 *
 * @author ags, pramook
 */
public class Glass extends Shader {

	/**
	 * The index of refraction of this material. Used when calculating Snell's Law.
	 */
	protected double refractiveIndex;
	public void setRefractiveIndex(double refractiveIndex) { this.refractiveIndex = refractiveIndex; }


	public Glass() { 
		refractiveIndex = 1.0;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {    
		return "glass " + refractiveIndex + " end";
	}

	/**
	 * Evaluate the intensity for a given intersection using the Glass shading model.
	 *
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param record The intersection record of where the ray intersected the surface.
	 * @param depth The recursion depth.
	 */
	@Override
	public void shade(Colord outIntensity, Scene scene, Ray ray, IntersectionRecord record, int depth) {
		// TODO#A7: fill in this function.
        // 1) Determine whether the ray is coming from the inside of the surface or the outside.
        // 2) Determine whether total internal reflection occurs.
        // 3) Compute the reflected ray and refracted ray (if total internal reflection does not occur)
        //    using Snell's law and call RayTracer.shadeRay on them to shade them
		Vector3d Norig = record.normal.clone();
		Vector3d V = ray.direction.clone().negate();

		Colord reflectedIntensity = new Colord();
		Colord refractedIntensity = new Colord();
		Vector3d N;
		double r1, r2;
		double R;
		double cos = getCos(Norig, V);
		if (cos < 0) {
			// ray comes from inside of the surface
			r1 = this.refractiveIndex;
			r2 = 1d;
			N = Norig.clone().negate();
			R = fresnel(N, V, 1/this.refractiveIndex);
		} else {
			// ray comes from outside
			r1 = 1d;
			r2 = this.refractiveIndex;
			N = Norig;
			R = fresnel(N, V, this.refractiveIndex);
		}

		double c1 = getCos(N, V);
		double s1 = cosToSin(c1);
		Vector3d reflected = getReflected(V, N);
		Ray reflectedRay = new Ray(record.location, reflected.normalize());
		reflectedRay.makeOffsetRay();
		RayTracer.shadeRay(reflectedIntensity, scene, reflectedRay, depth + 1);
		if (s1 * r1 >= r2) {
			// internal reflection occurs
			outIntensity.set(reflectedIntensity);
		} else {
			double s2 = s1 * r1 / r2;
			Vector3d refracted = getRefracted(V, N, s2);
			Ray refractedRay = new Ray(record.location, refracted.normalize());
			refractedRay.makeOffsetRay();
			RayTracer.shadeRay(refractedIntensity, scene, refractedRay, depth + 1);
			outIntensity.set(reflectedIntensity.mul(R).add(refractedIntensity.mul(1d-R)));
		}
	}
	
}