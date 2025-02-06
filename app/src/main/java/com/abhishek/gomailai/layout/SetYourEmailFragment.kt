package com.abhishek.gomailai.layout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abhishek.gomailai.R
import com.abhishek.gomailai.core.appsharepref.APPSharedPref
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.databinding.FragmentSetYourEmailBinding
import javax.inject.Inject


class SetYourEmailFragment : Fragment() {

    private lateinit var binding: FragmentSetYourEmailBinding

    @Inject
    lateinit var navigation : INavigation

    @Inject
    lateinit var appSharedPref: IAPPSharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         binding = FragmentSetYourEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.textView.text = "Set Your Email"
    }

}