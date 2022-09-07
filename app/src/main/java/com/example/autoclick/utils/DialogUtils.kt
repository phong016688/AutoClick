package com.example.autoclick.utils

import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.example.autoclick.databinding.DialogBasicBinding

private var alertDialog: AlertDialog? = null

fun FragmentActivity.showInputDialog(
    okCallback: (() -> Unit)? = null
) {
    dismissDialog()
    val binding = DialogBasicBinding.inflate(LayoutInflater.from(this))
    binding.btOk.setOnClickListener { dismissDialog() }
    alertDialog = AlertDialog.Builder(this)
        .setView(binding.root)
        .show()
}

fun FragmentActivity.dismissDialog() {
    try {
        alertDialog?.dismiss()
        alertDialog = null
    } catch (e: Exception) {
        Log.e("dismissAlertDialog", e.message.toString())
    }
}