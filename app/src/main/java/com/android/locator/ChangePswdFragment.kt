package com.android.locator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.locator.databinding.ChangePswdLayoutBinding
import com.android.locator.databinding.SignupFragLayoutBinding

class ChangePswdFragment:Fragment() {
    private var binding: ChangePswdLayoutBinding? = null
    private var signupFragmentListener:SignupFragmentListener?=null
    val repo=LoCATorRepo.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using the binding class
        binding = ChangePswdLayoutBinding.inflate(inflater, container, false)
        // Return the root view of the inflated layout
        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.changeButton.setOnClickListener {
            val pwd=binding!!.passwordEditText.text.toString()
            val pwd2=binding!!.confirmPasswordEditText.text.toString()
            val oldPwd=binding!!.oldPswdText.text.toString()
            if(pwd.equals(pwd2)){
                repo.changePwd(oldPwd,pwd)
            }
            else{
                Toast.makeText(requireContext(),"The passwords you typed do not match",Toast.LENGTH_SHORT).show()
            }
        }
        binding!!.changePswdBackBtn.setOnClickListener {
            findNavController().navigate(
                ChangePswdFragmentDirections.actionChangePswd2UserSetting()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding when the view is destroyed
        binding = null
    }
}