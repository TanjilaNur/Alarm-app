package com.bjit.alarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

data class Alarm(val id: Int, val name: String, val timeInMillis: Long)

class MainActivity : AppCompatActivity() {
    lateinit var btnSetAlarm: Button
    lateinit var timePicker: TimePicker
    lateinit var alarmNameEditText: EditText
    lateinit var alarmListView: ListView
    private val alarms = mutableListOf<Alarm>()
    private var alarmId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Alarm App"
        timePicker = findViewById(R.id.timePicker)
        btnSetAlarm = findViewById(R.id.buttonAlarm)
        alarmNameEditText = findViewById(R.id.alarmNameEditText)
        alarmListView = findViewById(R.id.alarmListView)

        btnSetAlarm.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            if (Build.VERSION.SDK_INT >= 23) {
                calendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.hour,
                    timePicker.minute,
                    0
                )
            } else {
                calendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.currentHour,
                    timePicker.currentMinute, 0
                )
            }
            val alarmName = alarmNameEditText.text.toString()
            setAlarm(calendar.timeInMillis, alarmName)
        }
    }

    private fun setAlarm(timeInMillis: Long, alarmName: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyAlarm::class.java).apply {
            putExtra("alarmName", alarmName)
        }
        val pendingIntent = PendingIntent.getBroadcast(this, alarmId, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setRepeating(
            AlarmManager.RTC,
            timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        alarms.add(Alarm(alarmId, alarmName, timeInMillis))
        alarmId++
        updateAlarmListView()
        Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show()
    }

    private fun updateAlarmListView() {
        val adapter = AlarmAdapter(this, alarms)
        alarmListView.adapter = adapter
    }

    fun deleteAlarm(alarm: Alarm) {
        alarms.remove(alarm)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyAlarm::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, alarm.id, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
        updateAlarmListView()
        Toast.makeText(this, "Alarm deleted", Toast.LENGTH_SHORT).show()
    }

    private class MyAlarm : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val alarmName = intent.getStringExtra("alarmName")
            Log.d("Alarm Bell", "Alarm just fired: $alarmName")
        }
    }
}

class AlarmAdapter(context: Context, private val alarms: List<Alarm>) :
    ArrayAdapter<Alarm>(context, 0, alarms) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val alarm = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.alarm_item, parent, false)

        val alarmNameTextView = view.findViewById<TextView>(R.id.alarmNameTextView)
        val alarmTimeTextView = view.findViewById<TextView>(R.id.alarmTimeTextView)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)

        alarmNameTextView.text = alarm?.name
        alarmTimeTextView.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(alarm?.timeInMillis ?: 0))
        deleteButton.setOnClickListener {
            (context as MainActivity).deleteAlarm(alarm!!)
        }

        return view
    }
}