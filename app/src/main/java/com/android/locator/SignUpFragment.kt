package com.android.locator

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.locator.databinding.SignupFragLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpFragment:Fragment(), AdapterView.OnItemSelectedListener {
    private var binding: SignupFragLayoutBinding? = null
    private var signupFragmentListener:SignupFragmentListener?=null
    val repo=LoCATorRepo.getInstance()
    var selectedCampus:String=""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using the binding class
        binding = SignupFragLayoutBinding.inflate(inflater, container, false)
        // Return the root view of the inflated layout
        binding?.selectCampus?.onItemSelectedListener =this
        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {

            val campus_list = repo.getAllCampuses()
            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, campus_list)
            binding!!.selectCampus.adapter = adapter

            // on below line we are adding on item selected listener for spinner.


            binding!!.signUpButton.setOnClickListener {
                val email = binding!!.emailEditText.text.toString()
                val pwd = binding!!.passwordEditText.text.toString()
                val pwd2 = binding!!.confirmPasswordEditText.text.toString()

                if (pwd.equals(pwd2)) {
                    if(selectedCampus.equals("")){
                        Toast.makeText(
                            requireContext(),
                            "Please select a campus.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        repo?.userSignUp(email, pwd, selectedCampus)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "The passwords you typed do not match",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
            binding!!.loginButton.setOnClickListener {
                signupFragmentListener?.gotoLogin()
            }
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

    class SpinnerDetailActivity(fragment: Fragment) : AppCompatActivity(), AdapterView.OnItemSelectedListener {
        var selectedCampus:String=""
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            // TODO: change language of user in fragment
            selectedCampus = parent?.getItemAtPosition(position).toString()
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Code to execute when no item is selected (optional)
            selectedCampus=""
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        selectedCampus = parent?.getItemAtPosition(position).toString()
        Log.d("CAMPUS","selected:${selectedCampus}")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

        selectedCampus=""
    }
}