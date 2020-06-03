package androidx.lifecycle

internal fun <T : Any> ViewModel.getTagFromViewModel(key: String): T? = getTag(key)
internal fun <T : Any> ViewModel.setTagIfAbsentForViewModel(key: String, newValue: T) = setTagIfAbsent(key, newValue)
