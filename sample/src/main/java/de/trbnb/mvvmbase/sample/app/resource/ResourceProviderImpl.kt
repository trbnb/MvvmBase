package de.trbnb.mvvmbase.sample.app.resource

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProviderImpl @Inject constructor(@ApplicationContext private val context: Context) : ResourceProvider {
    override fun getString(resId: Int, vararg args: Any): String = context.getString(resId, *args)
}
