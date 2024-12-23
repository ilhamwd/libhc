package com.xlzn.hcpda.uhf;

import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

public class Utils {
    private static Context context;
    /* access modifiers changed from: private */
    public static int soundID = 0;
    /* access modifiers changed from: private */
    public static SoundPool soundPool = null;
    /* access modifiers changed from: private */
    public static SoundThrea soundThrea = null;

    public static void loadSoundPool(Context context1) {
        context = context1;
        try {
            SoundPool soundPool2 = new SoundPool(10, 3, 10);
            soundPool = soundPool2;
        } catch (Exception e) {
            Log.d("Utils", " e=" + e.toString());
        }
        if (soundThrea == null) {
            SoundThrea soundThrea2 = new SoundThrea();
            soundThrea = soundThrea2;
            soundThrea2.start();
        }
    }

    public static void releaseSoundPool() {
        SoundPool soundPool2 = soundPool;
        if (soundPool2 != null) {
            soundPool2.release();
        }
        SoundThrea soundThrea2 = soundThrea;
        if (soundThrea2 != null) {
            soundThrea2.isFlag = true;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            soundThrea = null;
        }
    }

    public static void play() {
        soundThrea.play = true;
    }

    public static void stop() {
        soundThrea.play = false;
    }

    static class SoundThrea extends Thread {
        boolean isFlag = false;
        boolean play = false;

        SoundThrea() {
        }

        public void run() {
            while (!this.isFlag) {
                if (Utils.soundThrea.play) {
                    Utils.soundThrea.play = false;
                    if (Utils.soundPool != null) {
                        Utils.soundPool.play(Utils.soundID, 1.0f, 1.0f, 0, 0, 1.0f);
                    }
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }
}
