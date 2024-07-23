package com.example.vamosrachar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var totalAmount: EditText
    private lateinit var numberOfPeople: EditText
    private lateinit var result: TextView
    private lateinit var shareButton: Button
    private lateinit var ttsButton: Button
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        totalAmount = findViewById(R.id.totalAmount)
        numberOfPeople = findViewById(R.id.numberOfPeople)
        result = findViewById(R.id.result)
        shareButton = findViewById(R.id.shareButton)
        ttsButton = findViewById(R.id.ttsButton)

        tts = TextToSpeech(this, this)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateAndDisplayResult()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        totalAmount.addTextChangedListener(textWatcher)
        numberOfPeople.addTextChangedListener(textWatcher)

        shareButton.setOnClickListener {
            shareResult()
        }

        ttsButton.setOnClickListener {
            speakResult()
        }
    }

    private fun calculateAndDisplayResult() {
        val amount = totalAmount.text.toString().toDoubleOrNull()
        val people = numberOfPeople.text.toString().toIntOrNull()

        if (amount != null && people != null && people > 0) {
            val resultValue = amount / people
            val resultText = "Resultado: R$%.2f".format(resultValue)
            result.text = resultText
        } else {
            result.text = "Resultado: "
        }
    }

    private fun shareResult() {
        val resultText = result.text.toString()
        if (resultText.isNotEmpty()) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, resultText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))
        }
    }

    private fun speakResult() {
        val resultText = result.text.toString()
        if (resultText.isNotEmpty()) {
            tts.speak(resultText, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("pt", "BR")
        }
    }

    override fun onDestroy() {
        if (tts != null) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}