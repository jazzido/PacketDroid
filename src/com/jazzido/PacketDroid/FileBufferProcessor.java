package com.jazzido.PacketDroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.util.Log;


public class FileBufferProcessor  {
	
	File file;
	byte[] buffer = new byte[16384];
	float[] fbuf = new float[16384];
	
	native void init();
	native void processBuffer(float[] buf, int length);
	native void processBuffer2(byte[] buf);
	
    static {
        System.loadLibrary("multimon");
    }

	
	public FileBufferProcessor(String fileName) {
		super();
		file = new File(fileName);
		init();
	}
	
	
	public void read() {
		
		ByteBuffer bb;
		FileInputStream fis = null;
		int fbuf_cnt = 0;
		int overlap = 18; // overlap for AFSK DEMOD (FREQSAMP / BAUDRATE)
		
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		short tmp;
		
		byte b1, b2;
		
//		while (fis.read(buffer) != -1) {
//			processBuffer2(buffer);	
//		}
		
	    try {
			while (fis.read(buffer) != -1) {
				bb = ByteBuffer.wrap(buffer);
				while(bb.hasRemaining()) {
					b1 = bb.get(); b2 = bb.get();
					tmp = (short) (((b2 & 0xFF) << 8) | (b1 & 0xFF));
					fbuf[fbuf_cnt++] = tmp * (1.0f/32768.0f); // 32k is max amplitude
				}
				
				if (fbuf_cnt > overlap) {
					processBuffer(fbuf, fbuf_cnt-overlap);
					System.arraycopy(fbuf, fbuf_cnt-overlap, fbuf, 0, overlap);
					fbuf_cnt = overlap;

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		

	}
	
	
}
