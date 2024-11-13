package com.example.uts_iot1

import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button: Button = findViewById(R.id.my_button)
        val textView: TextView = findViewById(R.id.textView)

        button.setOnClickListener {
            fetchData(textView)
        }
    }

    private fun fetchData(textView: TextView) {
        // Create a queue for network requests
        val queue = Volley.newRequestQueue(this)

        // API URL
        val url = "http://10.0.2.2/backend/datacuaca.php"

        // Make a JSON request to the API
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    // Extract the top-level values
                    val suhuMax = response.getDouble("suhu_max")
                    val suhuMin = response.getDouble("suhu_min")
                    val suhuAvg = response.getDouble("suhu_avg")

                    // Get the nilai_suhu_max_humid_max array
                    val nilaiSuhuMaxHumidMaxArray = response.getJSONArray("nilai_suhu_max_humid_max")
                    val nilaiSuhuMaxHumidMaxText = StringBuilder()

                    for (i in 0 until nilaiSuhuMaxHumidMaxArray.length()) {
                        val dataObject = nilaiSuhuMaxHumidMaxArray.getJSONObject(i)
                        val id = dataObject.getInt("id")
                        val suhu = dataObject.getDouble("suhu")
                        val humid = dataObject.getDouble("humid")
                        val kecerahan = dataObject.getDouble("kecerahan")
                        val timestamp = dataObject.getString("timestamp")

                        nilaiSuhuMaxHumidMaxText.append("<b>ID:</b> $id<br>")
                        nilaiSuhuMaxHumidMaxText.append("<b>Temperature:</b> $suhu°C<br>")
                        nilaiSuhuMaxHumidMaxText.append("<b>Humidity:</b> $humid%<br>")
                        nilaiSuhuMaxHumidMaxText.append("<b>Kecerahan:</b> $kecerahan<br>")
                        nilaiSuhuMaxHumidMaxText.append("<b>Timestamp:</b> $timestamp<br>")
                        nilaiSuhuMaxHumidMaxText.append("<hr>") // Horizontal line
                    }

                    // Get the month_year_max array
                    val monthYearMaxArray = response.getJSONArray("month_year_max")
                    val monthYearMaxText = StringBuilder()

                    for (i in 0 until monthYearMaxArray.length()) {
                        val monthYearObject = monthYearMaxArray.getJSONObject(i)
                        val monthYear = monthYearObject.getString("month_year")
                        monthYearMaxText.append("<b>Month-Year:</b> $monthYear<br>")
                    }

                    // Combine all the text to display using HTML formatting
                    val resultText = """
                        <p style="color:#4CAF50;"><b>Max Temperature:</b></p> $suhuMax°C<br>
                        <p style="color:#4CAF50;"><b>Min Temperature:</b></p> $suhuMin°C<br>
                        <p style="color:#4CAF50;"><b>Average Temperature:</b></p> $suhuAvg°C<br><br>

                        <p style="color:#4CAF50;"><b>Max Temperature and Humidity Data:</b></p>
                        $nilaiSuhuMaxHumidMaxText<br>

                        <p style="color:#4CAF50;"><b>Month-Year Max Data:</b></p>
                        $monthYearMaxText
                    """.trimIndent()

                    // Display the formatted text in the TextView
                    textView.text = Html.fromHtml(resultText, Html.FROM_HTML_MODE_COMPACT)

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                // Handle error
                Toast.makeText(this, "Network Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        // Add the request to the queue
        queue.add(jsonObjectRequest)
    }
}