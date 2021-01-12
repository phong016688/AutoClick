package com.example.autoclick


import android.annotation.TargetApi
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.autoclick.service.AutoClickService
import com.example.autoclick.service.FloatingClickService


class MainActivity : AppCompatActivity() {
    companion object{
        private const val PERMISSION_CODE = 110
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button).setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                val serviceIntent = Intent(this@MainActivity, FloatingClickService::class.java)
                startService(serviceIntent)
                onBackPressed()
            } else {
                askPermission()
                shortToast("You need System Alert Window Permission to do this")
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    /* private fun checkAccess(): Boolean {
         val string = getString(R.string.accessibility_service_id)
         val manager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
         val list = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
         for (id in list) {
             if (string == id.id) {
                 return true
             }
         }
         return false
     }*/

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

    @TargetApi(Build.VERSION_CODES.M)
    private fun askPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivityForResult(intent, PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE && grantResults.isNotEmpty()) {
            "permissions grand".logd("##############")
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
            accessibilityEnabled = Settings.Secure.getInt(this.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: SettingNotFoundException) {
        }
        val mStringColonSplitter = SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessabilityService = mStringColonSplitter.next()
                    if (accessabilityService.equals(getString(R.string.accessibility_service_id), ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return accessibilityFound
    }
}
