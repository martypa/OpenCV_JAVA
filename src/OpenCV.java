import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.datamatrix.DataMatrixReader;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class OpenCV {

    private JFrame frame;
    private double angle = 0;


    public OpenCV() {


    }

    public void writeImage(Mat image){

        Imgcodecs.imwrite("/home/pi/Pictures/cropImage.jpg",image);
    }

    public Mat imageRead(String path){
        Mat img = Imgcodecs.imread(path);
        return img;
    }

    public void showImage(Image img2){
        //BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
        ImageIcon icon=new ImageIcon(img2);
        this.frame=new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        byte ba[]=mob.toArray();

        BufferedImage bi= ImageIO.read(new ByteArrayInputStream(ba));
        return bi;
    }

    public Mat doRightOrientation(Mat m){

        Mat thres = new Mat();

        Mat m2 = m;

        Imgproc.cvtColor(m,thres, Imgproc.COLOR_RGB2GRAY);

        Imgproc.threshold(thres,thres,120,255, Imgproc.THRESH_BINARY);

        List<MatOfPoint> points = new ArrayList<>();

        Mat hierarchy = new Mat();

        RotatedRect rect2 = null;

        Imgproc.findContours(thres, points, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        for(int i = 0; i<points.size();i++){
            Rect rect = Imgproc.boundingRect(points.get(i));
            if((rect.width > 900 && rect.width < 1500) && ((rect.height > 250 && rect.height < 500))) {
                //Imgproc.rectangle(m2, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                MatOfPoint2f pointf = new MatOfPoint2f();
                points.get(i).convertTo(pointf, CvType.CV_32FC2);
                rect2 = Imgproc.minAreaRect(pointf);
            }
        }

        Mat rotateImage = new Mat();


        double angle = 0;
        this.angle = angle;

        if(rect2 != null){
            angle = Math.abs(rect2.angle);
            if(angle > 45){
                angle = 90-angle;
            }else{
               angle = rect2.angle;
            }
        }

        this.angle = angle;

        int rows = m2.rows();
        int cols = m2.cols();

        Size size = m2.size();

        rotateImage = Imgproc.getRotationMatrix2D(new Point(cols/2,rows/2),angle,1);
        Imgproc.warpAffine(m2,m2,rotateImage,size);

        return m2;
    }

    public Mat findDataMatrix(Mat m){


        Mat gray = new Mat();

        Imgproc.cvtColor(m,gray, Imgproc.COLOR_RGB2GRAY);

        int x = 1100;
        int y = 150;

        if(angle > 3){
            x = x+150;
        }


        Rect rectCrop = new Rect(x,y,500,600);

        Mat cropImage = new Mat(gray,rectCrop);


        Mat imgThres = new Mat();

        Imgproc.threshold(cropImage,imgThres,120,255, Imgproc.THRESH_BINARY);

        Mat imgErod = new Mat();

        int erosion_size = 10;

        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1*erosion_size + 1, 1*erosion_size+1));


        Imgproc.erode(imgThres,imgErod,element);

        Mat imgThresInv = new Mat();

        Imgproc.threshold(imgErod,imgThresInv,120,255, Imgproc.THRESH_BINARY_INV);

        Mat imgErodInv = new Mat();

        int erosion_size2 = 10;

        Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1*erosion_size2 + 1, 1*erosion_size2+1));

        Imgproc.erode(imgThresInv,imgErodInv,element2);

        Mat imagePreparedFinish = new Mat();

        Imgproc.threshold(imgErodInv,imagePreparedFinish,120,255, Imgproc.THRESH_BINARY_INV);

        List<MatOfPoint> points = new ArrayList<>();

        Mat hierarchy = new Mat();

        Imgproc.findContours(imagePreparedFinish, points, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        Mat imageBounding = cropImage;
        Rect matrixCrop = new Rect();

        int widthMin = 250;
        int widthMax = 350;
        int heightMin = 250;
        int heightMax = 350;


        for(int i = 0; i<points.size();i++){
            Rect rect = Imgproc.boundingRect(points.get(i));
            if((rect.width > widthMin && rect.width < widthMax) && ((rect.height > heightMin && rect.height < heightMax))) {
                //Imgproc.rectangle(imageBounding, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                matrixCrop.height = rect.height + 20;
                matrixCrop.width = rect.width + 20;
                matrixCrop.x = rect.x - 10;
                matrixCrop.y = rect.y -10;
            }
        }


        Mat imgMatrix = new Mat(cropImage,matrixCrop);

        writeImage(imgMatrix);

        return imgMatrix;
    }

    public Mat prepareDataMatrix(Mat m, int erosion_size){

        Mat imgThres = new Mat();

        Imgproc.threshold(m,imgThres,120,255, Imgproc.THRESH_BINARY);

        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1*erosion_size + 1, 1*erosion_size +1));

        Imgproc.morphologyEx(imgThres,imgThres, Imgproc.MORPH_OPEN,element);

        Imgproc.morphologyEx(imgThres,imgThres, Imgproc.MORPH_CLOSE,element);

        return imgThres;
    }

    public String decodeDataMatrix(BufferedImage image){

        LuminanceSource source = new BufferedImageLuminanceSource(image);

        BinaryBitmap tmpBitmap = new BinaryBitmap(new HybridBinarizer(source));

        DataMatrixReader reader = new DataMatrixReader();

        boolean decodeOK = true;

        String matrixText = null;

        Result result = null;

        try {
            result = reader.decode(tmpBitmap);
        } catch (NotFoundException e) {
            decodeOK = false;
        } catch (ChecksumException e) {
            decodeOK = false;
        } catch (FormatException e) {
            decodeOK = false;
        }

        if(decodeOK){
            matrixText = result.getText();
        }


        return matrixText;

    }

    public String completeTest(Mat m){

        int index = 1;
        String decodeString;

        do {
            Mat prepareMat = prepareDataMatrix(m, index);

            try {
                showImage(Mat2BufferedImage(prepareMat));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                decodeString = decodeDataMatrix(Mat2BufferedImage(prepareMat));
            } catch (IOException e) {
                e.printStackTrace();
                decodeString = null;
            } catch (Exception e) {
                e.printStackTrace();
                decodeString = null;
            }

            index++;
        }while(decodeString == null && index < 11);


        return decodeString;
    }

    public Mat findRolPacket(Mat m){

        Mat origin = m;

        Mat gray = new Mat();

        Imgproc.cvtColor(m,gray, Imgproc.COLOR_RGB2GRAY);

        Mat imgThres = new Mat();

        Imgproc.threshold(gray,imgThres,120,255, Imgproc.THRESH_BINARY);

        Mat imgErod = new Mat();

        int erosion_size = 10;

        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1*erosion_size + 1, 1*erosion_size+1));

        Imgproc.erode(imgThres,imgErod,element);

        List<MatOfPoint> points = new ArrayList<>();

        Mat hierarchy = new Mat();

        Imgproc.findContours(imgErod, points, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);


        Rect muellerLogo = new Rect();

        int widthMin = 1000;
        int widthMax = 1200;
        int heightMin = 200;
        int heightMax = 300;


        for(int i = 0; i<points.size();i++){
            Rect rect = Imgproc.boundingRect(points.get(i));
            if((rect.width > widthMin && rect.width < widthMax) && ((rect.height > heightMin && rect.height < heightMax))) {
                //Imgproc.rectangle(origin, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                muellerLogo.height = rect.height+100;
                muellerLogo.width = rect.width+200;
                muellerLogo.x = rect.x+330;
                muellerLogo.y = rect.y+325;
            }
        }
        Mat cropImage = new Mat(origin,muellerLogo);



        return cropImage;



    }

    public int countBlackWheels(Mat m){

       Mat origin2 = m;

        Mat gray = new Mat();

        Imgproc.cvtColor(m,gray, Imgproc.COLOR_RGB2GRAY);

        Rect rolle4Rect = new Rect();
        rolle4Rect.x = 300;
        rolle4Rect.y = 50;
        rolle4Rect.height = 280;
        rolle4Rect.width = 150;

        Rect rolle3Rect = new Rect();
        rolle3Rect.x = 560;
        rolle3Rect.y = 50;
        rolle3Rect.height = 280;
        rolle3Rect.width = 150;

        Rect rolle2Rect = new Rect();
        rolle2Rect.x = 820;
        rolle2Rect.y = 50;
        rolle2Rect.height = 280;
        rolle2Rect.width = 150;

        Mat rolle4 = new Mat(gray,rolle4Rect);
        Mat rolle3 = new Mat(gray,rolle3Rect);
        Mat rolle2 = new Mat(gray,rolle2Rect);


        Imgproc.threshold(rolle4,rolle4,120,255, Imgproc.THRESH_BINARY);
        Imgproc.threshold(rolle3,rolle3,120,255, Imgproc.THRESH_BINARY);
        Imgproc.threshold(rolle2,rolle2,120,255, Imgproc.THRESH_BINARY);


        int erosion_size = 5;
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1*erosion_size, 1*erosion_size));
        Imgproc.morphologyEx(rolle4,rolle4,Imgproc.MORPH_OPEN,element);
        Imgproc.morphologyEx(rolle3,rolle3,Imgproc.MORPH_OPEN,element);
        Imgproc.morphologyEx(rolle2,rolle2,Imgproc.MORPH_OPEN,element);

        int[] numberWhitePixel = new int[3];

        numberWhitePixel[0] = Core.countNonZero(rolle4);
        numberWhitePixel[1] = Core.countNonZero(rolle3);
        numberWhitePixel[2] = Core.countNonZero(rolle2);

        int numberOfBlackWheels = 1;

        for(int i = 0; i < numberWhitePixel.length; i++){
            if(numberWhitePixel[i] < 10000){
                numberOfBlackWheels++;
            }
        }

        return  numberOfBlackWheels;

    }



}
