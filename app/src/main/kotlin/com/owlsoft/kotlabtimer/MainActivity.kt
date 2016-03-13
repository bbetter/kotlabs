package com.owlsoft.kotlabtimer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.owlsoft.kotlabtimer.models.Lab
import com.owlsoft.kotlabtimer.models.LabAdapter
import com.owlsoft.kotlabtimer.services.LabService
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ScheduledFuture
import kotlin.properties.Delegates

//explicitly imported extension
import com.owlsoft.kotlabtimer.extensions.transaction
import io.realm.RealmConfiguration

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var alarmManager: AlarmManager
    private var realm: Realm by Delegates.notNull()
    private var realmConfig: RealmConfiguration by Delegates.notNull()

    override fun onRefresh() {
        updateLabs()
    }

    private var updateFuture: ScheduledFuture<*>? = null

    private var linearLayoutManager: LinearLayoutManager? = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    private var labAdapter: LabAdapter? = LabAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        supportActionBar?.title = resources.getString(R.string.app_name)
        refreshLayout.setOnRefreshListener(this)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = labAdapter

        initRealm()

        fetchFromDatabase {
            labs ->
            if (labs.size > 0) labAdapter?.data = labs.toList() else updateLabs()
        }
        realm.addChangeListener {
            fetchFromDatabase {
                labs ->
                labs
                        .filter { !it.isDeadline() }
                        .forEach {
                            var intent: Intent = Intent(applicationContext, AlarmReceiver::class.java);

                            intent.putExtra("id", it.id)
                            intent.putExtra("theme", it.theme);
                            intent.putExtra("status", it.getStatus())

                            var pendingIntent: PendingIntent = PendingIntent.getBroadcast(applicationContext, 1, intent, 0)
                            alarmManager.cancel(pendingIntent)
                            alarmManager.set(
                                    AlarmManager.RTC_WAKEUP,
                                    it.getStatus().millis(),
                                    pendingIntent
                            );
                        }
            }
        }
    }

    private fun initRealm() {
        // Create configuration and reset Realm.
        realmConfig = RealmConfiguration.Builder(this).build()
//        Realm.deleteRealm(realmConfig)

        // Open the realm for the UI thread.
        realm = Realm.getInstance(realmConfig)
    }


    fun fetchFromDatabase(callback: (List<Lab>) -> Unit) {
        callback.invoke(realm.allObjects(Lab::class.java))
    }


    fun updateLabs() {
        LabService.getLabs(handler = object : com.github.kittinunf.fuel.core.Handler<List<Lab>> {
            override fun success(request: Request, response: Response, value: List<Lab>) {
                realm.transaction { realm.copyToRealmOrUpdate(value) }
                fetchFromDatabase {
                    labs->
                    labAdapter?.data = labs.toList()
                }

                refreshLayout.isRefreshing = false
            }

            override fun failure(request: Request, response: Response, error: FuelError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
                refreshLayout.isRefreshing = false
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (updateFuture != null) {
            updateFuture?.cancel(true);
            updateFuture = null;
        }
    }
}
