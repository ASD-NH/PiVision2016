package targeting.tower;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import boofcv.struct.PointIndex_I32;
import targeting.Target;

public class TowerTarget extends Target {
    public List<PointIndex_I32> m_bounds;
    
    public TowerTarget(List<PointIndex_I32> rawVertexes, Dimension cameraRes) {
        super(rawVertexes);
        m_boundsBox = new Dimension();
        m_bounds = findBounds(cameraRes);
    }
    
    public double getArea(){
    	int[] x = new int[4];
    	int[] y = new int[4];
    	double area;
    	
    	if(m_bounds.size() == 4){
	    	for(int i = 0; i < 4; i++){
	    		x[i] = m_bounds.get(i).x;
	    		y[i] = m_bounds.get(i).y;
	    	}
	    	
	    	area = 0.5 * (((x[0] - x[2]) * (y[1] - y[3])) - ((x[1] - x[3]) * (y[0] - y[2])));
    	}
    	else {
    		area = -1;
    	}
    	
    	return area;
    }
    
    public PointIndex_I32 getCenter(){
        
        int centerX = (m_bounds.get(0).x + m_bounds.get(2).x) / 2;
        int centerY = (m_bounds.get(1).y + m_bounds.get(3).y) / 2;
        
        return new PointIndex_I32(centerX, centerY, 0);
    }
    
    public List<PointIndex_I32> findBounds(Dimension maxSize){
    	return findBounds(maxSize.height, maxSize.width);
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
    
}
