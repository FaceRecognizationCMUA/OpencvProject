/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
/**
 *
 * @author Sky Xu <Sky Xu at Carnegie Mellon University>
 */
public class M {
    private static final String URL = "jdbc:mysql://opencvdb.cxsp5jskrofy.us-west-2.rds.amazonaws.com:3306/opencv";
    private static String sql="";
    private static Connection conn = null;
    private static PreparedStatement pstmt = null;
    private static ResultSet rs = null;
    private File file = null;
    static String username = "admin";
    static String password = "cmua2014";
    static boolean flag=true;
    static final int WIDTH=134;
    static final int HEIGHT=148;
    static{ 
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
    }
    public static void connect(){
        try {
            conn = DriverManager.getConnection(URL, username, password);
            System.out.println("Connected!");
        }
        catch(SQLException se){
            System.out.println(se);
        }
    }
    public static void nonRealtimeCamera() throws Exception{
        System.out.println("Hello, OpenCV");//test
        Mat frame = new Mat();
        VideoCapture cap = new VideoCapture(0);                     
        Thread.sleep(500);	// 0.5 sec of a delay. This is not obvious but its necessary as the camera simply needs time to initialize itself
        if(!cap.isOpened()){
            System.out.println("Did not connect to camera");
        }
        else 
            System.out.println("found webcam: "+ cap.toString());
        cap.retrieve(frame);// The current frame in the camera is saved in "frame"
        System.out.println("Captured image with "+ frame.width()+ " pixels wide by "         		  										+ frame.height() + " pixels tall.");
        Highgui.imwrite("cam_img\\me1.jpg", frame);
        Mat frameBlur = new Mat();
        Imgproc.blur(frame, frameBlur, new Size(5,5) );
        Highgui.imwrite("me2-blurred.jpg", frameBlur);
        cap.release(); // Remember to release the camera
    }
    /**
     * Call the real-time camera and resize the image to the size of WIDTH*HEIGHT.
     * The resized image is stored in the folder "img_resized".
     * @throws Exception 
     */
    public String realtimeCamera() throws Exception {
        String destPath="";
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //or ...     System.loadLibrary("opencv_java244");       
        //make the JFrame
        JFrame frame = new JFrame("WebCam Capture - Face detection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FaceDetector fd= new FaceDetector();
        FacePanel facePanel = new FacePanel();
        frame.setSize(400, 400); 
        frame.setBackground(Color.BLUE);
        frame.add(facePanel, BorderLayout.CENTER);
        frame.setVisible(true);
        //Open and Read from the video stream  
        Mat webcam_image = new Mat();
        VideoCapture webCam = new VideoCapture(0);
        if (webCam.isOpened()) {
            Thread.sleep(500); /// This one-time delay allows the Webcam to initialize itself  
            while (flag) {
                webCam.read(webcam_image);
                if (!webcam_image.empty()) {
                    Thread.sleep(200); /// This delay eases the computational load .. with little performance leakage
                    frame.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
                    //Apply the classifier to the captured image  
                    Mat temp = webcam_image;
                    webcam_image = fd.detect(webcam_image);
                    //Display the image  
                    facePanel.matToBufferedImage(webcam_image);
                    facePanel.repaint();
                    MatOfByte mb = new MatOfByte();
                    Highgui.imencode(".jpg", temp, mb);
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(mb.toArray()));
                    destPath="build\\classes\\cam_img\\capture.jpg";
                    File file = new File(destPath);
                    ImageIO.write(image, "JPEG", file);
                    
                } else {
                    System.out.println(" --(!) No captured frame from webcam !");
                    break;
                }
            }
        }
        webCam.release(); //release the webcam
        String imgPath=resize(destPath);
        return imgPath;
    }
    /**
     * Resize the certain image to required size (WIDTH*HEIGHT).
     * @param imgPath the path of the image.
     * @return the path of the resized image.
     * @throws Exception 
     */
    public static String resize(String imgPath)throws Exception{
        System.out.println("\nRunning DetectFaceDemo");
        String xmlfilePath = FaceDetector.class.getResource("haarcascade_frontalface_alt.xml").getPath().substring(1);
        System.out.println(xmlfilePath);//test
        CascadeClassifier faceDetector = new CascadeClassifier(xmlfilePath);
//        String imgPath=FaceDetector.class.getResource("cam_img/test.jpg").getPath().substring(1);
        Mat image = Highgui.imread(imgPath);
        System.out.println(imgPath);
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);
        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
        int count=1;
        String dir="";
        for (Rect rect : faceDetections.toArray()) {
            ImageFilter cropFilter = new CropImageFilter(rect.x, rect.y, rect.width, rect.height);
            BufferedImage tag = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
//            File file = new File("build\\classes\\cam_img\\test.jpg");
            File file = new File(imgPath);
            BufferedImage src = ImageIO.read(file);
            Image img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(src.getSource(), cropFilter));
            BufferedImage output = new BufferedImage (WIDTH,HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics g = output.getGraphics();
            g.drawImage(img, 0, 0,WIDTH,HEIGHT,null);
            g.dispose();
            dir = "img_resized\\cut_image.jpg";
//            String dir = "trainset\\57-tx\\57-"+(count++)+".jpg";
            File dest = new File(dir);
            ImageIO.write(output, "JPEG", dest);
        }
        return dir;
    }
    static void initDB(){
        
        sql="insert into student values";
    }
        
    
    public static void main(String[] args) throws Exception{
        M main=new M();
        int sno;
//        Window w=new Window();
//        w.setVisible(true);
//        connect();
         OpenCVFaceRecognizer.train("photodb_resized");
        sno=OpenCVFaceRecognizer.recognize(main.realtimeCamera());  
    }
}
