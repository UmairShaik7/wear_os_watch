package com.example.weatherwatchapp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
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
import java.util.concurrent.ExecutionException

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
        }

        viewBinding.bSecondRegion.setOnClickListener {
            viewBinding.progressBar.visibility = View.VISIBLE
            mainActivityViewModel.fetchWeather(CityCode.HYDERABAD.cityCode)
        }

        mainActivityViewModel.weatherValue.observe(this, Observer {
            viewBinding.progressBar.visibility = View.GONE

            viewBinding.tvWeatherResult.text = getString(
                R.string.weather_result,
                it.main.temp.toString(),
                it.weather?.firstOrNull()?.main.toString()
            )
            weatherData = it.main.temp.toString()
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
}