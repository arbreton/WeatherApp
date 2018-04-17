package com.example.andre.weatherapp
import android.os.AsyncTask
import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.DateFormat
import java.util.Date
import java.util.Locale

object WeatherAPI {

    private val OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5/weather?q="
    private val OPEN_WEATHER_UNITS = "&units=metric"
    private val OPEN_WEATHER_APIKEY = "&APPID=a9d881232fb70bb64f84dc9c79255dc8"


    interface AsyncResponse {
        fun processFinish(output1: String, output2: String, output3: String, output4: String, output5: String, output6: String, output7: String, output8: String)
    }


    fun setWeatherIcon(actualId: Int, sunrise: Long, sunset: Long): String {
        val id = actualId / 100
        var icon = ""
        if (actualId == 800) {
            val currentTime = Date().time
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = "&#xf00d;"
            } else {
                icon = "&#xf02e;"
            }
        } else {
            when (id) {
                2 -> icon = "&#xf01e;"
                3 -> icon = "&#xf01c;"
                7 -> icon = "&#xf014;"
                8 -> icon = "&#xf013;"
                6 -> icon = "&#xf01b;"
                5 -> icon = "&#xf019;"
            }
        }
        return icon
    }

    class placeIdTask(asyncResponse: AsyncResponse) : AsyncTask<String, Void, JSONObject>() {

        var delegate: AsyncResponse? = null//Call back interface

        init {
            delegate = asyncResponse//Assigning call back interfacethrough constructor
        }

        override fun doInBackground(vararg params: String): JSONObject? {
            var jsonWeather: JSONObject? = null
            try {
                jsonWeather = getWeatherJSON(params[0])
            } catch (e: Exception) {
                Log.d("Error", "Cannot process JSON results", e)
            }
            return jsonWeather
        }

        override fun onPostExecute(json: JSONObject?) {
            try {
                if (json != null) {
                    val details = json.getJSONArray("weather").getJSONObject(0)
                    val main = json.getJSONObject("main")
                    val df = DateFormat.getDateTimeInstance()


                    val city = json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country")
                    val description = details.getString("description").toUpperCase(Locale.US)
                    val temperature = String.format("%.2f", main.getDouble("temp")) + "°"
                    val humidity = main.getString("humidity") + "%"
                    val pressure = main.getString("pressure") + " hPa"
                    val updatedOn = df.format(Date(json.getLong("dt") * 1000))
                    val iconText = setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000)

                    delegate?.processFinish(city, description, temperature, humidity, pressure, updatedOn, iconText,"" + json.getJSONObject("sys").getLong("sunrise") * 1000)

                }
            } catch (e: JSONException) {
                //Log.e(LOG_TAG, "Cannot process JSON results", e);
            }
        }
    }


    fun getWeatherJSON(placeName :String): JSONObject? {
        try {
            val url = OPEN_WEATHER_MAP_URL + placeName + OPEN_WEATHER_UNITS + OPEN_WEATHER_APIKEY

            val request  = Request.Builder().url(url).build()

            var client = OkHttpClient()
            var data :JSONObject = JSONObject()

            val response = client.newCall(request).execute()

            if (response.isSuccessful){
                val body = response?.body()?.string()
                val parser = JsonParser()
                val gson = parser.parse(body) as JsonObject

                Log.i("JSON", body)
                data = JSONObject(gson.toString())
                Log.i("JSON", data.toString())
            }

            return if (data.getInt("cod") != 200) {
                null
            } else data

        } catch (e: Exception) {
            return null
        }
    }



}