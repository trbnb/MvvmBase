package androidx.lifecycle

internal fun <T : Any> ViewModel.getTag(key: String): T? = getTag(key)
internal fun <T : Any> ViewModel.setTagIfAbsent(key: String, item: T) = setTagIfAbsent(key, item)