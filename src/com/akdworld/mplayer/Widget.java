package com.akdworld.mplayer;

//import java.util.Random;

//import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
//import android.util.Log;
//import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		ComponentName thisWidget = new ComponentName(context, Widget.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		Intent intent = new Intent(context.getApplicationContext(),
				MusicService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
		context.startService(intent);
		// super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

}
