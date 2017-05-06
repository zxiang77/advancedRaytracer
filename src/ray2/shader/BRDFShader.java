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
import egl.math.Vector2d;
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
		if (texture != null) {
			texColor = texture.getTexColor(new Vector2d(iRec.texCoords));
			setDiffuseColor(texColor);
		}
		
		for (Light light : scene.getLights()) {
			LightSamplingRecord lRec = new LightSamplingRecord();
			light.sample(lRec, iRec.location);
			if (!isShadowed(scene, lRec, iRec, new Ray(ray))) {
				Vector3d L = lRec.direction.clone().normalize();
				Vector3d V = ray.direction.clone().negate().normalize();
				Vector3d N = iRec.normal.clone().normalize();
				double cos = Math.max(0, L.clone().dot(N));
				Colord outColor = new Colord();
				evalBRDF(L, V, N, diffuseColor, outColor);
				texColor.add(outColor.mul(light.intensity).mul(cos).mul(lRec.attenuation).div(lRec.probability));
			} else {
//				System.out.println("shade: shadow");
			}
			outIntensity.set((float) texColor.x, (float) texColor.y, (float) texColor.z);
		}
	}
}