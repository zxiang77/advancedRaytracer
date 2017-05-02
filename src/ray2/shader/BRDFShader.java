package ray2.shader;

import ray2.IntersectionRecord;
import ray2.Ray;
import ray2.Scene;
import ray2.light.Light;
import ray2.light.LightSamplingRecord;
import egl.math.Color;
import egl.math.Colord;
import egl.math.Colorf;
import egl.math.Vector2;
import egl.math.Vector3d;

public abstract class BRDFShader extends Shader {

	/** The color of the diffuse reflection. */
	protected final Colord diffuseColor = new Colord(Color.White);
	public void setDiffuseColor(Colord diffuseColor) { this.diffuseColor.set(diffuseColor); }

	protected abstract void evalBRDF(Vector3d L, Vector3d V, Vector3d N,
			Colord kD, Colord outColor);

	public BRDFShader() {
		super();
	}


	/**
	 * Evaluate the intensity for a given intersection using the CookTorrance shading model.
	 *
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param iRec The intersection record of where the ray intersected the surface.
	 * @param depth The recursion depth.
	 */
	@Override
	public void shade(Colord outIntensity, Scene scene, Ray ray, IntersectionRecord iRec,
			int depth) {
		// TODO#A7 Fill in this function.
		// 1) Loop through each light in the scene.
		// 2) If the intersection point is shadowed, skip the calculation for the light.
		//	  See Shader.java for a useful shadowing function.
		// 3) Compute the incoming direction by subtracting
		//    the intersection point from the light's position.
		// 4) Compute the color of the point using the CookTorrance shading model. Add this value
		//    to the output.
		Colord texColor = new Colord();
		for (Light light : scene.getLights()) {
			LightSamplingRecord lRec = new LightSamplingRecord();
			light.sample(lRec, iRec.location);
			if (!isShadowed(scene, lRec, iRec, new Ray (ray))) {
				Vector3d L = ray.origin.clone().sub(iRec.location);
				Vector3d V = ray.direction.clone().negate();
				Colord outColor = new Colord();
				evalBRDF(L, V, iRec.normal.clone(), diffuseColor, outColor);
				texColor.add(outColor.div(lRec.probability));
			}
			outIntensity.set(texColor);
		}
		
//		Colorf texColor = new Colorf();
//		if (texture != null) {
//			texColor = texture.getTexColor(new Vector2(record.texCoords));
//			setDiffuseColor(texColor);
//		}
//		
//		Vector3d outVec = new Vector3d();
//		for (Light light: scene.getLights()) {
//			if (!isShadowed(scene, light, record, new Ray(ray))){
//				Vector3d d2light = (new Vector3d(light.position.clone())).sub(record.location);
//				Vector3d n = record.normal;
//				double r2 = d2light.clone().dot(d2light);
//				double angle = Math.max(0d, d2light.clone().normalize().dot(n.normalize()));
//				outVec.add((new Vector3d(light.intensity)).div(r2).mul(getDiffuseColor()).mul(angle));
//			}
//		}
//		outIntensity.set((float) outVec.x, (float) outVec.y, (float) outVec.z);
		
	}

}