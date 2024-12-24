package com.quectel.multicamera;

/**
 * Created by zyz for beep 2020-04-27
 * @ProjectName: BeepService
 * @Package: com.android.beepservice
 * @ClassName: BeepManager
 * @Description: java类作用描述
 */

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.RawRes;

import java.io.Closeable;
import java.io.IOException;

public class BeepManager implements MediaPlayer.OnErrorListener, Closeable {

    private static final String TAG = BeepManager.class.getSimpleName();

    private static final float BEEP_VOLUME = 1f;
    private static final long VIBRATE_DURATION = 200L;

    private final Context     context;
    private       MediaPlayer mediaPlayer;
    private       boolean     playBeep;
    private       boolean     vibrate;

    public BeepManager(Context context) {
        this.context = context;
        this.mediaPlayer = null;
        updatePrefs();
    }

    private void updatePrefs() {
        playBeep = shouldBeep(context);
        vibrate = false;
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
            // so we now play on the music stream.
            mediaPlayer = buildMediaPlayer(context);
        }
    }

    public boolean isPlayingBeep(){
        if (mediaPlayer == null)
            return false;
        return mediaPlayer.isPlaying();
    }

    public void playBeepSoundAndVibrate(@RawRes int id) {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            mediaPlayer = buildMediaPlayer(context);
            try (AssetFileDescriptor file = context.getResources().openRawResourceFd(id)) {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                mediaPlayer.setOnErrorListener(this);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setLooping(false);
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                Log.w(TAG, e);
                mediaPlayer.release();
            }
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private static boolean shouldBeep(Context activity) {
        boolean shouldPlayBeep = true;
        if (shouldPlayBeep) {
            // See if sound settings overrides this
            AudioManager audioService = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
            if (audioService != null){
                if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                    shouldPlayBeep = false;
                }
            }
        }
        return shouldPlayBeep;
    }

    private MediaPlayer buildMediaPlayer(Context activity) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        return mediaPlayer;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            // we are finished, so put up an appropriate error toast if required and finish
            //context.finish();
        } else {
            // possibly media player error, so release and recreate
            close();
            updatePrefs();
        }
        return true;
    }

    @Override
    public void close() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
