package br.ufpe.cin.if710.rss

import android.arch.persistence.room.Room
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        conteudoRSS.apply {
            // use a linear layout manager
            layoutManager = LinearLayoutManager(this@MainActivity)

            // specify an viewAdapter (see also next example)
            adapter = MyAdapter()

            // add divider item decoration to recycler view
            addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation))
        }

        this.cancelAllNotifications.onReceive(this, Intent())
        this.broadcastReceiver.onReceive(this, Intent())

        MyIntentService.startActionDo(this)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(this.cancelAllNotifications, IntentFilter(ACTION_NEW))
        registerReceiver(this.broadcastReceiver, IntentFilter(ACTION_DONE))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(this.cancelAllNotifications)
        unregisterReceiver(this.broadcastReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.rss_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.settingsItem -> {
                startActivityForResult(Intent(this, SettingsActivity::class.java), 0)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        MyIntentService.startActionDo(this)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            doAsync {
                val db = Room.databaseBuilder(context, AppDatabase::class.java, "database").build()
                val dataset = db.entryDao().items.asReversed()
                uiThread {
                    (conteudoRSS.adapter as MyAdapter).setDataset(dataset)
                }
            }
        }
    }

    private val cancelAllNotifications = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            NotificationManagerCompat.from(context).cancelAll()
        }
    }
}
