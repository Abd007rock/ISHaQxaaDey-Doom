package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class VoiceNarrationManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.ENGLISH
            tts?.setPitch(1.1f) // Slightly higher friendly pitch for kids
            tts?.setSpeechRate(0.85f) // Clear, slower speaking pace for children
            isInitialized = true
        }
    }

    fun speak(text: String) {
        if (isInitialized) {
            tts?.stop()
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "POEM_SPEECH_ID")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun destroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
