package ray2.shader;

import ray2.IntersectionRecord;
import ray2.Ray;
import ray2.Scene;
import ray2.light.Light;
import ray2.light.LightSamplingRecord;
import ray2.light.SpotLight;
import egl.math.Color;
import egl.math.Colord;
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
		
		outIntensity.setZero();
		for (Light light : scene.getLights()) {
//			System.out.println("[BRDFshader]: getting lights");
			LightSamplingRecord lRec = new LightSamplingRecord();
			light.sample(lRec, iRec.location);
//			System.out.println(iRec.location);
			if (!isShadowed(scene, lRec, iRec, new Ray())) {
				Vector3d L = lRec.direction.clone().normalize();
				Vector3d V = ray.direction.clone().negate().normalize();
				Vector3d N = iRec.normal.clone().normalize();
				double cos = Math.max(0, L.clone().dot(N));
				Colord outColor = new Colord();
				evalBRDF(L, V, N, diffuseColor, outColor);
				Vector3d intensity = light.intensity.clone();
				
				// extension : SpotLight
				if (light instanceof SpotLight) {
					Vector3d lDir = ((SpotLight) light).direction.clone().normalize();
					double thetaF = ((SpotLight) light).falloffAngle;
					double thetaB = ((SpotLight) light).beamAngle;
					double angle = Math.acos(lRec.direction.clone().normalize().negate().dot(lDir));
					double ratio = 1d;
					if (angle <= thetaB) ratio = 1d;
					else if(angle <= thetaF) ratio = -(angle - thetaF) / (thetaF - thetaB);
					else ratio = 0;
					intensity.mul(ratio);
				}
				
				outIntensity.add(outColor.mul(intensity).mul(cos).mul(lRec.attenuation).div(lRec.probability));
			}
		}

	}
}