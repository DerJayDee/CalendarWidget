package de.dlrg.rodenkirchen.calendar;

import android.app.AlarmManager;
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

	private PendingIntent updater = null;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.d(context.getString(R.string.app_name), context.getString(R.string.update_reached));

		scheduleNextUpdate(context);

		Log.d(context.getString(R.string.app_name), context.getString(R.string.set_listeners));
		for (int widgetId : appWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
			                                          R.layout.widget);
			setDateInWidget(remoteViews);

			PendingIntent myPI = generateCalendarIntent(context);
			remoteViews.setOnClickPendingIntent(R.id.dateText, myPI);
			remoteViews.setOnClickPendingIntent(R.id.caption, myPI);

			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	private void scheduleNextUpdate(Context context) {
		final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DAY_OF_MONTH, 1);

		Intent intent = new Intent(context, CalendarWidgetProvider.class);
		intent.setAction(context.getString(R.string.update));
		updater = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.set(AlarmManager.RTC, cal.getTimeInMillis(), updater);
		Log.d(context.getString(R.string.app_name), context.getString(R.string.schedule_update) + cal.toString());
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (updater != null) {
			m.cancel(updater);
		}
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(context.getString(R.string.app_name), context.getString(R.string.on_receive) + intent.getAction());
		if (context.getString(R.string.update).equals(intent.getAction())) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
			                                          R.layout.widget);
			setDateInWidget(remoteViews);
		}
		super.onReceive(context, intent);
	}

	static private PendingIntent generateCalendarIntent(Context context) {
		long startMillis = new Date().getTime();

		Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
		builder.appendPath(context.getString(R.string.time));
		ContentUris.appendId(builder, startMillis);
		Intent intent = new Intent(Intent.ACTION_VIEW)
				.setData(builder.build());
		return PendingIntent.getActivity(context, 0, intent, 0);
	}

	static void setDateInWidget(RemoteViews views) {
		Date d = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		views.setTextViewText(R.id.dateText, String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
	}
}
