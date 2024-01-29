package com.qdreamer.utils;

import android.media.AudioFormat;
import android.media.MediaRecorder;

import com.qdreamer.utils.inter.IRecordOption;


public class AudioRecordOption implements IRecordOption {

    private static final int DEFAULT_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int DEFAULT_SAMPLE = 48000;
    private static final int DEFAULT_CHANNEL = AudioFormat.CHANNEL_IN_STEREO;
    private static final int DEFAULT_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = 4096;

    private final int source;
    private final int sample;
    private final int channel;
    private final int format;
    private final int bufferSize;

    public int getBufferSize() {
        return bufferSize;
    }



    public AudioRecordOption() {
        this(DEFAULT_SOURCE, DEFAULT_SAMPLE, DEFAULT_CHANNEL, DEFAULT_FORMAT,BUFFER_SIZE);
    }

    public AudioRecordOption(int source, int sample, int channel, int format,int bufferSize) {
        this.source = source;
        this.sample = sample;
        this.channel = channel;
        this.format = format;
        this.bufferSize = bufferSize;
    }

    public int getSource() {
        return source;
    }

    public int getSample() {
        return sample;
    }

    public int getChannel() {
        return channel;
    }

    public int getFormat() {
        return format;
    }

}
