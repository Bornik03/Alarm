package com.example.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Request exact alarm permission if needed
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
        setContent {
            MainContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainContent() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Alarm App", color = Color.Black) }) },
        content = { MyContent() }
    )
}

@Composable
fun MyContent() {
    val mContext = LocalContext.current
    val mCalendar = remember { Calendar.getInstance() }
    val mHour = remember { mCalendar[Calendar.HOUR_OF_DAY] }
    val mMinute = remember { mCalendar[Calendar.MINUTE] }
    val mTime = remember { mutableStateOf("") }

    val alarmMgr = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmIntent = Intent(mContext, AlarmReceiver::class.java).let { intent ->
        PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    val mTimePickerDialog = TimePickerDialog(
        mContext,
        { _, selectedHour: Int, selectedMinute: Int ->
            mTime.value = "$selectedHour:$selectedMinute"
            // Set the alarm to this time
            mCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            mCalendar.set(Calendar.MINUTE, selectedMinute)
            mCalendar.set(Calendar.SECOND, 0)
            mCalendar.set(Calendar.MILLISECOND, 0)
            if (mCalendar.before(Calendar.getInstance())) {
                // If the selected time is before the current time, set it for the next day
                mCalendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }, mHour, mMinute, false
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { mTimePickerDialog.show() }) {
            Text(text = "Open Time Picker", color = Color.White)
        }
        Button(onClick = {
            Toast.makeText(mContext, "Alarm has been set!", Toast.LENGTH_SHORT).show()
            alarmMgr.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                mCalendar.timeInMillis,
                alarmIntent
            )
        }) {
            Text(text = "Start")
        }
        Spacer(modifier = Modifier.size(100.dp))
        Text(text = "Selected Time: ${mTime.value}", fontSize = 30.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainContent()
}
