package de.trbnb.base.mvvm

import android.content.Context
import android.support.v4.content.Loader
import javax.inject.Provider

class ViewModelLoader<VM : ViewModel> (
        context: Context,
        private val viewModelProvider: Provider<VM>
) : Loader<VM>(context){

    private var viewModel: VM? = null

    override fun onStartLoading() {
        super.onStartLoading()

        if(viewModel != null){
            deliverResult(viewModel)
        } else {
            forceLoad()
        }
    }

    override fun onForceLoad() {
        super.onForceLoad()

        viewModel = viewModelProvider.get()
        deliverResult(viewModel)
    }

    override fun onReset() {
        super.onReset()

        viewModel?.onDestroy()
    }

}