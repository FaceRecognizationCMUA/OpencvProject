import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;
import static org.bytedeco.javacpp.opencv_contrib.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

/**
 *
 * Source: http://pcbje.com/2012/12/doing-face-recognition-with-javacv/
 *
 * @author Petter Christian Bjelland
 * @author Samuel Audet
 */
public class Recognizer {

    public boolean status;
    public static FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();
//             FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
//         FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
//         FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();

    /**
     * A method to recognize the face. Edited by Guangyao Xie.
     * @author Guangyao Xie
     * @param imgPath the path of the image to be recognized.
     * @return the label.
     */
    public static int recognize(String imgPath) {
        Mat testImage = imread("img_resized\\cut_image.jpg", CV_LOAD_IMAGE_GRAYSCALE);
        int predictedLabel = faceRecognizer.predict(testImage);
        int[] ints = new int[1];
        double[] pconfidence = new double[1];
        faceRecognizer.predict(testImage, ints, pconfidence);
        Main.distance = pconfidence[0];
        System.out.println(pconfidence[0]);//print the confidence
        System.out.println("Predicted label: " + predictedLabel);
        return predictedLabel;
    }

    /**
     * A method to train picture to recognizer.
     *
     * @author Petter Christian Bjelland
     * @author Samuel Audet
     * @param trainingDir the directory of training set.
     * @throws Exception
     */
    public static void train(String trainingDir) throws Exception {
        File root = new File(trainingDir);
        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
            }
        ;
        };
        File[] imageFiles = root.listFiles(imgFilter);// files in the training folder
        MatVector images = new MatVector(imageFiles.length);
        Mat labels = new Mat(imageFiles.length, 1, CV_32SC1);
        IntBuffer labelsBuf = labels.getIntBuffer();
        int counter = 0;
        for (File image : imageFiles) {

            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

            int label = Integer.parseInt(image.getName().split("\\-")[0]);
            images.put(counter, img);
            labelsBuf.put(counter, label);
            counter++;
        }

        faceRecognizer.train(images, labels);
    }
}
