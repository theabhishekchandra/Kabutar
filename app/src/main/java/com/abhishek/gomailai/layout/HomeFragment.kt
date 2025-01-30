package com.abhishek.gomailai.layout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abhishek.gomailai.R
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    @Inject
    lateinit var navigation: INavigation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.button1.setOnClickListener {
            // Navigate to the next fragment using the NavController
            navigation.getNavController().navigate(R.id.fragmentLoadEmail)
        }
        binding.button2.setOnClickListener {
            // Navigate to the next fragment using the NavController
            navigation.getNavController().navigate(R.id.emailGenerateFragment)
        }

    }
}
