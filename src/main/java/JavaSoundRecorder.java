import javax.sound.sampled.*;
import java.io.*;
import java.nio.ByteBuffer;


public class JavaSoundRecorder {

    static final long RECORD_TIME = 6000; //milliseconds


    File wavFile = new File("RecordAudio.wav");
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    TargetDataLine line;
//    byte[] audioData ;
//    byte tempBuffer[] = new byte[10000];
//    ByteArrayOutputStream byteArrayOutputStream =
//            new ByteArrayOutputStream();


    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }


    void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);


            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            System.out.println("Start capturing...");

            AudioInputStream ais = new AudioInputStream(line);

//            AudioSystem.write(ais, fileType,  byteArrayOutputStream);


            System.out.println("Start recording...");


            AudioSystem.write(ais, fileType, wavFile);

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    void finish() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }


    public static void main(String[] args) {
        final JavaSoundRecorder recorder = new JavaSoundRecorder();

        Thread stopper = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(RECORD_TIME);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                recorder.finish();
            }
        });

        stopper.start();
        recorder.start();
    }

    public double volumeRMS(double[] raw) {
        double sum = 0d;
        if (raw.length==0) {
            return sum;
        } else {
            for (int ii=0; ii<raw.length; ii++) {
                sum += raw[ii];
            }
        }
        double average = sum/raw.length;

        double sumMeanSquare = 0d;
        for (int ii=0; ii<raw.length; ii++) {
            sumMeanSquare += Math.pow(raw[ii]-average,2d);
        }
        double averageMeanSquare = sumMeanSquare/raw.length;
        double rootMeanSquare = Math.sqrt(averageMeanSquare);

        return rootMeanSquare;
    }
}