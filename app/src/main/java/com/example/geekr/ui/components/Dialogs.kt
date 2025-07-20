package com.example.geekr.ui.components


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import androidx.lifecycle.DefaultLifecycleObserver

@Composable
fun YouTubeTrailerDialog(videoId: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            val lifecycleOwner = LocalLifecycleOwner.current

            AndroidView(factory = { context ->
                YouTubePlayerView(context).apply {
                    lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            this@apply.release()
                        }
                    })

                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.loadVideo(videoId, 0f)
                        }
                    })
                }
            }, modifier = Modifier.fillMaxWidth())
        }
    }
}

