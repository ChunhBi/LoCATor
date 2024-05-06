package com.android.locator.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.locator.LoCATorRepo
import com.android.locator.databinding.FragmentUserSettingBinding

class UserSettingFragment: Fragment() {
    private var _binding: FragmentUserSettingBinding? = null
    private val repo=LoCATorRepo.getInstance()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        binding.apply {
            settingBackBtn.setOnClickListener {
                navController.navigate(
                    UserSettingFragmentDirections.actionUserSetting2Home()
                )
            }
            settingLogOutBtn.setOnClickListener{
                repo.userLogOut()
            }
            settingWitnessBtn.setOnClickListener {
                navController.navigate(
                    UserSettingFragmentDirections.actionUserSetting2UserList(0)
                )
            }
            settingLikedBtn.setOnClickListener {
                navController.navigate(
                    UserSettingFragmentDirections.actionUserSetting2UserList(1)
                )
            }
            settingNotBtn.setOnClickListener {
                navController.navigate(
                    UserSettingFragmentDirections.actionUserSetting2UserList(2)
                )
            }
            settingPswBtn.setOnClickListener {
                navController.navigate(
                    UserSettingFragmentDirections.actionUserSetting2ChangePswd()
                )
            }
//            settingIconBtn
//            settingEmail
        }
    }
}