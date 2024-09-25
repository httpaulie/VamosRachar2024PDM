package com.example.vamosrachar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val tvHistory: TextView = findViewById(R.id.tvHistory)
        val btnClearHistory: Button = findViewById(R.id.btnClearHistory)

        // Obter SharedPreferences e o histórico salvo como uma string
        val sharedPreferences = getSharedPreferences("LocationHistory", Context.MODE_PRIVATE)
        val historyString = sharedPreferences.getString("history", "")

        // Converter a string de histórico para uma lista, separando por "||"
        val historyList = historyString?.split("||")?.filter { it.isNotBlank() } ?: emptyList()

        // Mostrar o histórico na TextView com espaçamento
        if (historyList.isNotEmpty()) {
            tvHistory.text = historyList.joinToString(separator = "\n\n")
        } else {
            tvHistory.text = "Nenhum histórico salvo"
        }

        // Limpar o histórico quando o botão for clicado
        btnClearHistory.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.remove("history")  // Remover o conjunto de histórico
            editor.apply()

            // Atualizar a TextView para refletir que o histórico foi limpo
            tvHistory.text = "Histórico limpo!"

            Toast.makeText(this, "Histórico limpo com sucesso", Toast.LENGTH_SHORT).show()
        }
    }
}