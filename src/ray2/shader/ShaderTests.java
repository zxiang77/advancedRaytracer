package ray2.shader;

import static org.junit.Assert.*;

import org.junit.Test;

import ray2.IntersectionRecord;
import ray2.light.PointLight;
import ray2.Ray;
import ray2.Scene;
import ray2.accel.AccelStruct;
import ray2.accel.NaiveAccelStruct;
import ray2.camera.Camera;
import ray2.camera.PerspectiveCamera;
import ray2.surface.Sphere;
import ray2.surface.Surface;
import egl.math.Colord;
import egl.math.Matrix4d;
import egl.math.Vector3d;

public class ShaderTests {
    
    @Test
    public void testFresnel() {
        Vector3d normal = new Vector3d(1, 1, 1);
        Vector3d outgoing = new Vector3d(1, 1, 1);
        double refractiveIndex = 2.0f;
        CookTorrance shader = new CookTorrance();
        
        double result;
        result = shader.fresnel(normal, outgoing, refractiveIndex);
        
        assertTrue("Fresnel:\n"
                + "normal: " + normal + "\n"
                + "outgoing: " + outgoing + "\n"
                + "refractiveIndex: " + refractiveIndex + "\n"
                + "Expected: 0.1549192\n"
                + "Got: " + result, doublesEqual(0.1549192, result));
        
        outgoing.set(-1, 1, 0);
        result = shader.fresnel(normal, outgoing, refractiveIndex);
        assertTrue("Fresnel:\n"
                + "normal: " + normal + "\n"
                + "outgoing: " + outgoing + "\n"
                + "refractiveIndex: " + refractiveIndex + "\n"
                + "Expected: 1.0\n"
                + "Got: " + result, doublesEqual(1.0, result));
        
        normal.set(1, 2, 0);
        result = shader.fresnel(normal, outgoing, refractiveIndex);
        assertTrue("Fresnel:\n"
                + "normal: " + normal + "\n"
                + "outgoing: " + outgoing + "\n"
                + "refractiveIndex: " + refractiveIndex + "\n"
                + "Expected: 0.111111111\n"
                + "Got: " + result, doublesEqual(0.111111111, result));
        
        refractiveIndex = 5.0;
        result = shader.fresnel(normal, outgoing, refractiveIndex);
        assertTrue("Fresnel:\n"
                + "normal: " + normal + "\n"
                + "outgoing: " + outgoing + "\n"
                + "refractiveIndex: " + refractiveIndex + "\n"
                + "Expected: 0.44444444\n"
                + "Got: " + result, doublesEqual(0.44444444, result));
    }

