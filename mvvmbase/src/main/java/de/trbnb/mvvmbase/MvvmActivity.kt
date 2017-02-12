package de.trbnb.mvvmbase

import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import javax.inject.Provider

private const val LOADER_ID = 0

abstract class MvvmActivity<VM : ViewModel> : AppCompatActivity(), LoaderManager.LoaderCallbacks<VM> {

    protected lateinit var binding: ViewDataBinding
    protected var viewModel: VM? = null
        set(value) {
            if(field === value) return

            field?.removeOnPropertyChangedCallback(viewModelObserver)

            field = value
            val bindingWasSuccessful = binding.setVariable(viewModelBindingId, value)

            if(!bindingWasSuccessful){
                throw RuntimeException("Unable to set the ViewModel for the variable $viewModelBindingId.")
            }

            value?.let {
                onViewModelLoaded(it)
                it.addOnPropertyChangedCallback(viewModelObserver)
            }
        }

    private val viewModelBindingId: Int
        get() = BR.vm

    protected abstract val layoutId: Int

    abstract val viewModelProvider: Provider<VM>

    private val viewModelObserver = object : Observable.OnPropertyChangedCallback(){
        @Suppress("UNCHECKED_CAST")
        override fun onPropertyChanged(sender: Observable, fieldId: Int) {
            onViewModelPropertyChanged(sender as VM, fieldId)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = initBinding()

        initLoader()
    }

    private fun initBinding(): ViewDataBinding = DataBindingUtil.setContentView(this, layoutId)

    // region Loader
    private fun initLoader(){
        supportLoaderManager.initLoader(LOADER_ID, null, this)
    }

    override final fun onCreateLoader(id: Int, args: Bundle?): Loader<VM> {
        return ViewModelLoader(this, viewModelProvider)
    }

    override final fun onLoadFinished(loader: Loader<VM>, data: VM) {
        this.viewModel = data
    }

    override final fun onLoaderReset(loader: Loader<VM>?) {
        // nothing to do here
    }
    //endregion

    protected open fun onViewModelLoaded(viewModel: VM){

    }

    protected open fun onViewModelPropertyChanged(viewModel: VM, fieldId: Int){

    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel?.removeOnPropertyChangedCallback(viewModelObserver)

        if(isFinishing){
            viewModel?.onViewFinishing()
        }
    }
}