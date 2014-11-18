/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author xgy
 */


import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import javax.imageio.ImageIO;
import org.opencv.core.Core;  
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;  
import org.opencv.core.Point;  
import org.opencv.core.Rect;  
import org.opencv.core.Scalar;  
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;  
import org.opencv.objdetect.CascadeClassifier;
public class FaceDetector {
    private CascadeClassifier face_cascade;  
     // Create a constructor method  
    public FaceDetector(){   
        face_cascade=new CascadeClassifier("haarcascade_frontalface_alt.xml"); 
          if(face_cascade.empty())  
          {  
               System.out.println("--(!)Error loading A\n");  
                return;  
          }  
          else  
          {
              System.out.println("Face classifier loooaaaaaded up");  
          }  
     }  
     
     
     public Mat detect(Mat inputframe) throws Exception{  
          Mat mRgba=new Mat();  
          Mat mGrey=new Mat();  
          MatOfRect faces = new MatOfRect();  
          inputframe.copyTo(mRgba);  
          inputframe.copyTo(mGrey);  
          Imgproc.cvtColor( mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);  
          Imgproc.equalizeHist( mGrey, mGrey );  
          face_cascade.detectMultiScale(mGrey, faces);  
          for(Rect rect:faces.toArray())  
          {  
//              ImageFilter cropFilter = new CropImageFilter(rect.x, rect.y, rect.width, rect.height);
//              BufferedImage tag = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
//            File file = new File("src\\facedetect\\test.jpg");
//            BufferedImage src = ImageIO.read(file);
//            Image img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(src.getSource(), cropFilter));
//            BufferedImage output = new BufferedImage(134,148, BufferedImage.TYPE_INT_RGB);
//            Graphics g = output.getGraphics();
//            g.drawImage(img, 0, 0,134,148,null);
//            g.dispose();
//            String dir = "D:\\java\\Github\\JavaCVFaceRecSample\\editpic\\cut_image.jpg";
//            String dir = "cut_image.jpg";
//            File f = new File(dir);
//            ImageIO.write(output, "JPEG", f);
              
              
              
               Point center= new Point(rect.x + rect.width*0.5, rect.y + rect.height*0.5 );  
               //draw a blue eclipse around face
               Core.ellipse( mRgba, center, new Size( rect.width*0.5, rect.height*0.5), 0, 0, 360, new Scalar( 255, 0, 255 ), 4, 8, 0 );
          }  
          return mRgba;  
     }  
}
