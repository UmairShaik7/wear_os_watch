package com.example.weatherwatchapp

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.wearable.MessageClient.OnMessageReceivedListener
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.android.synthetic.main.activity_main.temperature
import kotlinx.android.synthetic.main.activity_main.lottieWeatherAnimation

class WearableActivity : FragmentActivity(), OnMessageReceivedListener{

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        Wearable.getMessageClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getMessageClient(this).removeListener(this)
    }

    //Receiving Message from Companion Phone device
    override fun onMessageReceived(event: MessageEvent) {
        temperature.text= getString(R.string.temperature, String(event.data))

        val tempRange = when {
            String(event.data) in "0".."20" -> {
                "raining.json"
            }
            String(event.data) in "20".."30" -> {
                "partly_cloudy.json"
            }
            else -> {
                "sunny_day.json"
            }
        }

        lottieWeatherAnimation.apply {
            setAnimation(tempRange)
            playAnimation()
        }

        Toast.makeText(this,"Received",Toast.LENGTH_SHORT).show()
    }
}