package com.akdworld.mplayer;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.akdworld.mplayer.MusicService.MusicBinder;

//import android.media.AudioManager;
import android.media.MediaPlayer;
//import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
//import android.provider.MediaStore.MediaColumns;
import android.app.Activity;
//import android.content.ClipData.Item;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
//import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.ToggleButton;
//import android.widget.VideoView;
import android.widget.MediaController.MediaPlayerControl;

public class MainActivity extends Activity implements MediaPlayerControl {

	private ArrayList<Song> songList;
	private ListView songView;
	public static MediaPlayer mp = new MediaPlayer();
	public boolean playsound = true;
	private int position = 0;
	// private int totalSongs = 0;
	private static MusicService musicSrv;
	private static boolean musicBound = false;
	private boolean paused;
	private Intent intent;
	// private Intent gotoPlay;
	private static Seekbar seekbar;
	// private boolean inLib = true;
	// private boolean shuffle = false;
	private static boolean sb = false;

	// private ToggleButton bShuffle ;
	//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Log.d("akd","creation");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.library);
		songView = (ListView) findViewById(R.id.song_list);
		songList = new ArrayList<Song>();
		getSongList();
		SongAdapter songAdt = new SongAdapter(this, songList);
		songView.setAdapter(songAdt);
		setSeekbar();
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {

		}

		ServiceConnection mConnection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				MusicBinder binder = (MusicBinder) service;
				musicSrv = binder.getService();
				musicSrv.setList(songList);
				musicBound = true;
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				musicBound = false;
			}
		};

		intent = new Intent(this, MusicService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		startService(intent);
		//Log.d("akd", "inoncreate");
	}

	private void showSongs() {
		setContentView(R.layout.library);
		songView = (ListView) findViewById(R.id.song_list);
		songList = new ArrayList<Song>();
		getSongList();
		SongAdapter songAdt = new SongAdapter(this, songList);
		songView.setAdapter(songAdt);
	}

	@Override
	protected void onStart() {
		if (musicBound) {
			musicSrv.deleteNotif();
		}
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void shuffle() {
		musicSrv.setShuffle();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.exit:
			stopService(intent);
			musicSrv = null;
			System.exit(0);
			break;
		case R.id.about:
			Toast mytoast = Toast
					.makeText(
							this,
							"hello !! \n I am  Ashis , If you wanna reach me mail me at ashishere15@gmail.com \n Thanks for using my app buddy :)",
							Toast.LENGTH_LONG);
			mytoast.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void getSongList() {
		ContentResolver musicResolver = getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor musicCursor = musicResolver.query(musicUri, null, null, null,
				null);
		if (musicCursor != null && musicCursor.moveToFirst()) {
			int titleColumn = musicCursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			int idColumn = musicCursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
			int artistColumn = musicCursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
			int pathColumn = musicCursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA);
			int albumColumn = musicCursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM);
			int durationColumn = musicCursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);
			int yearColumn = musicCursor
					.getColumnIndex(android.provider.MediaStore.Audio.Media.YEAR);
			do {
				long thisId = musicCursor.getLong(idColumn);
				String thisTitle = musicCursor.getString(titleColumn);
				String thisArtist = musicCursor.getString(artistColumn);
				String thisPath = musicCursor.getString(pathColumn);
				String thisYear = musicCursor.getString(yearColumn);
				String thisDuration = musicCursor.getString(durationColumn);
				String thisAlbum = musicCursor.getString(albumColumn);

				songList.add(new Song(thisId, thisTitle, thisArtist, thisPath,
						thisAlbum, thisYear, thisDuration));
			} while (musicCursor.moveToNext());
		}

	}

	public void next(View v) {
		musicSrv.next();
		if (paused) {
			paused = false;
		}
		gotoCurrent();
	}

	public void prev(View v) {

		musicSrv.prev();
		if (paused) {
			paused = false;
		}
		gotoCurrent();
	}

	public void songPicked(View v) {
		position = Integer.parseInt(v.getTag().toString());
		musicSrv.setPosition(position);
		musicSrv.play();
		if (paused) {
			paused = false;
		}
		makeSeekbarVisible();
	}

	public void pause(View v) {
		musicSrv.pause();
		paused = true;
	}

	public void gotoPlaying(View V) {
		gotoCurrent();
	}

	public void gotoCurrent() {
		setContentView(R.layout.main);
		TextView title = (TextView) findViewById(R.id.title);
		TextView artist = (TextView) findViewById(R.id.artist);
		TextView album = (TextView) findViewById(R.id.album);
		// TextView year = (TextView) findViewById(R.id.year);
		// TextView duration = (TextView) findViewById(R.id.duration);
		position = musicSrv.getPosition();
		Song toPlay = songList.get(position);
		title.setText(toPlay.getTitle());
		artist.setText(toPlay.getArtist());
		album.setText(toPlay.getAlbum());
		// duration.setText(toPlay.getDuration());
		// year.setText(toPlay.getYear());
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		if (musicSrv != null && musicBound && musicSrv.isPng())
			return musicSrv.getPosn();
		else
			return 0;
	}

	@Override
	public int getDuration() {
		if (musicSrv != null && musicBound && musicSrv.isPng())
			return musicSrv.getDur();
		else
			return 0;
	}

	@Override
	public boolean isPlaying() {
		if (musicSrv != null && musicBound)
			return musicSrv.isPng();
		return false;
	}

	@Override
	public void pause() {
		musicSrv.pausePlayer();
	}

	@Override
	public void seekTo(int pos) {
		musicSrv.seek(pos);
	}

	@Override
	public void start() {
		musicSrv.go();
	}

	private void setSeekbar() {
		seekbar = new Seekbar(this);
		// set previous and next button listeners
		seekbar.setPrevNextListeners(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				musicSrv.next();
			}
		}, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				musicSrv.prev();
			}
		});

		seekbar.setMediaPlayer(this);
		seekbar.setAnchorView(findViewById(R.id.song_list));
		seekbar.setEnabled(true);
	}

	public void library(View V) {
		//Log.d("akd", "library called");
		showSongs();
	}

	@Override
	protected void onStop() {
		seekbar.hide();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		stopService(intent);
		musicSrv = null;
		super.onDestroy();
	}

	public void refresh(View V) {
		gotoCurrent();
	}

	private void toggleSeekbar() {
		if (sb) {
			seekbar.setVisibility(View.GONE);
			sb = false;
		} else {
			makeSeekbarVisible();
		}
	}

	private void makeSeekbarVisible() {
		seekbar.show(0);
		seekbar.setVisibility(View.VISIBLE);
		sb = true;
	}

	public void toggleSeekbar(View v) {
		toggleSeekbar();
	}

	public void shuffle(View v) {
		//Log.d("akd", "shuffle called");
		shuffle();
	}

	@Override
	protected void onPause() {
		if (musicBound) {
			musicSrv.setNotif();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (musicBound) {
			musicSrv.deleteNotif();
		}
		super.onResume();
	}

}
