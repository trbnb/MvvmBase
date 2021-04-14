package de.trbnb.apptemplate.app.resource

import androidx.annotation.StringRes

interface ResourceProvider {
    fun getString(@StringRes resId: Int, vararg args: Any): String
}
