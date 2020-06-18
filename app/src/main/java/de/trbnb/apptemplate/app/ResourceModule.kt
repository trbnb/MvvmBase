package de.trbnb.apptemplate.app

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.trbnb.apptemplate.resource.ResourceProvider
import de.trbnb.apptemplate.resource.ResourceProviderImpl
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
interface ResourceModule {
    @Binds
    @Singleton
    fun resourceProvider(resourceProviderImpl: ResourceProviderImpl): ResourceProvider
}
