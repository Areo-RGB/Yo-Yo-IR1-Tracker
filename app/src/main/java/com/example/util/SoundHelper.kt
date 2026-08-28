package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator

class SoundHelper {
    private var toneGenerator: ToneGenerator? = null
    var isSoundEnabled: Boolean = true

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Exception) {
            toneGenerator = null
        }
    }

    fun playStartBeep() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
        } catch (_: Exception) {}
    }

    fun playWarningBeep() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 150)
        } catch (_: Exception) {}
    }

    fun playEliminationBeep() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_LOW_PBX_L, 300)
        } catch (_: Exception) {}
    }

    fun playCountdownBeep() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}
