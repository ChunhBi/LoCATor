package com.android.locator

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.locator.home.HomeFragment
import com.google.firebase.auth.FirebaseUser



//这个接口用于把Mainactivity注册给LoCATorRepo,
//因为repo里的一些功能需要把结果或信息返回给Mainactivity
interface MainActivityListener{
    fun onLoginSuccess(user: FirebaseUser?)
    fun onLoginFailure(exception: Exception?)
    fun makeToast(m:String)
}


//这两个接口把MainActivity注册给Login fragment 和Sign up fragment
//目前用于login和signup的切换
//如果改用Navigation graph的话就不需要了
interface LoginFragmentListener{
    //fun userLogin(email: String, pwd:String)
    fun gotoSignup()
}
interface SignupFragmentListener{
    //fun userSignup(email: String, pwd:String)
    fun gotoLogin()
}


class MainActivity : AppCompatActivity(),MainActivityListener,LoginFragmentListener,SignupFragmentListener {
    val TAG="MAIN"
    private val repo: LoCATorRepo=LoCATorRepo.getInstance()
    //val auth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_layout)

        repo.setLoginListener(this)

        val loginFragment = LoginFragment()
        loginFragment.setLoginFragmentListener(this)
        setFragmentToContainer(loginFragment)




        /*
                //auth=FirebaseAuth.getInstance()

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


    override fun onLoginSuccess(user:FirebaseUser?) {
        showInfo(user)
        Toast.makeText(this,"Welcome! ${user?.email}",Toast.LENGTH_SHORT).show()
        val homeFragment=HomeFragment()
        setFragmentToContainer(homeFragment)
    }

    override fun onLoginFailure(exception: Exception?) {
        val errorMessage = exception?.message ?: "Unknown error"
        Log.d(TAG, "Login Error: $errorMessage")
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun makeToast(m: String) {
        Toast.makeText(this,m,Toast.LENGTH_SHORT).show()
    }

//    override fun userLogin(email: String, pwd: String) {
//        if(email==null||pwd==null||email.equals("")||pwd.equals("")){
//            Toast.makeText(this,"Email or password cannot be empty!",Toast.LENGTH_SHORT).show()
//            return
//        }
//        viewModel.userLogIn(email,pwd)
//    }



    private fun setFragmentToContainer(fragment: Fragment) {
        // Get the FragmentManager
        val fragmentManager: FragmentManager = supportFragmentManager

        // Begin a transaction
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Replace the fragment container with the desired fragment
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)

        // Commit the transaction
        fragmentTransaction.commit()
    }


//    override fun userSignup(email: String, pwd: String) {
//        if(email==null||pwd==null||email.equals("")||pwd.equals("")){
//            Toast.makeText(this,"Email or password cannot be empty!",Toast.LENGTH_SHORT).show()
//            return
//        }
//        viewModel.userSignUp(email,pwd)
//    }

    override fun gotoLogin() {
        val loginFragment = LoginFragment()
        loginFragment.setLoginFragmentListener(this)
        setFragmentToContainer(loginFragment)
    }

    override fun gotoSignup() {
        val signUpFragment = SignUpFragment()
        signUpFragment.setSignupFragmentListener(this)
        setFragmentToContainer(signUpFragment)

    }
}