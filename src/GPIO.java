import com.pi4j.io.gpio.*;

/**
* The GPIO class controlled the GPIO headers from the Raspberry PI.
* @author patrick marty
**/
public class GPIO {

    final private GpioPinDigitalInput myInput;
    final private GpioPinDigitalOutput myOutput;

    public GPIO() {
        GpioController gpio = GpioFactory.getInstance();
        this.myInput = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07,PinPullResistance.PULL_UP);
        this.myOutput = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02,PinState.LOW);
    }
    
    /**
    * This method return the Pin29 GPIO Digital Input.
    * @return myInput     gives the GpioPinDigitalInput from Pin29
    **/
    public GpioPinDigitalInput getInput_GPIO29(){
        return myInput;
    }
    
    public GpioPinDigitalOutput getOutput_GPIO06(){
        return myOutput;
    }

}
