package ray2.shader;

import ray2.RayTracer;
import ray2.IntersectionRecord;
import ray2.Ray;
import ray2.Scene;
import egl.math.Colord;
import egl.math.Util;
import egl.math.Vector3d;

/**
 * A Phong material.
 *
 * @author ags, pramook
 */
public class Glazed extends Shader {

	/**
	 * The index of refraction of this material. Used when calculating Snell's Law.
	 */
	protected double refractiveIndex;
	public void setRefractiveIndex(double refractiveIndex) { this.refractiveIndex = refractiveIndex; }

	/**
	 * The underlying material beneath the glaze.
	 */
	protected Shader substrate;
	public void setSubstrate(Shader substrate) {
		this.substrate = substrate; 
	}
	
	public Glazed() { 
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
		// You may find it helpful to create helper methods if the code here gets too long.
		//  1) Compute the Fresnel term R
        //  2) Shade the substrate and multiply the result color by 1 - R
        //  3) Compute the reflected ray and call RayTracer.shadeRay on it, multiply result color by R
		
		Vector3d Norig = record.normal.clone();
		Vector3d V = ray.direction.clone().negate();

		Colord reflectedIntensity = new Colord();
		Colord substrateIntensity = new Colord();
		Vector3d N;
		double r1, r2;

		double cos = getCos(Norig, V);
		double R = fresnel(Norig, V, this.refractiveIndex);
		if (cos < 0) {
			// ray comes from inside of the surface
			r1 = this.refractiveIndex;
			r2 = 1d;
			N = Norig.clone().negate();
		} else {
			// ray comes from outside
			r1 = 1d;
			r2 = this.refractiveIndex;
			N = Norig;
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
			Ray substrateRay = new Ray(record.location, refracted.normalize());
			substrateRay.makeOffsetRay();
			substrate.shade(substrateIntensity, scene, substrateRay, record, depth + 1);
			outIntensity.set(reflectedIntensity.mul(R).add(substrateIntensity.mul(1d-R)));
		}
	}
}