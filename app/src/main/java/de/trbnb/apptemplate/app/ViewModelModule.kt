package de.trbnb.apptemplate.app

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(includes = [AssistedInject_ViewModelModule::class])
internal interface ViewModelModule
