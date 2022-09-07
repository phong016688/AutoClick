package com.example.autoclick.view.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autoclick.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private val venuesAdapter by lazy { VenuesAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        handleEvents()
    }

    private fun handleEvents() {

    }

    private fun initView() {
        binding.rvVenues.layoutManager = LinearLayoutManager(this)
        binding.rvVenues.adapter = venuesAdapter
        venuesAdapter.submitList((1..100).map { ItemVenues(it.toString()) })
    }
}