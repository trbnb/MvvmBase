package de.trbnb.mvvmbase.sample.app

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.trbnb.mvvmbase.sample.app.resource.ResourceProvider
import de.trbnb.mvvmbase.sample.app.resource.ResourceProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
    @Binds
    @Singleton
    fun resourceProvider(impl: ResourceProviderImpl): ResourceProvider
}
