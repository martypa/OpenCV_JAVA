import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.enums.AWB;
import com.hopding.jrpicam.enums.Exposure;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;
import com.pi4j.io.gpio.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.Buffer;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

public class Controller implements Initializable {

    private GpioController gpioController;
    private GpioPinDigitalOutput lamp;
    private RPiCamera piCamera;
    private OpenCV openCV;
    private BlockingQueue<BufferedImage> imageBuffer;

    @FXML
    private ImageView imageTest;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        gpioController = GpioFactory.getInstance();
        lamp = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_02,PinState.LOW);
        try {
            piCamera = new RPiCamera();
        } catch (FailedToRunRaspistillException e) {
            e.printStackTrace();
        }
        openCV = new OpenCV();
        //piCamera.setWidth(400).setHeight(400).setExposure(Exposure.BACKLIGHT).setAWB(AWB.FLASH).setSharpness(70)
          //      .setContrast(0).setBrightness(50).setSaturation(50).setTimeout(1).setQuality(50).setFullPreviewOff().setPreviewFullscreen(false).setPreviewOpacity(0).setISO(0);

        piCamera.setQuality(30).setPreviewOpacity(0).setFullPreviewOff().setDateTimeOff().setTimeout(1);

        imageBuffer = new LinkedBlockingDeque<>(10);
    }



    @FXML
    Button start;

    @FXML
    void takeImage(ActionEvent event) throws InterruptedException {
        lamp.high();
        new Thread(task2).start();
        new Thread(task).start();
    }

    Task task = new Task<Void>(){
        @Override
        protected Void call() throws Exception {
            do {
                if (imageBuffer.size() > 5) {
                    Image image = SwingFXUtils.toFXImage(imageBuffer.take(), null);
                    imageTest.setImage(image);
                    imageBuffer.remove(0);
                }
            }while (true);
        }
    };

    Task task2 = new Task<Void>(){
        @Override
        protected Void call() throws Exception {
            do{
               BufferedImage bufferImage = piCamera.takeBufferedStill(1920,1080);
               imageBuffer.put(bufferImage);
            }while (true);
        }
    };




}
