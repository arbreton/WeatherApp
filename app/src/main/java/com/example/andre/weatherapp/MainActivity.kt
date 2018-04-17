package com.example.andre.weatherapp

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val places = arrayOf("Tokyo","Copenhagen","Nassau","Greenland","Paris","Madrid","Stockholm","Vancouver","Rio","Moscu","Nairobi","Denver","Helsinki","Berlin","Tijuana","Mexicali","Mexico City","Argentina")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        weather_icon.setTypeface(weatherFont)

        places.sort()

        val itemsAdapter = ArrayAdapter<String>(this, R.layout.centertext, places)
        places_listview.adapter = itemsAdapter
        places_listview.setOnItemClickListener{
            parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val asyncTask = WeatherAPI.placeIdTask(object : WeatherAPI.AsyncResponse {
                override fun processFinish(weather_city: String, weather_description: String, weather_temperature: String, weather_humidity: String, weather_pressure: String, weather_updatedOn: String, weather_iconText: String, sun_rise: String) {
                    city_field.setText(weather_city)
                    updated_field.setText(weather_updatedOn)
                    details_field.setText(weather_description)
                    current_temperature_field.setText(weather_temperature)
                    humidity_field.setText("Humidity: $weather_humidity")
                    pressure_field.setText("Pressure: $weather_pressure")
                    weather_icon.setText(Html.fromHtml(weather_iconText));
                }
            })
            asyncTask.execute(places[position])
        }

        places_listview.performItemClick(places_listview.getChildAt(0), 0, places_listview.getItemIdAtPosition(0));

    }
}
