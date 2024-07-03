package com.example.alarm

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class Show : ComponentActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            View()
        }
    }

    @Composable
    fun View() {
        val context = LocalContext.current

        Box(modifier = Modifier.fillMaxSize()) {
            mediaPlayer = MediaPlayer.create(context, R.raw.s1)
            mediaPlayer.start()
            Button(
                onClick = {
                    mediaPlayer.stop()
                    mediaPlayer.release()
                    finish()
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(100.dp),
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = null)
            }
        }
    }
}
