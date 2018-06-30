package de.trbnb.apptemplate.app

import android.app.Application
import de.trbnb.apptemplate.BR
import de.trbnb.apptemplate.main.MainViewModel
import de.trbnb.apptemplate.second.SecondViewModel
import de.trbnb.mvvmbase.bindableproperty.BindableProperty
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

class App : Application() {

    private val appModule: Module = applicationContext {
        // viewmodels
        viewModel { MainViewModel(get()) }
        viewModel { SecondViewModel() }
        // providers, e.g. repositories, services, app context, etc.
        bean { this@App as Application }
    }

    override fun onCreate() {
        super.onCreate()
        BindableProperty.init<BR>()
        startKoin(this, listOf(appModule))
    }
}
