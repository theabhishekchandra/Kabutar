package com.abhishek.gomailai.core.nav

import com.abhishek.gomailai.core.appsharepref.APPSharedPref
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ActivityComponent::class)
@Module
interface AppBindingsModule {

    @Binds
    fun provideNavigationBinding(impl: NavManagerImp): INavigation

    @Binds
    fun provideAPPSharedPref(impl: APPSharedPref): IAPPSharedPref

}

@InstallIn(ViewModelComponent::class)
@Module
interface ViewModelBindings {
    @Binds
    fun provideAPPSharedPref1(impl: APPSharedPref): IAPPSharedPref
}