package com.jazzido.PacketDroid;

import java.util.concurrent.LinkedBlockingQueue;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

public class AudioBufferProcessor  {

	private AudioIn audioIn = new AudioIn();
	float[] fbuf = new float[16384];
	private int fbuf_cnt = 0;
	private int overlap = 18; // overlap for AFSK DEMOD (FREQSAMP / BAUDRATE)
	
	int _dumpCount = 1024;
	
	native void init();
	native void processBuffer(float[] buf, int length);
	native void processBuffer2(byte[] buf);
	
	private final LinkedBlockingQueue<short[]> queue;
	
    static {
        System.loadLibrary("multimon");
    }
	
	public AudioBufferProcessor() {
		init();
		queue = new LinkedBlockingQueue<short[]>();
		
	}
	
	public void read() {
		// TODO Auto-generated method stub
		audioIn.start();
		while (true) {
			try {
				decode(queue.take());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	void decode(short[] s) {
		// Log.d(PacketDroidActivity.LOG_TAG, "CALLBACK!: " + s.length);
		
		for (int i = 0; i < s.length; i++) {
			fbuf[fbuf_cnt++] = s[i] * (1.0f/32768.0f);
		}
		
		if (fbuf_cnt > overlap) {
			processBuffer(fbuf, fbuf_cnt - overlap);
			System.arraycopy(fbuf, fbuf_cnt-overlap, fbuf, 0, overlap);
			fbuf_cnt = overlap;
		}
	}
	
	// taken from: http://stackoverflow.com/questions/4525206/android-audiorecord-class-process-live-mic-audio-quickly-set-up-callback-func
	public class AudioIn extends Thread {
		private boolean stopped = false;

		public AudioIn() {
			super("AudioIn");
			android.os.Process
					.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		}

		@Override
		public void run() {
			AudioRecord recorder = null;
			short[][] buffers = new short[256][8192];
			int ix = 0;

			try {
				recorder = new AudioRecord(AudioSource.MIC, 22050,
						AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT, 16384);

				recorder.startRecording();

				while (!stopped) {
					int nRead = 0;
					short[] buffer = buffers[ix++ % buffers.length];

					nRead = recorder.read(buffer, 0, buffer.length);

					queue.put(buffer);
					// process(buffer);
				}
			} catch (Throwable x) {
				Log.w(PacketDroidActivity.LOG_TAG, "Error reading audio", x);
			} finally {
				recorder.stop();
			}
		}


		private void close() {
			stopped = true;
		}

	}

}
