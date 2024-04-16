package com.android.locator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.locator.databinding.LoginFragLayoutBinding

class LoginFragment:Fragment() {

    private var binding: LoginFragLayoutBinding? = null
    private var loginFragmentListener:LoginFragmentListener?=null
    val repo=LoCATorRepo.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using the binding class
        binding = LoginFragLayoutBinding.inflate(inflater, container, false)


        // Return the root view of the inflated layout
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.loginButton.setOnClickListener {
            val email=binding!!.emailEditText.text.toString()
            val pwd=binding!!.passwordEditText.text.toString()

            repo?.userLogIn(email,pwd)
        }
        binding!!.signupButton.setOnClickListener {
            loginFragmentListener?.gotoSignup()
        }
    }

    fun setLoginFragmentListener(listener:LoginFragmentListener?){
        loginFragmentListener=listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding when the view is destroyed
        binding = null
    }
}