package core;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import boofcv.gui.feature.VisualizeShapes;
import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;
import georegression.struct.point.Point2D_I32;
import georegression.struct.shapes.EllipseRotated_F64;

public class DebugDisplay {
    
    private ImagePanel m_display;
    Dimension m_resolution;
    private BufferedImage m_image;
    
    //used for drawing shapes
    private Graphics2D m_graphics;
    //set this variable for drawing a polygon
    private List<Point2D_I32> m_polyPoints;
    //set this variable for drawing an ellipse
    private EllipseRotated_F64 m_ellipse;
    //set this to draw an oval around the center point
    private Point2D_I32 m_centerPoint;
    //color to draw with
    private Color m_color;
    
    DebugDisplay(Dimension res, String title) {
        
        m_display = new ImagePanel();
        m_resolution = res;
        
        //init display window
        m_display.setPreferredSize(m_resolution);
        ShowImages.showWindow(m_display, title, true);
        
    }
    
    //set source to the RGB image (to be displayed)
    void setImageRGB(MultiSpectral<ImageUInt8> source) {
        
        //convert to displayable format
        m_image = ConvertBufferedImage.convertTo_U8(source, m_image, true);
        
        //if there is a anything to draw, initialize graphics
        if (m_polyPoints != null || m_ellipse != null || m_centerPoint != null) {
            //link graphics
            m_graphics = m_image.createGraphics();
            //init core graphics settings
            m_graphics.setStroke(new BasicStroke(2));
            m_graphics.setColor(m_color);
        }
        
        //draw polygon if applicable
        if (m_polyPoints != null) {
            VisualizeShapes.drawPolygon(m_polyPoints, true, m_graphics);
        }
        //draw ellipse if applicable
        if (m_ellipse != null) {
            double averageRadius = (m_ellipse.a + m_ellipse.b) / 2;
            m_graphics.drawOval((int) (m_ellipse.center.x - averageRadius),
                       (int) (m_ellipse.center.y - averageRadius),
                       (int) averageRadius * 2,
                       (int) averageRadius * 2);
        }
        //draw centerpoint if applicable
        if (m_centerPoint != null) {
            m_graphics.drawOval((int) (m_centerPoint.x - 2.5), (int) (m_centerPoint.y - 2.5), 5, 5);
        }
        
        //set image
        m_display.setBufferedImageSafe(m_image);
        
    }
    
    void setPolygon(List<Point2D_I32> points) {
        m_polyPoints = points;
    }
    void setEllipse(EllipseRotated_F64 ellipse) {
        m_ellipse = ellipse;
    }
    void setColor(Color color) {
        m_color = color;
    }
    
    void clearBuffer(){
    	m_polyPoints = null;
    	m_ellipse = null;
    }
}