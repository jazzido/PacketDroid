package com.jazzido.PacketDroid;


public abstract class BufferProcessor {


	native void init();
	native void processBuffer(float[] buf, int length);
	native void processBuffer2(byte[] buf);
	
    static {
        System.loadLibrary("multimon");
    }


	public BufferProcessor() {
		super();
	}

	abstract void read();

}