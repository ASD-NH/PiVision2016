package targeting.geometry;

import boofcv.struct.PointIndex_I32;
import georegression.struct.point.Point2D_I32;

public class SegmentIndex_I32 extends Segment2D_I32 {
    
    public PointIndex_I32 point1;
    public PointIndex_I32 point2;
    
    //constructs using coordinates and index values
    public SegmentIndex_I32(int x1, int y1, int i1, int x2, int y2, int i2) {
        this(new PointIndex_I32(x1, y1, i1), new PointIndex_I32(x2, y2, i2));
    }
    
    //constructs using points and index values
    public SegmentIndex_I32(Point2D_I32 point1, int index1, Point2D_I32 point2, int index2) {
        this(new PointIndex_I32(point1.x, point1.y, index1), new PointIndex_I32(point2.x, point2.y, index2));
    }
    
    //constructs using points with index values included
    public SegmentIndex_I32(PointIndex_I32 point1, PointIndex_I32 point2) {
        super(point1.x, point1.y, point2.x, point2.y);
        this.point1 = point1;
        this.point2 = point2;
    }
    
    //accessors
    public PointIndex_I32 getPoint1() {
        return point1;
    }
    public PointIndex_I32 getPoint2() {
        return point2;
    }
    public int getIndexPoint1() {
        return point1.index;
    }
    public int getIndexPoint2() {
        return point2.index;
    }
    
}
