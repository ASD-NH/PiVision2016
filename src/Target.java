import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import boofcv.struct.PointIndex_I32;

public class Target {
    public List<PointIndex_I32> m_rawVertexes;
    public Dimension m_boundsBox;
    public List<PointIndex_I32> m_bounds;
    public List<PointIndex_I32> m_largestSegment;
    
    public Target(List<PointIndex_I32> rawVertexes, Dimension boundsBox, List<PointIndex_I32> bounds){
        m_rawVertexes = rawVertexes;
        m_boundsBox = boundsBox;
        m_bounds = bounds;
    }
    
    public Target(){
        m_rawVertexes = new ArrayList<PointIndex_I32>();
        m_boundsBox = new Dimension();
        m_bounds = new ArrayList<PointIndex_I32>();
    }
    
    public PointIndex_I32 getCenter(){
        
        int centerX = (m_bounds.get(0).x + m_bounds.get(2).x) / 2;
        int centerY = (m_bounds.get(1).y + m_bounds.get(3).y) / 2;
        
        return new PointIndex_I32(centerX, centerY, 0);
    }
    
    public List<PointIndex_I32> findBounds(int maxHeight, int maxWidth){
        List<PointIndex_I32> result = new ArrayList<PointIndex_I32>();
        
        int minX = maxWidth;
        int minY = maxHeight;
        int maxX = 0;
        int maxY = 0;
        
        for(PointIndex_I32 p : m_rawVertexes){
            minX = Math.min(p.x, minX);
            minY = Math.min(p.y, minY);
            maxX = Math.max(p.x, maxX);
            maxY = Math.max(p.y, maxY);
        }
        
        int width = maxX - minX;
        int height = maxY - minY;
        
        PointIndex_I32 center = new PointIndex_I32(minX + (width / 2), minY + (height / 2), 0);
        
        for(int i = 0; i < 4; i++){
            result.add(new PointIndex_I32(center.x, center.y, i));
        }
        
        for(PointIndex_I32 p : m_rawVertexes){
            if(p.x < center.x){
                if(p.y < center.y){
                    if(center.distance(p) > center.distance(result.get(0))){
                        result.set(0, p);
                    }
                }
                else {
                    if(center.distance(p) > center.distance(result.get(3))){
                        result.set(3, p);
                    }
                }
            }
            else {
                if(p.y < center.y){
                    if(center.distance(p) > center.distance(result.get(1))){
                        result.set(1, p);
                    }
                }
                else {
                    if(center.distance(p) > center.distance(result.get(2))){
                        result.set(2, p);
                    }
                }
            }
        }
        
        if(m_boundsBox != null){
            m_boundsBox.width = result.get(0).distance2(result.get(1));
            m_boundsBox.height = result.get(1).distance2(result.get(2));
        }
        
        return result;
    }
    
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
