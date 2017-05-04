package egl.math;
import java.util.Comparator;
import ray2.surface.Surface;

public final class SurfaceXComparator implements Comparator<Surface>{
    @Override
    public int compare(final Surface a, final Surface b) {
        return (a.getAveragePosition().x > b.getAveragePosition().x) ? 
        		1 : (a.getAveragePosition().x < b.getAveragePosition().x ? -1 : 0);
    }
}

