package br.ufpe.cin.if710.rss

import android.app.IntentService
import android.arch.persistence.room.Room
import android.content.Intent
import android.content.Context
import android.content.IntentFilter
import android.database.sqlite.SQLiteConstraintException
import android.preference.PreferenceManager
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.jar.Manifest

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
const val ACTION_DO = "br.ufpe.cin.if710.rss.action.DO"
const val ACTION_DONE = "br.ufpe.cin.if710.rss.action.DONE"
const val ACTION_NEW = "br.ufpe.cin.if710.rss.action.NEW"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class MyIntentService : IntentService("MyIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_DO -> handleActionDo()
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionDo() {
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "database").build()
        val url = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("rssfeed", getString(R.string.rssfeed))

        try {
            // make request to specified URL using OkHttp
            val request = Request.Builder().url(url).build()
            OkHttpClient().newCall(request).execute().body()?.let {
                // parse XML
                val rss = ParserRSS.parse(it.string())

                with(MyReceiver()) {
                    registerReceiver(this, IntentFilter(ACTION_NEW))

                    rss.asReversed().forEach {
                        try {
                            db.entryDao().insertAll(Entry(it.title, it.link, it.pubDate, it.description))
                            sendBroadcast(Intent(ACTION_NEW).apply {
                                db.entryDao().all.last().apply {
                                    putExtra("id", uid)
                                    putExtra("title", title)
                                }
                            })
                        } catch (e: SQLiteConstraintException) {
                        }
                    }

                    unregisterReceiver(this)
                }

                sendBroadcast(Intent(ACTION_DONE))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionDo(context: Context) {
            val intent = Intent(context, MyIntentService::class.java).apply {
                action = ACTION_DO
            }
            context.startService(intent)
        }
    }
}
