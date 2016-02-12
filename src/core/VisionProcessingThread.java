package core;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import boofcv.struct.*;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;
import georegression.struct.point.Point2D_F32;
import georegression.struct.point.Point2D_I32;
import georegression.struct.shapes.EllipseRotated_F64;
import server.NetUtils;
import targeting.ImageConversion;
import targeting.TargetingUtils;
import targeting.ValueHistory;
import targeting.ball.BallTarget;
import targeting.tower.TowerTarget;
import boofcv.alg.color.ColorHsv;
import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.feature.detect.edge.EdgeContour;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.PixelMath;
import boofcv.alg.shapes.ShapeFittingOps;
import boofcv.alg.shapes.FitData;
import boofcv.core.image.ConvertImage;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.gui.image.*;
import boofcv.io.image.ConvertBufferedImage;
import java.net.*;
import targeting.ValueHistory;;

public class VisionProcessingThread extends Thread{

   //camera resolution
   private Dimension m_camRes; 
   //core interface and webcam variables
   private CameraInterface m_webcam;
   private MultiSpectral<ImageUInt8> m_image;

   //loop as long as this is true
   private boolean m_running = true;

   //do different things depending on the target
   Constants.TargetType m_target;

   //debug display object
   private DebugDisplay m_display;
   public static boolean m_showDisplay = true;

   //history of values found
   ValueHistory m_targetHistory;

   public VisionProcessingThread(int camIndex, Constants.TargetType target){
      this("VisionProcessingThread", camIndex, target);
   }
   public VisionProcessingThread(String name, int camIndex, Constants.TargetType target){
      super(name);
      //set target
      m_target = target;
      //init history for data
      m_targetHistory = new ValueHistory(m_target);
      //init webcam
      if(target == Constants.TargetType.ball){
         m_camRes = Constants.MED_RES;
      }
      else {
         m_camRes = Constants.MAX_RES;
      }
      m_webcam = new CameraInterface(camIndex, m_camRes);
      //init display
      if(m_showDisplay) {
         System.out.println("[INFO] Initializing display");

         //error catching if for example the system is headless
         try {
            String title = "Targeting: ";
            if(target == Constants.TargetType.ball){
               title += "Ball";
            }
            else {
               title += "Tower";
            }
            m_display = new DebugDisplay(m_camRes, title);
            System.out.println("[INFO] Display initializion succeeded");
         } catch (Exception e) {
            System.out.println("[ERROR] Cannot initialize display, continuing without one...");
         }
      }
   }

   public void run() {
      while(m_running) {
         //m_image is the current webcam image
         m_image = m_webcam.getImage();

         //the array to send to the rio
         int[] values;

         //Find the appropriate target
         if (m_target == Constants.TargetType.tower) {
            values = findTower();
         }
         else { //equals ball
            values = findBall();
         }
         
         System.out.println("values presend" + Arrays.toString(values));
         //Send data to RIO
         NetUtils.SendValues(values);

         if (m_showDisplay) {
            //update the image on the display
            m_display.setImageRGB(m_image);
         }
      }
   }

   
   
   //code to find the tower
   private int[] findTower() {
      int[] towerData = new int[9];

      //image to store thresholded image
      ImageUInt8 filtered = new ImageUInt8(m_image.width, m_image.height);

      //value to threshold by
      int thresholdVal = 200;
      
      //thresholds all color channels by thresholdVal
      for(int x = 0; x < m_image.width; x++){
         for(int y = 0; y < m_image.height; y++){
            int redPixelVal = m_image.getBand(0).get(x, y);
            int greenPixelVal = m_image.getBand(1).get(x, y);
            int bluePixelVal = m_image.getBand(2).get(x, y);
            
            if (redPixelVal > thresholdVal && greenPixelVal > thresholdVal && bluePixelVal > thresholdVal) {
                filtered.set(x, y, 1);
            }
            else {
                filtered.set(x, y, 0);
            }
         }
      }

      //edge detects the thresholded image
      CannyEdge<ImageUInt8, ImageSInt16> canny = FactoryEdgeDetectors.canny(2, true, true, ImageUInt8.class, ImageSInt16.class);
      canny.process(filtered, 0.1f, 0.3f, filtered);
      
      //make a copy of the image here for display purposes
      ImageUInt8 displayer = filtered.clone();
      PixelMath.multiply(displayer, 255, displayer);
      //create some objects for drawing the image to the screen
      BufferedImage gImage = new BufferedImage(filtered.width, filtered.height, 4);
      Graphics2D g = gImage.createGraphics();
      g.setStroke(new BasicStroke(2));
      ConvertBufferedImage.convertTo_U8(m_image, gImage, true);
      
      //get a list of contours from the thresholded image
      List<Contour> contours = BinaryImageOps.contour(filtered, ConnectRule.EIGHT, null);
      //list of potential towers
      List<TowerTarget> targets = new ArrayList<TowerTarget>();
      
      for(Contour c : contours){
         List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(c.external,true,0.05,0,100);
         
         TargetingUtils.smoothContour(vertexes, 10);
         
         TowerTarget possibleTarget = new TowerTarget(vertexes, m_camRes);
         double largeAngle = possibleTarget.largestAngle();
         
         if(vertexes.size() == 8
               && largeAngle > 1.5 && largeAngle < 1.8){
            targets.add(possibleTarget);
            
            g.setColor(Color.CYAN);
            g.drawString(String.valueOf(largeAngle), possibleTarget.getCenter().x, possibleTarget.getCenter().y);
         }
      }

      //we don't want to look for the central target. We should assume the target can be anywhere. tsk tsk
      TowerTarget centralTarget = null;
      PointIndex_I32 screenCenter = new PointIndex_I32(m_camRes.width / 2, m_camRes.height / 2, 0);

      for(TowerTarget t : targets){
         if(centralTarget == null){
            centralTarget = t;
         }
         else {
            PointIndex_I32 oldCenter = centralTarget.getCenter();
            PointIndex_I32 newCenter = t.getCenter();

            if(newCenter.distance(screenCenter) < oldCenter.distance(screenCenter)){
               centralTarget = t;
            }
         }
      }

      if(centralTarget != null && m_showDisplay){
         g.setColor(Color.GREEN);
         VisualizeShapes.drawPolygon(centralTarget.m_bounds, true, g);
         g.drawOval((int)(centralTarget.getCenter().x - 2.5), (int)(centralTarget.getCenter().y - 2.5), 5, 5);
      }

      m_image = ImageConversion.toMultiSpectral(gImage);

      /*
       * Format for data:
       *  -tower or ball (tower = 0)
       *  -top left x
       *  -top left y
       *  -top right x
       *  -top right y
       *  -bottom left x
       *  -bottom left y
       *  -bottom right x
       *  -bottom right y
       */
      towerData[0] = Constants.TOWER_FLAG;
      if(centralTarget != null) {
         towerData[1] = centralTarget.m_bounds.get(0).x;
         towerData[2] = centralTarget.m_bounds.get(0).y;
         towerData[3] = centralTarget.m_bounds.get(1).x;
         towerData[4] = centralTarget.m_bounds.get(1).y;
         towerData[5] = centralTarget.m_bounds.get(3).x;
         towerData[6] = centralTarget.m_bounds.get(3).y;
         towerData[7] = centralTarget.m_bounds.get(2).x;
         towerData[8] = centralTarget.m_bounds.get(2).y;
      }
      else {
         for(int i = 1; i < towerData.length; i++){
            towerData[i] = 0;
         }
      }

      m_targetHistory.updateHistory(towerData);
      System.out.println(Arrays.toString(m_targetHistory.m_currData));

      return m_targetHistory.m_currData;
   }

