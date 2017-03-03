package de.trbnb.mvvmbase

import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import javax.inject.Provider

private const val LOADER_ID_KEY = "de.trbnb.mvvmbase:loader_id"

abstract class MvvmFragment<VM : ViewModel> : Fragment(), LoaderManager.LoaderCallbacks<VM> {

    private var loaderID: Int = LoaderIdGenerator.NO_ID

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedLoaderId = savedInstanceState?.getInt(LOADER_ID_KEY, LoaderIdGenerator.NO_ID) ?: loaderID

        loaderID = when (savedLoaderId) {
            LoaderIdGenerator.NO_ID -> LoaderIdGenerator.generate()
            else                    -> savedLoaderId
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(LOADER_ID_KEY, loaderID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = initBinding(inflater, container)

        initLoader()

        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?): ViewDataBinding {
        return DataBindingUtil.inflate(inflater, layoutId, container, false)
    }

    //region Loader
    private fun initLoader(){
        activity.supportLoaderManager.initLoader(loaderID, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<VM> {
        return ViewModelLoader(context, viewModelProvider)
    }

    override fun onLoadFinished(loader: Loader<VM>, data: VM) {
        viewModel = data
    }

    override fun onLoaderReset(loader: Loader<VM>?) {
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

        if(activity.isFinishing){
            viewModel?.onViewFinishing()
        }
    }
}