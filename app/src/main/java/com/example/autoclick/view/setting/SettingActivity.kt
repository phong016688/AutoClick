package com.example.autoclick.view.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.autoclick.R
import com.example.autoclick.databinding.ActivitySettingBinding
import com.example.autoclick.utils.showInputDialog
import com.example.autoclick.view.history.HistoryActivity
import com.example.autoclick.view.home.HomeActivity

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySettingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        handleEvents()
    }

    private fun handleEvents() = binding.apply {
        header.ivKey.setOnClickListener { showInputDialog() }
        btGoHome.clRootManage.setOnClickListener {
            val intent = Intent(this@SettingActivity, HomeActivity::class.java)
            startActivity(intent)
        }
        btHistory.clRootManage.setOnClickListener {
            val intent = Intent(this@SettingActivity, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initView() {
        binding.btGoHome.tvName.text = getString(R.string.go_to_home)
        binding.btHistory.tvName.text = getString(R.string.go_to_history)
        binding.btProfile.tvName.text = getString(R.string.go_to_profile)
        binding.btAbout.tvName.text = getString(R.string.go_to_about)
    }
}