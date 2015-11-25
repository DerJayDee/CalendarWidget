package de.dlrg.rodenkirchen.calendar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;

public class CalendarWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.d(context.getString(R.string.app_name), context.getString(R.string.update_reached));

		for (int widgetId : appWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
			                                          R.layout.widget);
			setDateInWidget(remoteViews);

			PendingIntent myPI = generateIntent(context);
			remoteViews.setOnClickPendingIntent(R.id.dateText, myPI);
			remoteViews.setOnClickPendingIntent(R.id.caption, myPI);

			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	private PendingIntent generateIntent(Context context) {
		long startMillis = new Date().getTime();

		Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
		builder.appendPath(context.getString(R.string.time));
		ContentUris.appendId(builder, startMillis);
		Intent intent = new Intent(Intent.ACTION_VIEW)
				.setData(builder.build());
		return PendingIntent.getActivity(context, 0, intent, 0);
	}

	private void setDateInWidget(RemoteViews views) {
		Date d = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		views.setTextViewText(R.id.dateText, String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
	}
}
