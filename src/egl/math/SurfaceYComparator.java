package egl.math;
import java.util.Comparator;
import ray2.surface.Surface;

public final class SurfaceYComparator implements Comparator<Surface>{
    @Override
    public int compare(final Surface a, final Surface b) {
        return (a.getAveragePosition().y > b.getAveragePosition().y) ? 
        		1 : (a.getAveragePosition().y < b.getAveragePosition().y ? -1 : 0);
    }
}

