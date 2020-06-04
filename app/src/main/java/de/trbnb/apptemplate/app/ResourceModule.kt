package de.trbnb.apptemplate.app

import dagger.Binds
import dagger.Module
import de.trbnb.apptemplate.resource.ResourceProvider
import de.trbnb.apptemplate.resource.ResourceProviderImpl
import javax.inject.Singleton

@Suppress("UndocumentedPublicClass")
@Module
interface ResourceModule {
    @Binds
    @Singleton
    @Suppress("UndocumentedPublicFunction")
    fun resourceProvider(resourceProviderImpl: ResourceProviderImpl): ResourceProvider
}
