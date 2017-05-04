package egl.math;
import java.util.Comparator;
import ray2.surface.Surface;

public final class SurfaceZComparator implements Comparator<Surface>{
    @Override
    public int compare(final Surface a, final Surface b) {
        return (a.getAveragePosition().z > b.getAveragePosition().z) ? 
        		1 : (a.getAveragePosition().z < b.getAveragePosition().z ? -1 : 0);
    }
}

