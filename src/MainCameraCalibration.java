import org.opencv.core.Core;
import org.opencv.core.Mat;

public class MainCameraCalibration {

    public static void main(String[] args) throws Exception {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        SSH ftp = new SSH();
        OpenCV openCV = new OpenCV();

        ftp.takeRemotePicture();

        ftp.copyRemoteFile("/home/pi/Pictures/Development/image.jpg","C:\\Users\\patrick.marty\\Pictures\\OpenCV_JAVA");

        Mat image = openCV.imageRead("C:\\Users\\patrick.marty\\Pictures\\OpenCV_JAVA\\image.jpg");

        image = openCV.doRightOrientation(image);

        openCV.detectLogo(image);


    }

}