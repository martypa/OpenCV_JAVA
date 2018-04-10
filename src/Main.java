
import org.opencv.core.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {





    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        SSH ftp = new SSH();
        OpenCV openCV = new OpenCV();

        ftp.takeRemotePicture();

        ftp.copyRemoteFile("/home/pi/Pictures/Development/image.jpg","C:\\Users\\patrick.marty\\Pictures\\OpenCV_JAVA");

        Mat image = openCV.imageRead("C:\\Users\\patrick.marty\\Pictures\\OpenCV_JAVA\\image.jpg");

        Mat img_right_position = openCV.doRightOrientation(image);

        Mat rolPacket = openCV.findRolPacket(img_right_position);

        int blackWheels = 0;

        try {
          blackWheels = openCV.countBlackWheels(rolPacket);
        }catch (Exception ex){
        }

        System.out.println("Es gibt " + blackWheels + " schwarze Wheels");

        Mat dataMatrix = openCV.findDataMatrix(img_right_position);

        String matrixCode = null;

        try{
            matrixCode = openCV.completeTest(dataMatrix);
        }catch (Exception ex){
        }

        System.out.println("Datamatrix Code: " + matrixCode);


        try {
            Files.delete(Paths.get("C:\\Users\\patrick.marty\\Pictures\\OpenCV_JAVA\\image.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
