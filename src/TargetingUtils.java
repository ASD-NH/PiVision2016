import java.util.List;

import boofcv.struct.PointIndex_I32;
import georegression.struct.shapes.EllipseRotated_F64;

public class TargetingUtils {
    public static EllipseRotated_F64 largestArea(List<EllipseRotated_F64> ellipses){
        EllipseRotated_F64 largest = null;
        
        for(EllipseRotated_F64 e : ellipses){
            if(largest == null){
                largest = e;
            }
            else {
                double largeArea = Math.PI * largest.a * largest.b;
                double newArea = Math.PI * e.a * e.b;
                
                if(newArea > largeArea){
                    largest = e;
                }
            }
        }
        
        return largest;
    }
    
}