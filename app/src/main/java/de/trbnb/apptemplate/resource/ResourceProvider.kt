package de.trbnb.apptemplate.resource

import androidx.annotation.StringRes

@Suppress("UndocumentedPublicClass")
interface ResourceProvider {
    @Suppress("UndocumentedPublicFunction")
    fun getString(@StringRes resId: Int, vararg args: Any): String
}
