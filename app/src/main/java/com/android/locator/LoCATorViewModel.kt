package com.android.locator

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoCATorViewModel: ViewModel() {
    val TAG="VM"
    val db=DatabaseImpl()
    val auth=FirebaseAuth.getInstance()

    val cats: MutableList<Cat> = mutableListOf()
    val _witnesses: MutableList<Witness> = mutableListOf()
    val _likes:MutableList<String> = mutableListOf()

    var loginListener:LoginListener?=null

    fun setLoginListener(listener: LoginListener) {
        loginListener = listener
    }

    fun userLogIn(email:String, pwd:String){
        auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                loginListener?.onLoginSuccess(user)


            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                loginListener?.onLoginFailure(task.exception)
            }
        }
    }

}