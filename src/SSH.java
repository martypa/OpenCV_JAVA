import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SSH {



    public SSH() {
    }


    public boolean copyRemoteFile(String filePath, String localPath){

        JSch jsch = new JSch();
        Session session = null;

        boolean ok = true;

        try {
            session = jsch.getSession("pi", "172.30.105.151", 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword("gwf");
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.get(filePath, localPath);
            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
            ok = false;
        } catch (SftpException e) {
            ok = false;
        }

        return ok;
    }


    public void takeRemotePicture(){
        try {
            JSch js = new JSch();
            Session s = js.getSession("pi", "172.30.105.151", 22);
            s.setPassword("gwf");
            s.setConfig("StrictHostKeyChecking", "no");
            s.connect();


            Channel c = s.openChannel("exec");
            ChannelExec ce = (ChannelExec) c;
            ce.setCommand("gpio mode 2 out && gpio write 2 1");
            ce.setErrStream(System.err);
            ce.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ce.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            ce.disconnect();



            Channel c2 = s.openChannel("exec");
            ChannelExec ce2 = (ChannelExec) c2;
            ce2.setCommand("sudo raspistill -o /home/pi/Pictures/Development/image.jpg --nopreview --exposure backlight --awb flash --sharpness 70 --contrast 0 --brightness 50 --saturation 50 --ev 3 --width 1920 --height 1080 -t 1500 -q 100");
            ce2.setErrStream(System.err);
            ce2.connect();
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(ce2.getInputStream()));
            String line2;
            while ((line2 = reader2.readLine()) != null) {
                System.out.println(line2);
            }
            ce2.disconnect();



            Channel c3 = s.openChannel("exec");
            ChannelExec ce3 = (ChannelExec) c3;
            ce3.setCommand("gpio write 2 0");
            ce3.setErrStream(System.err);
            ce3.connect();
            BufferedReader reader3 = new BufferedReader(new InputStreamReader(ce3.getInputStream()));
            String line3;
            while ((line3 = reader3.readLine()) != null) {
                System.out.println(line3);
            }
            ce3.disconnect();




            s.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
