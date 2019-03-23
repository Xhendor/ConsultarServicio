package com.edu.uabc.appm.consultarservicio

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.edu.uabc.appm.consultarservicio.modelo.User
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.async
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    lateinit var urlConnection:HttpURLConnection
    lateinit var reader:BufferedReader
    lateinit var result:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val dialog = progressDialog(message = "Please wait a bit…", title = "Fetching data")
            dialog.show()
            doAsync {
                val result = URL("https://jsonplaceholder.typicode.com/users").readText()
                uiThread {
                    Log.d("Request", result)

                    var gson:Gson = Gson()
                    var usuarios= gson.fromJson(result,Array<User>::class.java)

                   for (user in usuarios){

                       Log.d("Usuario", user.name)

                   }

                    //Crear alertas
                    alert("Numero de usuarios obtenidos ${usuarios.size}", "Se llamo un recurso web") {
                        yesButton { toast("Entendido…") }
                        noButton {

                            val countries = listOf("Russia", "USA", "Japan", "Australia")
                            selector("De donde eres tu?", countries) { j  ->

                                toast("Ok, entonces vives en: ${countries[j]}, verdad?")

                            }


                        }
                    }.show()
                    dialog.cancel()
                    longToast("Request performed")
                }
            }
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    public fun caller(apiCall:String): String? {

        //Tipica funciona de llamada a URL
        try {
            val url = URL(apiCall)

            var urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.setRequestMethod("GET")
            urlConnection.connect()

            val inputStream = urlConnection.getInputStream()
            val buffer = StringBuffer()
            if (inputStream == null) {
                // Nothing to do.
                return null
            }
            reader = BufferedReader(InputStreamReader(inputStream))

            var line: String=""
            while ({ line = reader.readLine(); line }() != null) {

                buffer.append(line + "\n")
            }

            if (buffer.length == 0) {
                return null
            }
            result = buffer.toString()
        } catch (e: IOException) {
            Log.e("Request", "Error ", e)
            return null
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect()
            }
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    Log.e("Request", "Error closing stream", e)
                }

            }
        }
    return null
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
