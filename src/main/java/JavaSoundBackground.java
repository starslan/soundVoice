import javax.sound.sampled.*;

import java.io.*;
import java.util.Arrays;

public class JavaSoundBackground {
    ByteArrayOutputStream
            byteArrayOutputStream;
    AudioFormat audioFormat;
    TargetDataLine targetDataLine;
    AudioInputStream audioInputStream;
    SourceDataLine sourceDataLine;

    public static void main(String[] args) {
        new JavaSoundBackground();

    }
    public JavaSoundBackground(){
        AudioFileFormat.Type[] dfr = AudioSystem.getAudioFileTypes();
//        System.out.println(Arrays.stream(AudioSystem.getAudioFileTypes()).toString());
        captureAudio();
    }

    private  AudioFormat getAudioFormat(){
        float sampleRate = 8000.0F;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(
                sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);
    }

    private void captureAudio(){
        try{
            //Установим все для захвата

            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo =
                    new DataLine.Info(
                            TargetDataLine.class,
                            audioFormat);
            targetDataLine = (TargetDataLine)
                    AudioSystem.getLine(
                            dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            //Создаем поток для захвата аудио
            // и запускаем его
            //он будет работать
            //пока не нажмут кнопку
            Thread captureThread =
                    new Thread(
                            new CaptureThread()
                    );
            captureThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    class CaptureThread extends Thread{

        byte tempBuffer[] = new byte[10000];
        public void run(){
            byteArrayOutputStream =
                    new ByteArrayOutputStream();

            try{


                while(true){


                    int cnt = targetDataLine.read(
                            tempBuffer,
                            0,
                            tempBuffer.length);
                    if(cnt > 0){
                        //Сохраняем данные в выходной поток

                        byteArrayOutputStream.write(
                                tempBuffer, 0, cnt);
                    }

                    System.out.println(byteArrayOutputStream.size());
                    if(byteArrayOutputStream.size() > 500000){

                        try(OutputStream outputStream = new FileOutputStream("thefilename.wav")) {
//                            byteArrayOutputStream.writeTo(outputStream);
                            AudioInputStream audioStream = new AudioInputStream(targetDataLine);
                            File file = new File("thefilename");
                            AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
                            AudioSystem.write(audioStream, fileType, file);
                        }
                        break;
                    }
                }
                byteArrayOutputStream.close();
            }catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }
}


