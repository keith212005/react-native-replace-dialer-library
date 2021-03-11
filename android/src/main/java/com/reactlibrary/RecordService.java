package com.reactlibrary;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class RecordService {
    private static RecordService mInstance;
    private MediaRecorder recorder;
    public String fileextn =  "Call Record_" + new Date().getTime() + ".mp4";

    private RecordService() {
        recorder = new MediaRecorder();
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
        return (file.getAbsolutePath() + "/" + fileextn);
    }

    public void startRecording() {
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            recorder.getMaxAmplitude();
            recorder.setOutputFile(getFilePath());
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }



}