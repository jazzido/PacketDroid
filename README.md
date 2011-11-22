PacketDroid is [Multimon](http://www.baycom.org/~tom/ham/linux/multimon.html) for Android
=========================================================================================

Right now, the only demodulator available is AFSK1200. 
After I clean up the code a little bit, it'll support all the decoders implemented by Multimon:

- AX.25
    - 1200 Baud AFSK
    - 2400 Baud AFSK (2 variants)
    - 4800 Baud HAPN
    - 9600 Baud FSK (G3RUH)
- POCSAG
    - 512 Baud
    - 1200 Baud
    - 2400 Baud
- Miscellaneous
    - DTMF
    - ZVEI


Building
--------

    $ ndk-build
    $ android update project -p .
    $ ant debug
    
(you need the [Android NDK](http://developer.android.com/sdk/ndk/index.html))

Tested Devices
--------------

- Motorola Milestone
- Motorola Defy
- HTC Desire Z (thanks [ge0rg](https://github.com/ge0rg)!)
- HTC Dream (thanks [ge0rg](https://github.com/ge0rg)!)
- Sony Xperia Mini Pro (thanks Martin OK1DJO!)

Caveat emptor
-------------

This code was put together in two afternoons. It is also my first Android application, so don't expect much.
