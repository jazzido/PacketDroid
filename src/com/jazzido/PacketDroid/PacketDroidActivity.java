package com.jazzido.PacketDroid;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import net.ab0oo.aprs.parser.Parser;

public class PacketDroidActivity extends Activity implements PacketCallback {
	
	public static String LOG_TAG = "MultimonDroid";
	
	// TODO this shouldn't be a constant. Use Context.getApplicationInfo().dataDir
	private String PIPE_PATH = "/data/data/com.jazzido.PacketDroid/pipe";
	
	private Button readButton, stopButton;
	private TextView tv;
	private ScrollView sv;
	
	private AudioBufferProcessor abp = null;

	
	// FIXME see what happens when this gets called with the application running
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        readButton = (Button) findViewById(R.id.button1);
        readButton.setOnClickListener(onClickReadButtonListener);
        
        stopButton = (Button) findViewById(R.id.button2);
        stopButton.setOnClickListener(onClickStopButtonListener);
        
		tv = (TextView) findViewById(R.id.textview);
		sv = (ScrollView) findViewById(R.id.scrollView1);
		
		Log.d(LOG_TAG, "PacketDroidActivity: OnCreate");
    }
    
    private OnClickListener onClickReadButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "START: Monitor");
			startMonitor();
			
			//Log.d(LOG_TAG, "START: PipeReader");
			//startPipeRead();
			
			v.setEnabled(false);
			stopButton.setEnabled(true);
		}
	};
    
	private OnClickListener onClickStopButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "STOP: Monitor");
			stopMonitor();

			v.setEnabled(false);
			readButton.setEnabled(true);
		}
	};

	private void startMonitor() {
		if (abp == null) {
			abp = new AudioBufferProcessor(this);
			abp.start();
		}
		else {
			abp.startRecording();
		}
	}
	
	private void stopMonitor() {
		abp.stopRecording();
	}
	

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d(LOG_TAG, "GOT MESSAGE FROM FILE READER!");
			tv.append(msg.getData().getString("line") + "\n");
			sv.scrollTo(0, tv.getHeight()); 
		}
	};
	
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
	
	
	// PacketCallback interface
	public void received(byte[] data) {
		Message msg = Message.obtain();
		msg.what = 0;
		Bundle bundle = new Bundle();
		String packet;
		try {
			packet = Parser.parseAX25(data).toString();
		} catch (Exception e) {
			packet = "raw " + new String(data);
		}
		bundle.putString("line", packet);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
}
	
	
