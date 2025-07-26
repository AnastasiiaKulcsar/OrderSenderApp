package com.example.ordersenderapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editPrice: EditText
    private lateinit var buttonSend: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editName = findViewById(R.id.editName)
        editPrice = findViewById(R.id.editPrice)
        buttonSend = findViewById(R.id.buttonSend)

        buttonSend.setOnClickListener {
            sendOrder()
        }
    }

    private fun sendOrder() {
        val name = editName.text.toString()
        val price = editPrice.text.toString()

        if (name.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Please enter both name and price", Toast.LENGTH_SHORT).show()
            return
        }

        thread {
            try {
                val url = URL("http://192.168.1.170:5000/add_order")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")

                val json = """{"name":"$name", "price":$price}"""
                val outputStream: OutputStream = conn.outputStream
                outputStream.write(json.toByteArray())
                outputStream.flush()
                outputStream.close()

                val responseCode = conn.responseCode
                runOnUiThread {
                    if (responseCode == 200) {
                        Toast.makeText(this, "Order sent!", Toast.LENGTH_SHORT).show()
                        editName.text.clear()
                        editPrice.text.clear()
                    } else {
                        Toast.makeText(this, "Server error: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }

                conn.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
