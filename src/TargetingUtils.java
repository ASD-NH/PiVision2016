import java.util.List;

import boofcv.struct.PointIndex_I32;
import georegression.struct.shapes.EllipseRotated_F64;

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
    
}