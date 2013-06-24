package com.media.service;

import java.io.File;

import com.constants.AppConstant;

import android.os.Environment;

public class AudioService {

    private static AudioService audioService = new AudioService();

    private WAVAudioRecorder extAudioRecorder;// Compressed

    private VolumnShowThread volumnShow;
    
    private AudioService() {
    }

    public static AudioService getService() {
        return audioService;
    }

    public void start() {
        extAudioRecorder = WAVAudioRecorder.getInstanse();

        extAudioRecorder.setOutputFile(AppConstant.VOICE_PATH);
        extAudioRecorder.prepare();
        extAudioRecorder.start();

        volumnShow = new VolumnShowThread();

        volumnShow.start();
    }

    public void stop() {
        volumnShow.setStop();
        extAudioRecorder.stop();
        extAudioRecorder.release();
    }

    public class VolumnShowThread extends Thread {

        private boolean VolumnShow = true;

        public void run() {
            while (VolumnShow) {
                try {
                    System.out.println(extAudioRecorder.getMaxAmplitude());
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
              
        public void setStop() {
            VolumnShow = false;
        }
    }

    public int getVolumnSize(){
    	return extAudioRecorder.getMaxAmplitude();
    }
}
