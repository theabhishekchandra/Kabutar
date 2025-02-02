package com.abhishek.gomailai.layout

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.abhishek.gomailai.R
import com.abhishek.gomailai.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val emailGenerateViewModel: EmailGenerateViewModel by viewModels()
    private val emailViewModel: EmailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.navigate(R.id.homeFragment)

    }
    fun getNavController(): NavController {
        return this.navController
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.homeFragment) {
            // If at home, exit the app
            finish()
        } else {
            if (!navController.popBackStack()) {
                super.onBackPressed()
            }
        }
    }


    /*override fun onBackPressed() {
        var actionFound = false
        try {
            actionFound = (getForegroundFragment() as IOnBackPressedOverride).onBackPressed()
        } catch (e: Exception) {
            // Ignore
        }
//        try {
//            actionFound = (getSubFragment() as IOnBackPressedOverride).onBackPressed()
//        } catch (e: Exception) {
//            // Ignore
//        }
        if (!actionFound)
            super.onBackPressed()
    }*/

//    private fun getSubFragment(): Fragment {
//        val navHostFragment: Fragment? =
//            supportFragmentManager.findFragmentById(R.id.fragment_container)
//        return navHostFragment!!.childFragmentManager.fragments[navHostFragment.childFragmentManager.fragments.size - 1]
//    }

    private fun getForegroundFragment(): Fragment? {
        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        return navHostFragment!!.childFragmentManager.fragments[navHostFragment.childFragmentManager.fragments.size - 1]
    }
}

