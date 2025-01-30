package com.abhishek.gomailai.core.nav

import android.content.Context
import com.abhishek.gomailai.layout.MainActivity
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class NavManagerImp @Inject constructor(
    @ActivityContext val context: Context
) : INavigation {
    override fun getNavController() = (context as MainActivity).getNavController()
}