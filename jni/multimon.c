
#include <stdio.h>
#include <stdarg.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/wait.h>
#include <stdlib.h>

#include <jni.h>

#include "multimon.h"


static const struct demod_param *dem[] = { ALL_DEMOD };

#define NUMDEMOD (sizeof(dem)/sizeof(dem[0]))

static struct demod_state dem_st[NUMDEMOD];
static unsigned int dem_mask[(NUMDEMOD+31)/32];

#define MASK_SET(n) dem_mask[(n)>>5] |= 1<<((n)&0x1f)
#define MASK_RESET(n) dem_mask[(n)>>5] &= ~(1<<((n)&0x1f))
#define MASK_ISSET(n) (dem_mask[(n)>>5] & 1<<((n)&0x1f))

static int verbose_level = 0;


//memset(&dem_afsk1200_st, 0, sizeof(dem_afsk1200_st));
//dem_st[
//dem_afsk1200_st.dem_par = ALL_DEMOD[3]; //&dem_afsk1200;
//&dem_afsk1200->init(dem_afsk1200_st);


void verbprintf(int verb_level, const char *fmt, ...)
{
        va_list args;
        
        /* va_start(args, fmt); */
        /* if (verb_level <= verbose_level) { */
        /*   //                vfprintf(stdout, fmt, args); */
        /*   LOGI(fmt, args); */
        /* } */
        /* va_end(args); */
}

static void process_buffer(float *buf, unsigned int len)
{
	int i;

	/* for (i = 0; i <  NUMDEMOD; i++)  */
	/* 	if (MASK_ISSET(i) && dem[i]->demod) */
	/* 		dem[i]->demod(dem_st+i, buf, len); */
        //(&demod_afsk1200)->demod(&dem_afsk1200_st, buf, len);
        dem[3]->demod(dem_st+3, buf, len);
        //(*demod_afsk1200)->demod(dem_st+3, buf, len);
        //        dem[3]->demod(dem_st+3, buf, len);
}


static unsigned int fbuf_cnt = 0;
static int overlap = 18;
static float fbuf[16374];

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved)
{
  LOGI("called JNI_OnLoad");
  return JNI_VERSION_1_6;
}


void Java_com_jazzido_PacketDroid_AudioBufferProcessor_init(JNIEnv *env, jobject object) {
  static int sample_rate = -1;
  unsigned int i;
  unsigned int overlap = 0;

  LOGI("YESYESYES I'm on init");

  LOGI("NUMDEMOD: %d", NUMDEMOD);

  for (i = 0; i < NUMDEMOD; i++) {
    LOGI(" DEM: %s", dem[i]->name);
    memset(dem_st+i, 0, sizeof(dem_st[i]));
    dem_st[i].dem_par = dem[i];
    if (dem[i]->init)
      dem[i]->init(dem_st+i);
    if (sample_rate == -1)
      sample_rate = dem[i]->samplerate;
    else if (sample_rate != dem[i]->samplerate) {
      /* fprintf(stdout, "\n"); */
      /* fprintf(stderr, "Error: Current sampling rate %d, " */
      /*         " demodulator \"%s\" requires %d\n", */
      /*         sample_rate, dem[i]->name, dem[i]->samplerate); */
      exit(3);
    }
    if (dem[i]->overlap > overlap)
      overlap = dem[i]->overlap;
  }

  // create named pipe for sending data to Java side
  unlink(NAMED_PIPE);
  int res = mkfifo(NAMED_PIPE,  S_IRUSR | S_IWUSR);

  fbuf_cnt = 0;
  
}



// this should be equivalent to
// com.jazzido.PacketDroid.AudioBufferProcessor.read
// but it doesn't work...
// FIXME 
void Java_com_jazzido_PacketDroid_AudioBufferProcessor_processBuffer2(JNIEnv *env, jobject object, jbyteArray buf) {

  LOGI("ProcessBuffer2 NATIVE");

  unsigned int i;
  short tmp;

  jsize len = (*env)->GetArrayLength(env, buf);
  jbyte *jbuf = (*env)->GetByteArrayElements(env, buf, 0);

  for(i = 0; i < len / 2; i++) {
    tmp = (short) (((jbuf[(i*2)+1] & 0xFF) << 8) | (jbuf[i*2] & 0xFF));
    fbuf[fbuf_cnt++] = tmp * (1.0/32768.0); // 32k is max amplitude
    // LOGI("SHORT %d: %d - %.10f", i, tmp, fbuf[fbuf_cnt-1]);
  }

  if (fbuf_cnt > overlap) {
    process_buffer(fbuf, len);
    memmove(fbuf, fbuf+fbuf_cnt-overlap, overlap*sizeof(fbuf[0]));
    fbuf_cnt = overlap;
  }

  (*env)->ReleaseByteArrayElements(env, buf, jbuf, 0);

} 

void Java_com_jazzido_PacketDroid_AudioBufferProcessor_processBuffer(JNIEnv *env, jobject object, jfloatArray fbuf, jint length) {
  LOGI("ProcessBuffer NATIVE");
  jfloat *jfbuf = (*env)->GetFloatArrayElements(env, fbuf, 0);
  process_buffer(jfbuf, length);
  (*env)->ReleaseFloatArrayElements(env, fbuf, jfbuf, 0);
}

