package de.trbnb.base.mvvm

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import de.trbnb.apptemplate.app.App
import de.trbnb.apptemplate.app.AppComponent
import javax.inject.Inject
import javax.inject.Provider

abstract class MvvmFragment<VM : ViewModel> : Fragment(), LoaderManager.LoaderCallbacks<VM> {

    protected lateinit var viewModel: VM

    protected abstract val loaderID: Int

    @Inject
    lateinit var viewModelLoaderProvider: Provider<ViewModelLoader<VM>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectDependencies(App.appComponent)
        initLoader()
    }

    abstract fun injectDependencies(graph: AppComponent)

    abstract fun onViewModelLoaded(viewModel: VM)

    private fun initLoader(){
        activity.supportLoaderManager.initLoader(loaderID, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<VM> {
        return viewModelLoaderProvider.get()
    }

    override fun onLoadFinished(loader: Loader<VM>, data: VM) {
        viewModel = data
        onViewModelLoaded(data)
    }

    override fun onLoaderReset(loader: Loader<VM>?) {
        viewModel.onDestroy()
    }
}