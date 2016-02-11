package targeting.geometry;

import georegression.struct.point.Point2D_I32;

public class Segment2D_I32 {
    
    public Point2D_I32 point1;
    public Point2D_I32 point2;
    
    public double length;
    
    //constructs using coordinates and index values
    public Segment2D_I32(int x1, int y1, int x2, int y2) {
        this(new Point2D_I32(x1, y1), new Point2D_I32(x2, y2));
    }
    
    //constructs using points and index values
    public Segment2D_I32(Point2D_I32 point1, Point2D_I32 point2) {
        this.point1 = point1;
        this.point2 = point2;
    }
    
    //calculates length and sets it to var length
    protected void setLength() {
        length = Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2));
    }
    
    //accessors
    public Point2D_I32 getPoint1() {
        return point1;
    }
    public Point2D_I32 getPoint2() {
        return point2;
    }
    public double getLength() {
        return length;
    }
}
