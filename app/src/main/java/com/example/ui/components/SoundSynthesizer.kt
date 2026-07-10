package com.example.ui.components

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundSynthesizer {
    private const val SAMPLE_RATE = 22050

    var isSoundEnabled: Boolean = true
    var isMusicEnabled: Boolean = false

    private val scope = CoroutineScope(Dispatchers.Default)

    fun playClick() {
        if (!isSoundEnabled) return
        scope.launch {
            val buffer = generateTone(880f, 0.05f)
            playPcm(buffer)
        }
    }

    fun playPlacement() {
        if (!isSoundEnabled) return
        scope.launch {
            val duration = 0.08f
            val samples = (SAMPLE_RATE * duration).toInt()
            val buffer = ShortArray(samples)
            for (i in 0 until samples) {
                val progress = i.toFloat() / samples
                val freq = 523.25f + progress * 261.63f
                val angle = 2.0 * Math.PI * freq * i / SAMPLE_RATE
                buffer[i] = (sin(angle) * Short.MAX_VALUE * 0.4f * (1f - progress)).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    fun playVictory() {
        if (!isSoundEnabled) return
        scope.launch {
            val notes = floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f)
            val noteDuration = 0.12f
            val totalSamples = (SAMPLE_RATE * noteDuration * notes.size).toInt()
            val buffer = ShortArray(totalSamples)
            
            for (n in notes.indices) {
                val startIdx = (n * SAMPLE_RATE * noteDuration).toInt()
                val noteSamples = (SAMPLE_RATE * noteDuration).toInt()
                val freq = notes[n]
                for (i in 0 until noteSamples) {
                    val angle = 2.0 * Math.PI * freq * i / SAMPLE_RATE
                    val fadeOut = (noteSamples - i).toFloat() / noteSamples
                    buffer[startIdx + i] = (sin(angle) * Short.MAX_VALUE * 0.4f * fadeOut).toInt().toShort()
                }
            }
            playPcm(buffer)
        }
    }

    fun playDefeat() {
        if (!isSoundEnabled) return
        scope.launch {
            val notes = floatArrayOf(392.00f, 311.13f, 261.63f)
            val noteDuration = 0.18f
            val totalSamples = (SAMPLE_RATE * noteDuration * notes.size).toInt()
            val buffer = ShortArray(totalSamples)
            
            for (n in notes.indices) {
                val startIdx = (n * SAMPLE_RATE * noteDuration).toInt()
                val noteSamples = (SAMPLE_RATE * noteDuration).toInt()
                val freq = notes[n]
                for (i in 0 until noteSamples) {
                    val angle = 2.0 * Math.PI * freq * i / SAMPLE_RATE
                    val fadeOut = (noteSamples - i).toFloat() / noteSamples
                    buffer[startIdx + i] = (sin(angle) * Short.MAX_VALUE * 0.4f * fadeOut).toInt().toShort()
                }
            }
            playPcm(buffer)
        }
    }

    fun playDraw() {
        if (!isSoundEnabled) return
        scope.launch {
            val duration = 0.3f
            val samples = (SAMPLE_RATE * duration).toInt()
            val buffer = ShortArray(samples)
            for (i in 0 until samples) {
                val progress = i.toFloat() / samples
                val angle1 = 2.0 * Math.PI * 300.0 * i / SAMPLE_RATE
                val angle2 = 2.0 * Math.PI * 310.0 * i / SAMPLE_RATE
                val wave = (sin(angle1) + sin(angle2)) * 0.5f
                buffer[i] = (wave * Short.MAX_VALUE * 0.4f * (1f - progress)).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    fun playToggle() {
        if (!isSoundEnabled) return
        scope.launch {
            val buffer = generateTone(1200f, 0.04f)
            playPcm(buffer)
        }
    }

    private fun generateTone(freq: Float, durationSec: Float): ShortArray {
        val samples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(samples)
        for (i in 0 until samples) {
            val progress = i.toFloat() / samples
            val angle = 2.0 * Math.PI * freq * i / SAMPLE_RATE
            buffer[i] = (sin(angle) * Short.MAX_VALUE * 0.4f * (1f - progress)).toInt().toShort()
        }
        return buffer
    }

    private fun playPcm(buffer: ShortArray) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            
            scope.launch {
                kotlinx.coroutines.delay((buffer.size.toFloat() / SAMPLE_RATE * 1000).toLong() + 100)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (e: Exception) {
                    // Ignore
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var musicJob: kotlinx.coroutines.Job? = null

    fun startMusic() {
        if (!isMusicEnabled) {
            stopMusic()
            return
        }
        if (musicJob?.isActive == true) return
        musicJob = scope.launch {
            val notes = listOf(
                listOf(220f, 261.63f, 329.63f),
                listOf(174.61f, 220f, 261.63f),
                listOf(261.63f, 329.63f, 392f),
                listOf(196f, 246.94f, 293.66f)
            )
            val noteDuration = 1.2f
            val samples = (SAMPLE_RATE * noteDuration).toInt()
            
            while (isMusicEnabled) {
                for (chord in notes) {
                    if (!isMusicEnabled) break
                    val buffer = ShortArray(samples)
                    for (i in 0 until samples) {
                        val progress = i.toFloat() / samples
                        var wave = 0f
                        for (freq in chord) {
                            wave += sin(2.0 * Math.PI * freq * i / SAMPLE_RATE).toFloat()
                        }
                        wave /= chord.size
                        val envelope = if (progress < 0.1f) {
                            progress / 0.1f
                        } else {
                            (1f - progress)
                        }
                        buffer[i] = (wave * Short.MAX_VALUE * 0.15f * envelope).toInt().toShort()
                    }
                    playPcm(buffer)
                    kotlinx.coroutines.delay((noteDuration * 1000).toLong())
                }
            }
        }
    }

    fun stopMusic() {
        musicJob?.cancel()
        musicJob = null
    }
}
