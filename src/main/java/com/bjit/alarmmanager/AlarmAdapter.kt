package com.bjit.alarmmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView

class AlarmAdapter(context: Context, private val alarms: List<Alarm>) :
    ArrayAdapter<Alarm>(context, 0, alarms) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val alarm = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.alarm_item, parent, false)

        val alarmNameTextView = view.findViewById<TextView>(R.id.alarmNameTextView)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)

        alarmNameTextView.text = alarm?.name
        deleteButton.setOnClickListener {
            (context as MainActivity).deleteAlarm(alarm!!)
        }

        return view
    }
}