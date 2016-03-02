package targeting;
import java.awt.Dimension;
import java.util.List;

import boofcv.struct.PointIndex_I32;
import targeting.geometry.GeometryUtils;
import targeting.geometry.Segment2D_I32;

public abstract class Target {
    //vertexes of targets
    public List<PointIndex_I32> m_rawVertexes;
    public Segment2D_I32 m_largestSegment;
	public Dimension m_boundsBox = new Dimension();
	
    public Target(List<PointIndex_I32> rawVertexes){
        m_rawVertexes = rawVertexes;
    }
    
    public abstract PointIndex_I32 getCenter();
    
    //calculates the largest segment and stores it in m_largestSegment
    public void calculateLargestSegment(){ 
        for(int i = 0; i < m_rawVertexes.size() - 1; i++){
            Segment2D_I32 segment = new Segment2D_I32(m_rawVertexes.get(i), m_rawVertexes.get(i + 1));
            
            if (m_largestSegment == null) {
                m_largestSegment = segment;
            }
            else if(segment.getLength() > m_largestSegment.getLength()){
                m_largestSegment = segment;
            }
        }
    }
    
    public double largestAngle(){
        double largest = 0;
        
        for(int i = 1; i < m_rawVertexes.size(); i++){
            
            Segment2D_I32 segmentA = new Segment2D_I32(m_rawVertexes.get(i - 1), m_rawVertexes.get(i));
            Segment2D_I32 segmentB;
            if (i < m_rawVertexes.size() - 1) {
                segmentB = new Segment2D_I32(m_rawVertexes.get(i), m_rawVertexes.get(i + 1));
            }
            else {
                segmentB = new Segment2D_I32(m_rawVertexes.get(i), m_rawVertexes.get(0));
            }
            Segment2D_I32 segmentC = new Segment2D_I32(segmentA.getPoint1(), segmentB.getPoint2());
            
            double angle = GeometryUtils.lawOfCosines(segmentA, segmentB, segmentC);
            largest = Math.max(largest, angle);
            
        }
        
        return largest;
    }
    
    
}