    @Test
    public void testCookTorrance() {
        // Create surface
        Sphere s = new Sphere();
        s.setTransformation(new Matrix4d(), new Matrix4d(), new Matrix4d());
        
        // Create camera and get a ray.
        Camera cam = new PerspectiveCamera();
        cam.setViewDir(new Vector3d(0, 0, -1));
        cam.setViewUp(new Vector3d(0, 1, 0));
        cam.setViewPoint(new Vector3d(0, 0, 2));
        cam.setProjNormal(new Vector3d(0, 0, -1));
        
        Ray ray = new Ray();
        cam.getRay(ray, 0.5, 0.5);
        
        // Create a light source
        PointLight light0 = new PointLight();
        light0.setPosition(new Vector3d(1, 1, 3));
        
        // Set accel struct -> naive
        AccelStruct naive = new NaiveAccelStruct();
        Surface surfaces[] = {s};
        naive.build(surfaces);
        
        // Set up the scene
        Scene scene = new Scene();
        scene.addSurface(s);
        scene.setCamera(cam);
        scene.addLight(light0);
        scene.setAccelStruct(naive);
        
        // Set the intersection record
        IntersectionRecord its = new IntersectionRecord();
        its.location.set(0, 0, 1);
        its.normal.set(0, 0, 1);
        its.surface = s;
        its.t = 1.0;
        
        // Create the shader
        CookTorrance shader = new CookTorrance();
        shader.setRefractiveIndex(2.0f);
        
        // Begin testing
        Colord outIntensity = new Colord();
        shader.shade(outIntensity, scene, ray, its, 0);
        assertTrue(shader + "\n"
                + "Expected: <0.14399356, 0.14399356, 0.14399356>\n"
                + "got: " + outIntensity,
                colorsEqual(new Colord(0.14399356, 0.14399356, 0.14399356), outIntensity));
        
        // Make diffuse color red
        shader.setDiffuseColor(new Colord(0.7, 0.2, 0.1));
        shader.shade(outIntensity, scene, ray, its, 0);
        assertTrue(shader + "\n"
                + "Expected: <0.10316873, 0.03512735, 0.02151907>\n"
                + "got: " + outIntensity,
                colorsEqual(new Colord(0.10316873, 0.03512735, 0.02151907), outIntensity));
        
        // Make specular color red and diffuse color white.
        shader.setDiffuseColor(new Colord(1, 1, 1));
        shader.setSpecularColor(new Colord(0.7, 0.2, 0.1));
        shader.shade(outIntensity, scene, ray, its, 0);
        assertTrue(shader + "\n"
                + "Expected: <0.1416203, 0.1376649, 0.13687384>\n"
                + "got: " + outIntensity,
                colorsEqual(new Colord(0.1416203, 0.1376649, 0.13687384), outIntensity));
        
        // Make specular white, diffuse red, and roughness 5.0
        shader.setDiffuseColor(new Colord(0.7, 0.2, 0.1));
        shader.setSpecularColor(new Colord(1, 1, 1));
        shader.setRoughness(5.0);
        shader.shade(outIntensity, scene, ray, its, 0);
        assertTrue(shader + "\n"
                + "Expected: <0.0956066, 0.02756521, 0.013956933>\n"
                + "got: " + outIntensity,
                colorsEqual(new Colord(0.0956066, 0.02756521, 0.013956933), outIntensity));
        
        // Add an occluder
        Sphere occluder = new Sphere();
        occluder.setCenter(light0.position);
        occluder.setTransformation(new Matrix4d(), new Matrix4d(), new Matrix4d());
        Surface surfaces0[] = {s, occluder};
        scene.addSurface(occluder);
        naive.build(surfaces0);
        scene.setAccelStruct(naive);
        
        shader.setDiffuseColor(new Colord(1, 1, 1));
        shader.setSpecularColor(new Colord(1, 1, 1));
        shader.setRoughness(2.0);
        shader.shade(outIntensity, scene, ray, its, 0);
        assertTrue(shader + "\nWith occluder blocking all lights\n"
                + "Expected: <0, 0, 0>\n"
                + "got: " + outIntensity,
                colorsEqual(new Colord(0, 0, 0), outIntensity));
        
        // Add a second light
        PointLight light1 = new PointLight();
        light1.setPosition(new Vector3d(0, 0, 2));
        scene.addLight(light1);
        shader.shade(outIntensity, scene, ray, its, 0);
        assertTrue(shader + "\nWith occluder blocking one of two lights\n"
                + "Expected: <1.0088494, 1.0088494, 1.0088494>\n"
                + "got: " + outIntensity,
                colorsEqual(new Colord(1.0088494, 1.0088494, 1.0088494), outIntensity));
    }
    
    // Simple element-wise comparison.
    private boolean colorsEqual(Colord v0, Colord v1) {
        double epsilon = 1e-4;
        return (Math.abs(v0.x - v1.x) < epsilon &&
                Math.abs(v0.y - v1.y) < epsilon && 
                Math.abs(v0.z - v1.z) < epsilon);
    }
    
    private boolean doublesEqual(double d0, double d1) {
        double epsilon = 1e-4;
        return Math.abs(d0 - d1) < epsilon;
    }

}
