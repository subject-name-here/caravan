package com.unicorns.invisible.caravan.toast

import android.widget.Toast
import com.unicorns.invisible.caravan.MainActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


fun showToast(activity: MainActivity, message: String) {
    MainScope().launch {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}