package targeting;
import java.util.List;

import boofcv.struct.PointIndex_I32;
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
    public static void smoothContour(List<PointIndex_I32> v, int minLength) {
        if(v.size() > 0){
           //if the distance between any two sequential points is < minLength, remove that point
           for (int i = v.size() - 2; i >= 0; i--) {
               if (v.get(i).distance(v.get(i + 1)) < minLength) {
                   v.remove(i + 1);
               }
           }
           //test the distance between the last point and the first
           if (v.get(v.size() - 1).distance(v.get(0)) < minLength) {
               v.remove(v.size() - 1);
           }
        }
    }
    
}