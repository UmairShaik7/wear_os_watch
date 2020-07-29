package com.example.weatherwatchapp.ui.main

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.weatherwatchapp.R
import com.example.weatherwatchapp.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private val mainActivityViewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewBinding.viewmodel = mainActivityViewModel
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        viewBinding.bFirstRegion.setOnClickListener {
            progressBar?.visibility = View.VISIBLE
            mainActivityViewModel.fetchWeather(CityCode.BANGALORE.cityCode)
        }

        viewBinding.bSecondRegion.setOnClickListener {
            progressBar?.visibility = View.VISIBLE
            mainActivityViewModel.fetchWeather(CityCode.HYDERABAD.cityCode)
        }

        mainActivityViewModel.weatherValue.observe(this, Observer {
            progressBar?.visibility = View.GONE

            viewBinding.tvWeatherResult.text = baseContext.getString(
                R.string.weather_result, it.main.temp.toString(), it.weather?.get(
                    index = 0
                )?.main.toString()
            )
        })
    }
}