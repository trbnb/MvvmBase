package de.trbnb.mvvmbase

import android.content.Context
import android.support.v4.content.Loader
import javax.inject.Provider

/**
 * [Loader] implementation that is used to save a [ViewModel] throughout the lifecycle of a view.
 *
 * @param[VM] Specific type of the [ViewModel] implementation.
 * @param[context] Is needed by the [Loader] base class.
 *
 * @property[viewModelProvider] [Provider] implementation that is used to create a new instance of the [ViewModel].
 *                              Is only used one time, when the Loader is
 *                              initialized for the first time.
 */
class ViewModelLoader<VM : ViewModel> (context: Context, private val viewModelProvider: Provider<VM>) : Loader<VM>(context){

    /**
     * Stores the [ViewModel] instance.
     * Will only be set once.
     */
    private var viewModel: VM? = null

    /**
     * Is called by the framework when the Loader is initialized.
     *
     * Delivers the [ViewModel] instance if one is available.
     * If not, the creation of a new one is forced.
     */
    override fun onStartLoading() {
        super.onStartLoading()

        if(viewModel != null){
            deliverResult(viewModel)
        } else {
            forceLoad()
        }
    }

    /**
     * Creates new [ViewModel] instance and delivers it.
     */
    override fun onForceLoad() {
        super.onForceLoad()

        viewModel = viewModelProvider.get()
        deliverResult(viewModel)
    }

    /**
     * Is called by the framework when this Loader is reset.
     *
     * In this case the Loader and so the [ViewModel] instance are about to be garbage collected.
     */
    override fun onReset() {
        super.onReset()

        viewModel?.onDestroy()
    }

}