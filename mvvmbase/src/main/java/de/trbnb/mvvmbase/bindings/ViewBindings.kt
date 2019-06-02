package de.trbnb.mvvmbase.bindings

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("android:visible")
fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}