   //code to find the ball
   private int[] findBall() {
      int[] ballData = new int[Constants.BALL_SIZE];

      MultiSpectral<ImageFloat32> hsvImage = new MultiSpectral<ImageFloat32>(ImageFloat32.class, m_camRes.width, m_camRes.height, 3);
      ImageUInt8 valueBand = new ImageUInt8(m_camRes.width, m_camRes.height);

      //sets m_hsvImage to the hsv version of the source image
      ColorHsv.rgbToHsv_F32(
            ImageConversion.MultiSpectralUInt8ToFloat32(m_image),
            hsvImage);

      //extracts just the value band from the hsv image
      ConvertImage.convert(hsvImage.getBand(2), valueBand);
      //valueBand = m_image.getBand(0).clone();

      //threshold the image to make the ball clear
      valueBand = ThresholdImageOps.localSquare(valueBand, null, 20, 0.98f, true, null, null);

      //edge detect to locate the ball
      CannyEdge<ImageUInt8, ImageSInt16> canny = FactoryEdgeDetectors.canny(2,true, true, ImageUInt8.class, ImageSInt16.class);
      canny.process(valueBand, 0.1f, 0.3f, valueBand);
      
      //objects and settings to display the found ball on the display window
      BufferedImage gImage = null;
      Graphics2D g = null;
      if(m_showDisplay){
         gImage = new BufferedImage(valueBand.width, valueBand.height, 4);
         g = gImage.createGraphics();
         g.setStroke(new BasicStroke(2));

         ConvertBufferedImage.convertTo_U8(m_image, gImage, true);
      }

      //creates contours from the edge detection done earlier
      List<Contour> contours = BinaryImageOps.contour(valueBand, ConnectRule.EIGHT, null);

      //list to store all valid ellipses
      List<BallTarget> validEllipses = new ArrayList<BallTarget>();

      //get the center of the screen to use for vertical deviation calculations
      PointIndex_I32 screenCenter = new PointIndex_I32(m_camRes.width / 2, m_camRes.height / 2, 0);

      for(Contour c : contours){
         List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(c.external, false, 0.05, 0, 100);

         BallTarget circle = new BallTarget(vertexes, m_camRes);

         double largestAngle = circle.largestAngle();

         double ratio = circle.m_shape.a / circle.m_shape.b;
         if(ratio < 1){
            ratio = 1 / ratio;
         }

         double averageRadius = (circle.m_shape.a + circle.m_shape.b) / 2;

         int verticalDeviation = Math.abs(screenCenter.y - circle.getCenter().y);

         if(ratio < 1.2 && averageRadius > 30
               && verticalDeviation < 20){
            validEllipses.add(circle);
         }
      }

      BallTarget ball = TargetingUtils.largestArea(validEllipses);

      if(m_showDisplay){
         m_display.clearBuffer();

         if(ball != null){
            m_display.setColor(Color.CYAN);
            m_display.setEllipse(ball.m_shape);
         }

         m_image = ImageConversion.toMultiSpectral(gImage);
      }

      ballData[0] = Constants.BALL_FLAG;
      if(ball != null){
         ballData[1] = (int)Math.round(ball.getAverageRadius());
         ballData[2] = ball.getCenter().x;
         ballData[3] = ball.getCenter().y;
      }
      else {
         for(int i = 1; i < ballData.length; i++){
            ballData[i] = 0;
         }
      }

      m_targetHistory.updateHistory(ballData);
      System.out.println(Arrays.toString(m_targetHistory.m_currData));

      return m_targetHistory.m_currData;
   }

}