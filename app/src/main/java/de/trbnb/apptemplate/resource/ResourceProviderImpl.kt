package de.trbnb.apptemplate.resource

import android.content.Context
import javax.inject.Inject

@Suppress("UndocumentedPublicClass")
class ResourceProviderImpl @Inject constructor(private val context: Context) : ResourceProvider {
    override fun getString(resId: Int, vararg args: Any): String = context.getString(resId, *args)
}
