package com.example.kk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorApp()
        }
    }
}

@Composable
fun CalculatorApp() {
    var input by remember { mutableStateOf("") }
    var history by remember { mutableStateOf(listOf<String>()) }

    Column(modifier = Modifier.fillMaxSize()) {
        // History Display
        Column(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("History:", fontSize = 20.sp)
            history.forEach { entry ->
                Text(entry, fontSize = 16.sp)
            }
        }

        // Input Field
        BasicTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 32.sp)
        )

        // Row for calculator buttons
        val buttons = listOf(
            "C","(-)", "%", "+",
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ",","="
        )

        buttons.chunked(4).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                row.forEach { button ->
                    Button(
                        onClick = {
                            when (button) {
                                "C" -> input = ""
                                "=" -> {
                                    val result = evaluateExpression(input)
                                    history = history + "$input = $result"
                                    input = result.toString()
                                }
                                "(-)" -> {
                                    input = if (input.startsWith("-")) {
                                        input.substring(1) // Убираем знак минус
                                    } else {
                                        "-$input" // Добавляем знак минус
                                    }
                                }
                                "%" -> {
                                    if (input.isNotEmpty()) {
                                        val percentage = evaluateExpression(input) / 100
                                        input = percentage.toString()
                                    }
                                }
                                // Обработка запятой
                                "," -> {
                                    if (!input.contains(",")) {
                                        input += ","
                                    }
                                }
                                else -> input += button
                            }
                        },
                        modifier = Modifier.weight(1f).padding(5.dp)
                    ) {
                        Text(button)
                    }
                }
            }
        }
    }
}

fun evaluateExpression(expression: String): Double {
    // Заменяем запятую на точку для правильного парсинга
    val formattedExpression = expression.replace(",", ".")

    // Простой парсер для базовых арифметических операций
    return try {
        val formatted = formattedExpression.replace(" ", "")
        when {
            formatted.contains("+") -> {
                val parts = formatted.split("+")
                parts[0].toDouble() + parts[1].toDouble()
            }
            formatted.contains("-") -> {
                val parts = formatted.split("-")
                parts[0].toDouble() - parts[1].toDouble()
            }
            formatted.contains("*") -> {
                val parts = formatted.split("*")
                parts[0].toDouble() * parts[1].toDouble()
            }
            formatted.contains("/") -> {
                val parts = formatted.split("/")
                parts[0].toDouble() / parts[1].toDouble()
            }
            formatted.contains("^") -> {
                val parts = formatted.split("^")
                parts[0].toDouble().pow(parts[1].toDouble())
            }
            else -> formatted.toDouble()
        }
    } catch (e: Exception) {
        Double.NaN
    }
}
