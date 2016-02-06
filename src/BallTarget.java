import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import boofcv.alg.shapes.ShapeFittingOps;
import boofcv.struct.PointIndex_I32;
import georegression.struct.point.Point2D_I32;
import georegression.struct.shapes.EllipseRotated_F64;

public class BallTarget extends Target {
	public EllipseRotated_F64 m_shape;
	private PointIndex_I32 m_center;
	
	public BallTarget(List<PointIndex_I32> rawVertexes, Dimension cameraRes) {
		super(rawVertexes, cameraRes);
		List<Point2D_I32> tempVertexes = new ArrayList<Point2D_I32>();
		
		for(PointIndex_I32 v : rawVertexes){
			tempVertexes.add(new Point2D_I32(v.x, v.y));
		}
		
		m_shape = ShapeFittingOps.fitEllipse_I32(tempVertexes, 0, true, null).shape;
		
		updateCenter();
	}
	
	private void updateCenter(){
		int x = (int) Math.round(m_shape.center.x);
		int y = (int) Math.round(m_shape.center.y);
		
		m_center = new PointIndex_I32(x, y, 0);
	}

	public BallTarget() {
		super();
		m_shape = new EllipseRotated_F64();
		m_center = new PointIndex_I32();
	}

	public PointIndex_I32 getCenter() {
		return m_center;
	}
	
	public double getAverageRadius() {
		double average;
		
		average = m_shape.a + m_shape.b;
		average /= 2;
		
		return average;
	}
}
