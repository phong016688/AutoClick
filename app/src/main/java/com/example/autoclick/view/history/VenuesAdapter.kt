package com.example.autoclick.view.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autoclick.R

class VenuesAdapter : ListAdapter<ItemVenues, VenuesAdapter.VenuesVH>(
    object : DiffUtil.ItemCallback<ItemVenues>() {
        override fun areItemsTheSame(oldItem: ItemVenues, newItem: ItemVenues): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ItemVenues, newItem: ItemVenues): Boolean {
            return oldItem == newItem
        }
    }) {

    inner class VenuesVH(iv: View) : RecyclerView.ViewHolder(iv) {
        private var item: ItemVenues? = null

        fun bind(itemVenues: ItemVenues) {
            this.item = itemVenues
        }
    }

    var listener : ItemVenuesClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenuesVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_venues, parent, false)
        return VenuesVH(view)
    }

    override fun onBindViewHolder(holder: VenuesVH, position: Int) {
        holder.bind(getItem(position))
    }
}

interface ItemVenuesClickListener{
    fun onOpenMap()
}