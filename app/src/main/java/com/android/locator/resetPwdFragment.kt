package com.android.locator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.locator.databinding.ForgetPwdLayoutBinding

class resetPwdFragment:Fragment() {

    private var binding: ForgetPwdLayoutBinding? = null
    private var resetPwdFragmentListener:ResetPwdFragmentListener?=null
    val repo=LoCATorRepo.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using the binding class
        binding = ForgetPwdLayoutBinding.inflate(inflater, container, false)


        // Return the root view of the inflated layout
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.sendRequest.setOnClickListener {
            var email=binding!!.emailEditText.text.toString()


            repo?.resetRequest(email)
        }
        binding!!.goBack.setOnClickListener {
            resetPwdFragmentListener?.goBackToLogin()
        }
    }

    fun setResetPwdListener(listener:ResetPwdFragmentListener?){
        resetPwdFragmentListener=listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding when the view is destroyed
        binding = null
    }
}