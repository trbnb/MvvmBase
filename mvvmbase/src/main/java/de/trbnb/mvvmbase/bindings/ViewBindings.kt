package de.trbnb.mvvmbase.bindings

import android.databinding.BindingAdapter
import android.view.View

@BindingAdapter("android:visible")
fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}
