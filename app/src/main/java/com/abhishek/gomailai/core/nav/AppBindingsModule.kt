package com.abhishek.gomailai.core.nav

import com.abhishek.gomailai.core.appsharepref.APPSharedPref
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(ActivityComponent::class)
@Module
interface AppBindingsModule {

    @Binds
    fun provideNavigationBinding(impl: NavManagerImp): INavigation
}

@InstallIn(SingletonComponent::class)
@Module
interface SharedPreferencesModule {

    @Binds
    @Singleton
    fun provideAPPSharedPref(impl: APPSharedPref): IAPPSharedPref
}
