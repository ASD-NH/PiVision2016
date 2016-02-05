import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import boofcv.struct.PointIndex_I32;

abstract class Target {
    public List<PointIndex_I32> m_rawVertexes;
    public List<PointIndex_I32> m_largestSegment;
	public Dimension m_boundsBox;
    
    public Target(List<PointIndex_I32> rawVertexes, Dimension cameraRes){
        m_rawVertexes = rawVertexes;
        m_boundsBox = new Dimension();
    }
    
    public Target(){
        m_rawVertexes = new ArrayList<PointIndex_I32>();
        m_boundsBox = new Dimension();
    }
    
    public abstract PointIndex_I32 getCenter();
    
    public double findLargestSegment(){
        double longestLength = 0;
        
        for(int i = 0; i < m_rawVertexes.size() - 1; i++){
            double length = m_rawVertexes.get(i).distance(m_rawVertexes.get(i + 1));
            if(length > longestLength){
                longestLength = length;
                if(m_largestSegment != null){
                    m_largestSegment.clear();
                    m_largestSegment.add(m_rawVertexes.get(i));
                    m_largestSegment.add(m_rawVertexes.get(i + 1));
                }
            }
        }
        
        return longestLength;
    }
    
    public double largestAngle(){
        double largest = 0;
        
        for(int i = 1; i < m_rawVertexes.size(); i++){
            PointIndex_I32 last = m_rawVertexes.get(i - 1);
            PointIndex_I32 current = m_rawVertexes.get(i);
            PointIndex_I32 next;
            if(i < m_rawVertexes.size() - 1){
                next = m_rawVertexes.get(i + 1);
            }
            else {
                next = m_rawVertexes.get(0);
            }

            double sideA = last.distance(current);
            double sideB = current.distance(next);
            double sideC = last.distance(next);
            
            double angle = Math.acos((Math.pow(sideA, 2) + Math.pow(sideB, 2) - Math.pow(sideC, 2)) / (2 * sideA * sideB));
            
            largest = Math.max(largest, angle);
        }
        
        return largest;
    }
}
