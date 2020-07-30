package com.example.weatherwatchapp

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.wearable.MessageClient.OnMessageReceivedListener
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.android.synthetic.main.activity_main.text

class MainActivity : FragmentActivity(), OnMessageReceivedListener{

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

    override fun onMessageReceived(event: MessageEvent) {
        text.text= String(event.data)
        Toast.makeText(this,"Received",Toast.LENGTH_SHORT).show()
    }
}
