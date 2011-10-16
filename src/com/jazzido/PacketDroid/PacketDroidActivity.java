package com.jazzido.PacketDroid;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PacketDroidActivity extends Activity implements OnClickListener {
	
	public static String LOG_TAG = "MultimonDroid";
	
	// TODO this shouldn't be a constant. Use Context.getApplicationInfo().dataDir
	private String PIPE_PATH = "/data/data/com.jazzido.PacketDroid/pipe";
	
	private Button b;
	private TextView tv;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        b = (Button) findViewById(R.id.button1);
        b.setOnClickListener((android.view.View.OnClickListener) this);
        
		tv = (TextView) findViewById(R.id.textview);
		tv.setMovementMethod(new ScrollingMovementMethod());

    }

	public void onClick(View v) {
		Log.d(LOG_TAG, "START: Monitor");
		startMonitor();
		Log.d(LOG_TAG, "START: PipeReader");
		startPipeRead();
	}
	
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d(LOG_TAG, "GOT MESSAGE FROM FILE READER!");
			tv.append(msg.getData().getString("line") + "\n");
//			tv.post(new Runnable() { 
//                public void run() { 
//                    tv.scrollTo(0, tv.getBottom());
//                } 
//            }); 
		}
	};
	
	private void startMonitor() {
		Thread t = new Thread(null, 
				  new Runnable () {
					public void run() {
						//FileBufferProcessor bp = new FileBufferProcessor("/sdcard/packet-radio-signed-16.raw");
						AudioBufferProcessor bp = new AudioBufferProcessor();
						bp.read();
					}
				  },
		          "FBP Thread");
		t.start();
	}
	
	private void startPipeRead() {
		Thread t = new Thread(null, new Runnable() {
			 public void run() {
				try {
					BufferedReader in = new BufferedReader(new FileReader(PIPE_PATH));
					String line;
					while (true) {
						line = in.readLine();
						if (line != null) {
							Log.d(LOG_TAG, line);
							Message msg = Message.obtain();
							msg.what = 0;
							Bundle bundle = new Bundle();
							bundle.putString("line", line);
							msg.setData(bundle);
							handler.sendMessage(msg);
						}
						
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
			 }
		});
		t.start();
	}
	
	
	
}
	
	
