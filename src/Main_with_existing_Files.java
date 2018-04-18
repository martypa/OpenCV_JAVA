import org.opencv.core.Core;
import org.opencv.core.Mat;

public class Main_with_existing_Files {

    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        OpenCV openCV = new OpenCV();

        Mat image = openCV.imageRead("C:\\Users\\patrick.marty\\AustauschGWF\\T1_BadPictures\\image6.jpg");
        long time1 = System.currentTimeMillis();

        Mat img_right_position = openCV.doRightOrientation(image);

        Mat img = openCV.findDataMatrix(img_right_position);

        String s = openCV.completeTest(img);

        long time2 = System.currentTimeMillis();

        long time = time2 - time1;

        double timeD = time/1000.0;

        System.out.println("Datamatrix: " + s);
        System.out.println("Time: " + timeD + "s");
    }

}