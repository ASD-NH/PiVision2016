package targeting;
import java.util.List;

import boofcv.alg.filter.binary.Contour;
import boofcv.struct.PointIndex_I32;
import georegression.struct.point.Point2D_I32;
import georegression.struct.shapes.EllipseRotated_F64;
import targeting.ball.BallTarget;

public class TargetingUtils {
    
    public static BallTarget largestArea(List<BallTarget> targets){
        BallTarget largest = null;
        
        for(BallTarget e : targets){
            if(largest == null){
                largest = e;
            }
            else {
                double largeArea = Math.PI * largest.m_shape.a * largest.m_shape.b;
                double newArea = Math.PI * e.m_shape.a * e.m_shape.b;
                
                if(newArea > largeArea){
                    largest = e;
                }
            }
        }
        
        return largest;
    }
    
    //smooths out a contour by removing small segments (length lower than minLength)
    public static Contour smoothContour(Contour c, int minLength) {
        Contour output = c;
        
        //if the distance between any two sequential points is < minLength, remove that point
        for (int i = c.external.size() - 2; i >= 0; i--) {
            if (c.external.get(i).distance(c.external.get(i + 1)) < minLength) {
                c.external.remove(i + 1);
            }
        }
        //test the distance between the last point and the first
        if (c.external.get(c.external.size() - 1).distance(c.external.get(0)) < minLength) {
            c.external.remove(c.external.size() - 1);
        }
        
        return output;
    }
    
}