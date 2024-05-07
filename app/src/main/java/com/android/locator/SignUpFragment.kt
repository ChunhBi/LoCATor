package com.android.locator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.locator.databinding.SignupFragLayoutBinding

class SignUpFragment:Fragment() {
    private var binding: SignupFragLayoutBinding? = null
    private var signupFragmentListener:SignupFragmentListener?=null
    val repo=LoCATorRepo.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using the binding class
        binding = SignupFragLayoutBinding.inflate(inflater, container, false)
        // Return the root view of the inflated layout
        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.signUpButton.setOnClickListener {
            val email=binding!!.emailEditText.text.toString()
            val pwd=binding!!.passwordEditText.text.toString()
            val pwd2=binding!!.confirmPasswordEditText.text.toString()
            if(pwd.equals(pwd2)){
                repo?.userSignUp(email,pwd,"BU")
            }
            else{
                Toast.makeText(requireContext(),"The passwords you typed do not match",Toast.LENGTH_SHORT).show()
            }


        }
        binding!!.loginButton.setOnClickListener {
            signupFragmentListener?.gotoLogin()
        }
    }

    fun setSignupFragmentListener(listener:SignupFragmentListener?){
        signupFragmentListener=listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding when the view is destroyed
        binding = null
    }
}