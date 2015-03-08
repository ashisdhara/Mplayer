package com.akdworld.mplayer;

import java.util.ArrayList;
import java.util.Random;
//import com.akdworld.mplayer.MainActivity;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class MusicService extends Service implements
		MediaPlayer.OnCompletionListener {

	public static int position = 0;
	private static MediaPlayer mp = new MediaPlayer();
	private static ArrayList<Song> songsList;
	private final IBinder musicBind = new MusicBinder();
	private boolean shuffle = false;
	private Random rand = new Random();
	public static int NOTIFY_ID = 1;
	private static String songTitle = "";
	private static Notification not;
	private static boolean paused = true;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public class MusicBinder extends Binder {
		MusicService getService() {
			return MusicService.this;
		}
	}

	// @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void setNotif() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			setNotifJB();
		} else {
			return;
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void setNotifJB() {
		Intent notIntent = new Intent(this, MainActivity.class);
		notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		Notification.Builder builder = new Notification.Builder(this);
		builder.setContentIntent(pendInt)
				.setSmallIcon(paused ? R.drawable.pause : R.drawable.play)
				.setTicker(songTitle).setOngoing(true)
				.setContentTitle(getPlayingState()).setContentText(songTitle);
		not = builder.build();
		startForeground(NOTIFY_ID, not);

	}

	public void deleteNotif() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			NotificationManager notifManager = (NotificationManager) getApplicationContext()
					.getSystemService(
							getApplicationContext().NOTIFICATION_SERVICE);
			notifManager.cancelAll();
		} else {
			return;
		}
	}

	/*
	 * @Override public void onStart(Intent intent, int startId) {
	 * AppWidgetManager appWidgetManager =
	 * AppWidgetManager.getInstance(this.getApplicationContext()); int []
	 * allWidgetIds =
	 * intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
	 * ComponentName thisWidget = new ComponentName(getApplicationContext(),
	 * Widget.class); int [] allWidgetIds2 =
	 * appWidgetManager.getAppWidgetIds(thisWidget); for(int widgetId :
	 * allWidgetIds) { int number = (new Random().nextInt(100)); RemoteViews
	 * remoteViews = new
	 * RemoteViews(this.getApplicationContext().getPackageName(),
	 * R.layout.widget); Log.d("akd","abcdinservice");
	 * remoteViews.setTextViewText(R.id.tWidget,"setstringfrom service"); Intent
	 * clickIntent = new Intent(this.getApplicationContext() , Widget.class);
	 * clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	 * clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
	 * PendingIntent pendingIntent =
	 * PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent,
	 * PendingIntent.FLAG_UPDATE_CURRENT);
	 * remoteViews.setOnClickPendingIntent(R.id.tWidget, pendingIntent);
	 * appWidgetManager.updateAppWidget(widgetId, remoteViews);
	 * 
	 * } stopSelf(); super.onStart(intent, startId); }
	 */
	public void initMusicPlayer() {
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mp.setOnCompletionListener(this);
	}

	public void setShuffle() {
		if (shuffle) {
			shuffle = false;
		} else
			shuffle = true;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return musicBind;
	}

	public void setList(ArrayList<Song> theSongs) {
		songsList = theSongs;
	}

	public void play() {
		// Log.d("akd", "ashiswashere");
		if (mp.isPlaying()) {
			mp.reset();
		}
		Song toPlay = songsList.get(position);
		String currSongPath = toPlay.getPath();
		songTitle = toPlay.getTitle();
		mp = MediaPlayer.create(this, Uri.parse(currSongPath));
		paused = false;
		mp.start();
		setNotif();
		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// Log.d("akd", "ordered next");
				next();
			}
		});

	}

	public void setSong(int songIndex) {

		position = songIndex;
	}

	public void next() {
		if (shuffle) {
			// Log.d("akd","in shuffle"+position);
			position = rand.nextInt(songsList.size());

		} else {
			position++;
		}
		if (position > (songsList.size() - 1)) {
			position = 0;
		}

		play();

	}

	public void prev() {
		position--;
		if (position < 0) {
			position = (songsList.size() - 1);
		}
		play();
	}

	public void pause() {
		if (mp.isPlaying()) {
			paused = true;
			mp.pause();

		} else {
			paused = false;
			mp.start();
		}
		setNotif();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		{
			next();
		}
	}

	@Override
	public void onDestroy() {
		stopForeground(true);
	}

	public int getPosn() {
		return mp.getCurrentPosition();
	}

	public int getDur() {
		return mp.getDuration();
	}

	public boolean isPng() {
		return mp.isPlaying();
	}

	public void pausePlayer() {
		paused = true;
		mp.pause();
		setNotif();
	}

	public void seek(int posn) {
		mp.seekTo(posn);
	}

	public void go() {
		paused = false;
		mp.start();
		setNotif();
	}

	public int getPosition() {
		return position;
	}

	public String getPlayingState() {
		if (paused) {
			return ("Paused");
		} else {
			return ("Playing");
		}
	}

	public void setPosition(int pos) {
		position = pos;
	}

}
