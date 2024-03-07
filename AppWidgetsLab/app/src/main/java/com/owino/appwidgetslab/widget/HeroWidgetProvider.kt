package com.owino.appwidgetslab.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.owino.appwidgetslab.MainActivity
import com.owino.appwidgetslab.R
import kotlin.random.Random

class HeroWidgetProvider: AppWidgetProvider() {
    private var widgetTitle: String = "MARVEL HEROES"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        appWidgetIds?.forEach { widgetId ->
            val pendingIntent = PendingIntent.getActivity(
                context,
                widgetId,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val widgetViews: RemoteViews = RemoteViews(
                context?.packageName,
                R.layout.hero_appwidget_layout
            ).apply {
                setTextViewText(R.id.widget_title, widgetTitle)
                setTextViewText(R.id.widget_timer, " ${Random.nextInt()}")
                setOnClickPendingIntent(R.id.widget_parent_container, pendingIntent)
            }
            appWidgetManager?.updateAppWidget(widgetId, widgetViews)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.getStringExtra(WIDGET_TITLE_KEY) != null){
            widgetTitle = intent.getStringExtra(WIDGET_TITLE_KEY)!!
        }
        super.onReceive(context, intent)
    }
    companion object {
        const val WIDGET_TITLE_KEY = "widget.title.key"
    }
}