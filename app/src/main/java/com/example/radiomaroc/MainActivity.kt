package com.example.radiomaroc

import android.Manifest
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radiomaroc.databinding.ActivityMainBinding
import com.google.common.util.concurrent.MoreExecutors

@UnstableApi
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StationAdapter
    private var controller: MediaController? = null
    private var currentStation: Station? = null

    private val notifPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askNotificationPermissionIfNeeded()

        adapter = StationAdapter(StationRepository.stations) { station ->
            onStationClicked(station)
        }
        binding.recyclerStations.layoutManager = LinearLayoutManager(this)
        binding.recyclerStations.adapter = adapter

        binding.buttonPlayPause.setOnClickListener {
            togglePlayback()
        }
    }

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, RadioPlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener({
            try {
                controller = controllerFuture.get()
                controller?.addListener(playerListener)
                Toast.makeText(this, "الخدمة متصلة، اختر محطة", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "فشل الاتصال بالخدمة: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }, MoreExecutors.directExecutor())
    }

    override fun onStop() {
        super.onStop()
        controller?.removeListener(playerListener)
        controller?.release()
        controller = null
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlayPauseUi(isPlaying)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateText = when (playbackState) {
                Player.STATE_IDLE -> "IDLE"
                Player.STATE_BUFFERING -> "جاري التحميل..."
                Player.STATE_READY -> "جاهز"
                Player.STATE_ENDED -> "انتهى"
                else -> "غير معروف"
            }
            Toast.makeText(this@MainActivity, "الحالة: $stateText", Toast.LENGTH_SHORT).show()
        }

        override fun onPlayerError(error: PlaybackException) {
            Toast.makeText(
                this@MainActivity,
                "خطأ فالتشغيل: ${error.errorCodeName} - ${error.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun onStationClicked(station: Station) {
        val mc = controller
        if (mc == null) {
            Toast.makeText(this, "الخدمة مازال ماتصلاتش، حاول مرة أخرى", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentStation?.id == station.id) {
            togglePlayback()
            return
        }

        currentStation = station
        binding.textNowPlaying.text = station.name
        adapter.setPlayingStation(station.id)

        Toast.makeText(this, "جاري تشغيل: ${station.name}", Toast.LENGTH_SHORT).show()

        val mediaItem = MediaItem.Builder()
            .setUri(station.streamUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(station.name)
                    .setArtist(getString(R.string.app_name))
                    .build()
            )
            .build()

        mc.setMediaItem(mediaItem)
        mc.prepare()
        mc.play()
    }

    private fun togglePlayback() {
        val mc = controller ?: return
        if (currentStation == null) return

        if (mc.isPlaying) {
            mc.pause()
        } else {
            mc.play()
        }
    }

    private fun updatePlayPauseUi(isPlaying: Boolean) {
        binding.buttonPlayPause.setImageResource(
            if (isPlaying) android.R.drawable.ic_media_pause
            else android.R.drawable.ic_media_play
        )
    }

    private fun askNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
        askNotificationPermissionIfNeeded()

        adapter = StationAdapter(StationRepository.stations) { station ->
            onStationClicked(station)
        }
        binding.recyclerStations.layoutManager = LinearLayoutManager(this)
        binding.recyclerStations.adapter = adapter

        binding.buttonPlayPause.setOnClickListener {
            togglePlayback()
        }
    }

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, RadioPlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener({
            try {
                controller = controllerFuture.get()
                controller?.addListener(playerListener)
            } catch (e: Exception) {
                Toast.makeText(this, "خطأ فالاتصال بخدمة التشغيل: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }, MoreExecutors.directExecutor())
    }
    override fun onStop() {
        super.onStop()
        controller?.removeListener(playerListener)
        controller?.release()
        controller = null
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updatePlayPauseUi(isPlaying)
        }

        override fun onPlayerError(error: PlaybackException) {
            Toast.makeText(
                this@MainActivity,
                getString(R.string.error_playback, currentStation?.name ?: ""),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun onStationClicked(station: Station) {
        val mc = controller ?: return

        if (currentStation?.id == station.id) {
            togglePlayback()
            return
        }

        currentStation = station
        binding.textNowPlaying.text = station.name
        adapter.setPlayingStation(station.id)

        val mediaItem = MediaItem.Builder()
            .setUri(station.streamUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(station.name)
                    .setArtist(getString(R.string.app_name))
                    .build()
            )
            .build()

        mc.setMediaItem(mediaItem)
        mc.prepare()
        mc.play()
    }

    private fun togglePlayback() {
        val mc = controller ?: return
        if (currentStation == null) return

        if (mc.isPlaying) {
            mc.pause()
        } else {
            mc.play()
        }
    }

    private fun updatePlayPauseUi(isPlaying: Boolean) {
        binding.buttonPlayPause.setImageResource(
            if (isPlaying) android.R.drawable.ic_media_pause
            else android.R.drawable.ic_media_play
        )
    }

    private fun askNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
