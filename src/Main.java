
import org.opencv.core.*;

public class Main {





    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        SSH ftp = new SSH();

        ftp.takeRemotePicture();



        /*penCV openCV = new OpenCV();

        Mat image = openCV.imageRead("C:\\Users\\patrick.marty\\Downloads\\Images\\image2.jpg");

        Mat img2 = openCV.doRightOrientation(image);

        Mat imgMatrix = openCV.findDataMatrix(img2);

        String s = openCV.completeTest(imgMatrix);

        System.out.println(s);*/




    }






}
