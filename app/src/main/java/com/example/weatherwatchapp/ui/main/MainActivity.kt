package com.example.weatherwatchapp.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.weatherwatchapp.R
import com.example.weatherwatchapp.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale
import java.util.concurrent.ExecutionException
import kotlin.collections.HashSet

class MainActivity : AppCompatActivity() {

    companion object {
        private const val START_ACTIVITY_PATH = "/start-activity-path"
    }

    private lateinit var weatherData: String
    private lateinit var viewBinding: ActivityMainBinding

    private val mainActivityViewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewBinding.viewmodel = mainActivityViewModel

        viewBinding.bFirstRegion.setOnClickListener {
            viewBinding.progressBar.visibility = View.VISIBLE
            mainActivityViewModel.fetchWeather(CityCode.BANGALORE.cityCode)
            mainActivityViewModel.selectedCity.value = CityCode.BANGALORE.toString()
        }

        viewBinding.bSecondRegion.setOnClickListener {
            viewBinding.progressBar.visibility = View.VISIBLE
            mainActivityViewModel.fetchWeather(CityCode.CHENNAI.cityCode)
            mainActivityViewModel.selectedCity.value = CityCode.CHENNAI.toString()
        }

        mainActivityViewModel.weatherValue.observe(this, Observer {
            viewBinding.progressBar.visibility = View.GONE
            viewBinding.tvWeatherResult.text = getString(
                R.string.weather_result,
                it.main.temp.toString(),
                it.weather?.firstOrNull()?.main.toString()
            )
            weatherData = it.main.temp.toString()

            val tempRange = when (it.main.temp) {
                in 0.0F..20.0F -> {
                    "partly_cloudy.json"
                }
                in 20.0F..25.0F -> {
                    "raining.json"
                }
                else -> {
                    "sunny_day.json"
                }
            }
            viewBinding.lottieWeatherAnimation.apply {
                setAnimation(tempRange)
                playAnimation()
            }
            if (mainActivityViewModel.selectedCity.value == CityCode.BANGALORE.toString()) {
                notifyWeather(
                    CityCode.BANGALORE.toString(),
                    CityCode.BANGALORE.latitude,
                    CityCode.BANGALORE.longitude,
                    it.main.temp.toString()
                )
            } else {
                notifyWeather(
                    CityCode.CHENNAI.toString(),
                    CityCode.CHENNAI.latitude,
                    CityCode.CHENNAI.longitude,
                    it.main.temp.toString()
                )
            }

            lifecycleScope.launch {
                startNodes()
            }
        })
    }

    private suspend fun startNodes() {
        withContext(context = Dispatchers.Default) {

            val nodes: Collection<String>? = getNodes()
            if (nodes != null) {
                for (node in nodes) {
                    sendStartActivityMessage(node)
                }
            }
        }
    }

    @WorkerThread
    private fun getNodes(): Collection<String>? {
        val results = HashSet<String>()
        val nodeListTask: Task<List<Node>> =
            Wearable.getNodeClient(applicationContext).connectedNodes
        try {
            // Block on a task and get the result synchronously (because this is on a background
            // thread).
            val nodes: List<Node> =
                Tasks.await(nodeListTask)
            for (node in nodes) {
                results.add(node.id)
            }
        } catch (exception: ExecutionException) {
            Log.e("TAG", "Task failed: $exception")
        } catch (exception: InterruptedException) {
            Log.e("TAG", "Interrupt occurred: $exception")
        }
        return results
    }

    @WorkerThread
    private fun sendStartActivityMessage(node: String) {
        val sendMessageTask = Wearable.getMessageClient(this).sendMessage(
            node,
            START_ACTIVITY_PATH,
            weatherData.toByteArray()
        )
        try {
            val result = Tasks.await(sendMessageTask)
            Log.i("TAG", "Message sent: $result")
        } catch (exception: ExecutionException) {
            Log.e("TAG", "Task failed: $exception")
        } catch (exception: InterruptedException) {
            Log.e(
                "TAG",
                "Interrupt occurred: $exception"
            )
        }
    }

    private fun notifyWeather(
        city: String,
        latitude: Float,
        longitude: Float,
        temperature: String
    ) {

        val builder =
            NotificationCompat.Builder(applicationContext, "notify")
        val uri: String =
            java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText(
            HtmlCompat.fromHtml(
                getString(
                    R.string.notification_message,
                    city,
                    temperature
                ), HtmlCompat.FROM_HTML_MODE_COMPACT
            )
        )

        builder.setContentIntent(pendingIntent)
        builder.setSmallIcon(R.drawable.map_icon)
        builder.setContentTitle(
            HtmlCompat.fromHtml(
                "<font color=\"" + ContextCompat.getColor(
                    baseContext,
                    R.color.royal_blue
                ) + "\">" + getString(R.string.weather_notification_title) + "</font>",
                HtmlCompat.FROM_HTML_MODE_COMPACT
            )
        )
        builder.setStyle(bigText)
        builder.addAction(
            R.drawable.map_icon,
            HtmlCompat.fromHtml(
                "<font color=\"" + ContextCompat.getColor(
                    baseContext,
                    R.color.royal_blue
                ) + "\">" + getString(R.string.notification_action) + "</font>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            ),
            pendingIntent
        )
        builder.setAutoCancel(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "notify"
            val channel = NotificationChannel(
                channelId,
                "Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(channelId)
        }

        notificationManager.notify(0, builder.build())
    }
}