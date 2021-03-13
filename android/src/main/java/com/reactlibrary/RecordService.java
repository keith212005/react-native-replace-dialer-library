package com.reactlibrary;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class RecordService {
    private static RecordService mInstance;
    private MediaRecorder recorder;
    boolean isRecording = false;

    private RecordService() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
        recorder.getMaxAmplitude();
        recorder.setOutputFile(getFilePath());
    }

    public static RecordService getInstance() {
        if (mInstance == null) {
            mInstance = new RecordService();
        }
        return mInstance;
    }

    private String getFilePath() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, "MediaRecorderSample");
        if (!file.exists())
            file.mkdirs();
        return (file.getAbsolutePath() + "/" + "Call Record_" + Calendar.getInstance().getTime() + ".mp4");
    }

    public void startRecording() {
        try {
            if(recorder == null && isRecording == false){
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
                recorder.getMaxAmplitude();
                recorder.setOutputFile(getFilePath());
            }
            recorder.prepare();
            recorder.start();
            isRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        if (recorder != null && isRecording == true) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
            isRecording = false;
        }
    }

    public boolean isRecording(){
        return isRecording;
    }



}
