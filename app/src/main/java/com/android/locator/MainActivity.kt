package com.android.locator

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch


interface LoginListener{
    fun onLoginSuccess(user: FirebaseUser?)
    fun onLoginFailure(exception: Exception?)
}

interface LoginFragmentListener{
    fun userLogin(email:String, pwd:String)
}
class MainActivity : AppCompatActivity(),LoginListener,LoginFragmentListener {
    val TAG="MAIN"
    private val viewModel: LoCATorViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        setContentView(R.layout.main_layout)




        /*
        auth=FirebaseAuth.getInstance()

        viewModel.setLoginListener(this)

        auth.signInWithEmailAndPassword("ydhe@bu.edu", "12345678").addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser

                showInfo(user)


            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()

            }
        }

        lifecycleScope.launch {
            try{
                db.fetchCatsFromFirestore()
                db.fetchWitsFromFirestore()
                db.fetchLikesFromFirestore(auth.currentUser)
                val cats=db.getAllCats()
                val wits=db.getAllWits()
                val likes=db.getLikes()

                cats.forEach { cat->
                    Log.d(TAG,cat.toString())
                }
                wits.forEach { wit->
                    Log.d(TAG,wit.toString())
                }
                likes.forEach { like->
                    Log.d(TAG,like)
                }
            }catch (e:Exception){

            }

        }

         */




    }
    private fun showInfo(user: FirebaseUser?) {
        user?.let {


            // UID specific to the provider
            val uid = user.uid
            val email=user.email
            Log.d(TAG,"email:${email}, uid:${uid}.")


        }
        if(user==null){
            Log.d(TAG,"User is null.")
        }
    }
    fun openLocation(latitude: Double, longitude: Double) {
        val geoUri = Uri.parse("geo:$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
        mapIntent.resolveActivity(packageManager)?.let {
            startActivity(mapIntent)
        }
    }

    override fun onLoginSuccess(user:FirebaseUser?) {
        showInfo(user)
        Toast.makeText(this,"Welcome! ${user?.email}",Toast.LENGTH_SHORT).show()
        //TODO: go to homepage fragment
    }

    override fun onLoginFailure(exception: Exception?) {
        Log.d(TAG, "Login Error: ${exception.toString()}")
        Toast.makeText(this,exception.toString(),Toast.LENGTH_SHORT).show()
    }

    override fun userLogin(email: String, pwd: String) {
        viewModel.userLogIn(email,pwd)
    }
}