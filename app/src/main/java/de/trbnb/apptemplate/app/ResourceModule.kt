package de.trbnb.apptemplate.app

import dagger.Binds
import dagger.Module
import de.trbnb.apptemplate.resource.ResourceProvider
import de.trbnb.apptemplate.resource.ResourceProviderImpl
import javax.inject.Singleton

@Module
interface ResourceModule {
    @Binds
    @Singleton
    fun resourceProvider(resourceProviderImpl: ResourceProviderImpl): ResourceProvider
}
