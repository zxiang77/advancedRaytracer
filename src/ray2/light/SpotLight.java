package ray2.light;

import egl.math.Vector3d;

public class SpotLight extends PointLight{
	// setting up beam angle
	public final Vector3d direction = new Vector3d();
	public void setDirection(Vector3d direction) { this.direction.set(direction); }
	
	public double beamAngle = 0.;
	public void setbeamAngle(double beamAngle) { this.beamAngle = beamAngle; }
	// setting up falloff angle
	public double falloffAngle = 0.;
	public void setFalloffAngle(double falloffAngle) { this.falloffAngle = falloffAngle; }

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
}
