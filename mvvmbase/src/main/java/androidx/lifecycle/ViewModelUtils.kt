package androidx.lifecycle

internal fun <T : Any> ViewModel.getTagFromViewModel(key: String): T? = getTag(key)
internal fun <T : Any> ViewModel.setTagIfAbsentForViewModel(key: String, newValue: T): T = setTagIfAbsent(key, newValue)

internal fun ViewModel.destroyInternal() {
    clear()
}