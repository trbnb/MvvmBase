package de.trbnb.mvvmbase

import androidx.databinding.Observable

/**
 * [Observable.OnPropertyChangedCallback] implementation that delegates invocations to [MvvmView.onViewModelPropertyChanged]
 * of a given [MvvmView].
 */
class ViewModelPropertyChangedCallback<VM>(
    private val mvvmView: MvvmView<VM, *>
) : Observable.OnPropertyChangedCallback() where VM : ViewModel, VM : androidx.lifecycle.ViewModel {
    @Suppress("UNCHECKED_CAST")
    override fun onPropertyChanged(sender: Observable, fieldId: Int) {
        mvvmView.onViewModelPropertyChanged(sender as? VM ?: return, fieldId)
    }
}
