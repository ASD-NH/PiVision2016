package targeting.geometry;

public class GeometryUtils {
    
    //returns angle c in rads
    public static double lawOfCosines(Segment2D_I32 segmentA, Segment2D_I32 segmentB, Segment2D_I32 segmentC) {
        double sideA = segmentA.getLength();
        double sideB = segmentB.getLength();
        double sideC = segmentC.getLength();
        
        double angle = Math.acos((Math.pow(sideA, 2) + Math.pow(sideB, 2) - Math.pow(sideC, 2)) / (2 * sideA * sideB));
        
        return angle;
    }
    
}
