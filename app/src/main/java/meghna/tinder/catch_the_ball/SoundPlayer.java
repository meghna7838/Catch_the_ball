package meghna.tinder.catch_the_ball;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class SoundPlayer {
    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 3;

    private static SoundPool soundpool;
    private static int hitOrangeSound;
    private static int hitPinkSound;
    private  static int hitBlackSound;

    public SoundPlayer(Context context)
    {
        //SoundPool is deprecated in API level 21.(Lollipop)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundpool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(SOUND_POOL_MAX)
                    .build();
        }else {
            soundpool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC,0);
        }

        hitOrangeSound = soundpool.load(context,R.raw.orange,1);
        hitPinkSound = soundpool.load(context,R.raw.pink,1);
        hitBlackSound = soundpool.load(context,R.raw.black,1);
    }

    public void playHitOrangeSound(){
            soundpool.play(hitOrangeSound, 1.0f,1.0f,1,0,1.0f);
    }


    public void playHitPinkSound(){
        soundpool.play(hitPinkSound, 1.0f,1.0f,1,0,1.0f);
    }


    public void playHitBlackSound(){
        soundpool.play(hitBlackSound, 1.0f,1.0f,1,0,1.0f);
    }
}
