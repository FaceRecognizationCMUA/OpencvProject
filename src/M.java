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
import java.io.FilenameFilter;
import java.sql.*;
import java.util.ArrayList;
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

    static double distance;
    private File file = null;
//    static String username = "admin";
//    static String password = "cmua2014";
    static boolean flag = true;
    static final int WIDTH = 134;
    static final int HEIGHT = 148;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
//    static JFrame frame = new JFrame("WebCam Capture - Face detection");
//    static FaceDetector fd= new FaceDetector();
//    static FacePanel facePanel = new FacePanel();
//    

    /**
     * Call the real-time camera and resize the image to the size of
     * WIDTH*HEIGHT. The resized image is stored in the folder "img_resized".
     *
     * @throws Exception
     */
    public static String realtimeCamera() throws Exception {
        System.out.println("Camera is called!");
        String destPath = "";
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //or ...     System.loadLibrary("opencv_java244");       
        //make the JFrame
        JFrame frame = new JFrame("WebCam Capture - Face detection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FaceDetector fd = new FaceDetector();
        FacePanel facePanel = new FacePanel();

        frame.setSize(400, 400);
        frame.setBackground(Color.BLUE);
        frame.add(facePanel, BorderLayout.CENTER);
//        
        frame.setVisible(true);
        facePanel.setVisible(true);
        facePanel.validate();

//        Thread t = new Thread();
        //Open and Read from the video stream  
        Mat webcam_image = new Mat();
        VideoCapture webCam = new VideoCapture(0);
        if (webCam.isOpened()) {
//            Thread.sleep(500); /// This one-time delay allows the Webcam to initialize itself  
            while (M.flag) {
                webCam.read(webcam_image);
                if (!webcam_image.empty()) {
//                    Thread.sleep(200); /// This delay eases the computational load .. with little performance leakage
                    System.out.println("CAMERA: " + Thread.currentThread());
                    frame.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
                    //Apply the classifier to the captured image  
                    Mat temp = webcam_image;
                    temp = fd.detect(webcam_image);
                    //Display the image --------BUG
                    facePanel.matToBufferedImage(temp);
                    System.out.println("Image buffered!");
                    facePanel.repaint();
                    System.out.println("Panel repainted!");
                    System.out.println(facePanel.isVisible());
//                    System.out.println("visibility:"+facePanel.isVisible());//true
//                    System.out.println("enabled?"+facePanel.isEnabled());//true
//                    System.out.println("validity?"+facePanel.isValid());//true
                    MatOfByte mb = new MatOfByte();
                    Highgui.imencode(".jpg", webcam_image, mb);
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(mb.toArray()));
                    destPath = "build\\classes\\cam_img\\capture.jpg";
                    File file = new File(destPath);
                    ImageIO.write(image, "JPEG", file);

                } else {
                    System.out.println(" --(!) No captured frame from webcam !");
                    break;
                }
            }
        }
        webCam.release(); //release the webcam
        String imgPath = resize(destPath);
        flag = true;
        frame.dispose();
        return imgPath;
    }

    public static int createLabelInput(String andrewid) {
        File DBroot = new File("photodb_resized/");
        FilenameFilter imgFilter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
            }
        };
        File[] imageFiles = DBroot.listFiles(imgFilter);

        int i = 0;
        ArrayList<Integer> labelList = new ArrayList<>();
        for (File f : imageFiles) {
            String aid = f.getName().split("\\.|\\-")[1];

            if (aid.split("\\d+")[0].equals(andrewid) && aid.equals(andrewid) == false) {
                int d = Integer.parseInt(aid.split("\\D+")[1]);
                labelList.add(d);
                //System.out.println(d);
            }
        }
        if (!labelList.isEmpty()) {
            i = labelList.get(labelList.size() - 1);
            System.out.println("max label is " + i);
            System.out.println("new label is " + (i + 1));
            return i + 1;
        } else {
            return i + 1;
        }

    }

    /**
     * Resize the certain image to required size (WIDTH*HEIGHT).
     *
     * @param imgPath the path of the image.
     * @return the path of the resized image.
     * @throws Exception
     */
    public static String resize(String imgPath) throws Exception {
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
        int count = 1;
        String dir = "";
        for (Rect rect : faceDetections.toArray()) {
            ImageFilter cropFilter = new CropImageFilter(rect.x, rect.y, rect.width, rect.height);
            BufferedImage tag = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
//            File file = new File("build\\classes\\cam_img\\test.jpg");
            File file = new File(imgPath);
            BufferedImage src = ImageIO.read(file);
            Image img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(src.getSource(), cropFilter));
            BufferedImage output = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics g = output.getGraphics();
            g.drawImage(img, 0, 0, WIDTH, HEIGHT, null);
            g.dispose();
            dir = "img_resized\\cut_image.jpg";
//            String dir = "trainset\\57-tx\\57-"+(count++)+".jpg";
            File dest = new File(dir);
            ImageIO.write(output, "JPEG", dest);
        }
        return dir;
    }

    public static String resize(String imgPath, String andrewId, int trainImageCount) throws Exception {
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
        int count = 1;
        String dir = "";
        for (Rect rect : faceDetections.toArray()) {
            ImageFilter cropFilter = new CropImageFilter(rect.x, rect.y, rect.width, rect.height);
            BufferedImage tag = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
//            File file = new File("build\\classes\\cam_img\\test.jpg");
            File file = new File(imgPath);
            BufferedImage src = ImageIO.read(file);
            Image img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(src.getSource(), cropFilter));
            BufferedImage output = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics g = output.getGraphics();
            g.drawImage(img, 0, 0, WIDTH, HEIGHT, null);
            g.dispose();
            int st_no = findLabel("photodb_resized\\", andrewId);
            dir = "photodb_resized\\" + st_no + "-" + andrewId + trainImageCount + ".jpg";
//            String dir = "trainset\\57-tx\\57-"+(count++)+".jpg";
            File dest = new File(dir);
            ImageIO.write(output, "JPEG", dest);
        }
        return dir;
    }

    /**
     * Find the label by andrew id.
     *
     * @param trainDBdir directory of training set.
     * @param andrewid andrew id of student.
     * @return the label of student.
     */
    public static int findLabel(String trainDBdir, String andrewid) {
        File DBroot = new File(trainDBdir);
        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
            }
        };
        File[] imageFiles = DBroot.listFiles(imgFilter);
        int label = 0;
        for (File f : imageFiles) {
            String aid = f.getName().split("\\-|\\.")[1];
            //System.out.println(aid);
            if (aid.equals(andrewid)) {
                label = Integer.parseInt(f.getName().split("\\-")[0]);
            }
        }
        return label;
    }

    static void init() {
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
////        facePanel.setVisible(false);
//        frame.setSize(400, 400);
//        frame.setBackground(Color.BLUE);
//        frame.add(facePanel, BorderLayout.CENTER);
////        frame.setVisible(false);
//        frame.setEnabled(true);
//        facePanel.setSize(400, 400);
//        facePanel.setEnabled(true);
//        facePanel.setVisible(false);
    }

    /**
     * This method receives parameter of andrew ID. It checks if this aID exist
     * or not. If not, create a label by finding the biggest stu_no in database.
     *
     * @author Guangyao Xie
     * @param andrewid
     * @return a label number for a new comer
     * @throws Exception
     */
    public static int createNewLabel(String andrewid) throws Exception {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            DB.DBconnect();
            String sql0 = String.format("SELECT stu_no from opencv.student WHERE andrew_id = '%s'", andrewid);
            String sql1 = String.format("SELECT MAX(stu_no) FROM opencv.student;");
            DB.stmt = DB.conn.createStatement();
            boolean isExist = false;
            int label = 0;
            DB.rs = DB.stmt.executeQuery(sql0);
            // rs0.first();
            while (DB.rs.next()) {

                if (DB.rs.getRow() != 0) {
                    isExist = true;
                    throw new Exception("aid_exist");
                }
            }
            DB.rs.close();

            if (isExist == false) {
                DB.rs = DB.stmt.executeQuery(sql1);
                while (DB.rs.next()) {
                    label = Integer.parseInt(DB.rs.getString(1)) + 1;//create new label
                }
                DB.rs.close();
            }

            return label;

        } catch (SQLException se) {
            System.out.println(se);
            int label = -1;
            return label;
        } catch (Exception aid_exist) {
            int label = -2;
            return label;
        } finally {
            DB.conn.close();
            System.out.println("End connection");
        }

    }

    /**
     * This method executes insert query to the db
     *
     * @author Guangyao Xie
     * @param label
     * @param name
     * @param andrewid
     * @param program
     * @param gender
     * @throws Exception
     */
    public static void writeNewComerInfo(int label, String name, String andrewid,
            String program, String gender) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        DB.sql = String.format("INSERT INTO opencv.student (stu_no, andrew_id, stu_name, program, gender)"
                + "VALUES (%d, '%s', '%s', '%s', '%s');", label, name, andrewid, program, gender);

        DB.stmt = DB.conn.createStatement();
        DB.stmt.execute(DB.sql);
        DB.conn.close();
        System.out.println("Write in DB");
    }

    public static void main(String[] args) throws Exception {
//        M main=new M();
        int sno;
//        init();
//        System.out.println(findLabel("photodb","hongl"));
        Window w = new Window();
        System.out.println("MAIN: " + Thread.currentThread());
        new Window().setVisible(true);

        OpenCVFaceRecognizer.train("photodb_resized");
//        realtimeCamera();
//        DB.connectDB();
//        sno=OpenCVFaceRecognizer.recognize(realtimeCamera());

    }
}
