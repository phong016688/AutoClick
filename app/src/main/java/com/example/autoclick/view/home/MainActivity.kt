package com.example.autoclick.view.home


import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.autoclick.R
import com.example.autoclick.databinding.ActivityMainBinding
import com.example.autoclick.service.AutoClickService
import com.example.autoclick.service.FloatingClickService


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        events()
    }

    private fun events() {
        binding.btnStart.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                val serviceIntent = Intent(this@MainActivity, FloatingClickService::class.java)
                startService(serviceIntent)
                onBackPressed()
            } else {
                askPermission()
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            //val mode = appOpsManager.checkOp(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, android.os.Process.myUid(), packageName)
            //if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mode == AppOpsManager.MODE_ALLOWED)) {
            val hasPermission = isAccessibilityEnabled()
            if (!hasPermission) {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
            if (!Settings.canDrawOverlays(this)) {
                askPermission()
            }
        }, 1000)
    }

    private fun askPermission() {
        val intent =
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivityForResult(intent, PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE && grantResults.isNotEmpty()) {
        }
    }

    override fun onDestroy() {
        stopService(Intent(this, FloatingClickService::class.java))
        stopService(Intent(this, AutoClickService::class.java))
        super.onDestroy()
    }

    private fun isAccessibilityEnabled(): Boolean {
        var accessibilityEnabled = 0
        val accessibilityFound = false
        try {
            accessibilityEnabled =
                Settings.Secure.getInt(this.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: SettingNotFoundException) {
        }
        val mStringColonSplitter = SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessabilityService = mStringColonSplitter.next()
                    if (accessabilityService.equals(
                            getString(R.string.accessibility_service_id),
                            ignoreCase = true
                        )
                    ) {
                        return true
                    }
                }
            }
        }
        return accessibilityFound
    }

    companion object {
        private const val PERMISSION_CODE = 110
    }
}
