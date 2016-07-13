package de.trbnb.kotlindaggerdatabindingtemplate

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetModule() {

    @Provides
    @Singleton
    fun providesAPI(): API = APIImpl()

}