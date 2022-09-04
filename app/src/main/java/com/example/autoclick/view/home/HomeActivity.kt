package com.example.autoclick.view.home


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.example.autoclick.databinding.ActivityHomeBinding
import com.example.autoclick.service.AutoClickService
import com.example.autoclick.service.FloatingClickService


class HomeActivity : AppCompatActivity() {
    private val TAG = "#####" + this::class.java.simpleName
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        events()
    }

    private fun events() {
        binding.btnStart.setOnClickListener {
            if (!isAccessibilityEnabled()) {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } else if (!Settings.canDrawOverlays(this)) {
                val uri = Uri.parse("package:$packageName")
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
                startActivity(intent)
            } else {
                startClickService()
                backToHomeScreen()
            }
        }
    }

    private fun backToHomeScreen() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    private fun startClickService() {
        val serviceIntent = Intent(this, FloatingClickService::class.java)
        startService(serviceIntent)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onDestroy() {
        stopService(Intent(this, FloatingClickService::class.java))
        stopService(Intent(this, AutoClickService::class.java))
        super.onDestroy()
    }

    private fun isAccessibilityEnabled(): Boolean {
        return runCatching {
            val clazz = AutoClickService::class.java
            Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ).contains("$packageName/${clazz.`package`?.name}.${clazz.simpleName}")
        }.getOrDefault(false)
    }
}
