package com.android.locator

import AccessPermissionHelper
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.android.locator.home.BitmapHelper
import com.android.locator.home.Home
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit


//这个接口用于把Mainactivity注册给LoCATorRepo,
//因为repo里的一些功能需要把结果或信息返回给Mainactivity
interface MainActivityListener{
    fun onLoginSuccess()
    fun onLoginFailure(exception: Exception?)
    fun makeToast(m:String)

    //fun startWorkManager()

    fun logOut()


    fun restartWorkManager()

}


//这两个接口把MainActivity注册给Login fragment 和Sign up fragment
//目前用于login和signup的切换
//如果改用Navigation graph的话就不需要了
interface LoginFragmentListener{
    //fun userLogin(email: String, pwd:String)
    fun gotoSignup()
    fun gotoResetPwd()
}
interface SignupFragmentListener{
    //fun userSignup(email: String, pwd:String)
    fun gotoLogin()
}

interface ResetPwdFragmentListener{
    //fun userSignup(email: String, pwd:String)
    fun goBackToLogin()
}


class MainActivity : AppCompatActivity(),MainActivityListener,LoginFragmentListener,SignupFragmentListener,ResetPwdFragmentListener {
    val TAG="MAIN"
    private val repo: LoCATorRepo=LoCATorRepo.getInstance()
    lateinit var workId: UUID

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
    //val auth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BitmapHelper.set_Context(this)
        AccessPermissionHelper.setActivity(this)
        //LocationHelper.set_Context(this)

        setContentView(R.layout.main_layout)

        repo.setLoginListener(this)
        if(repo.hasUser()){
            CoroutineScope(Dispatchers.Main).launch {
                repo.initAllDbData()
                onLoginSuccess()
                return@launch
            }
            return

        }

        val loginFragment = LoginFragment()
        loginFragment.setLoginFragmentListener(this)
        setFragmentToContainer(loginFragment)


        /*
        supportFragmentManager.addOnBackStackChangedListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (fragment is Home) {
                fragment.loadData()
                fragment.drawLatestMarkers()
                Log.d("RELOAD","Home reload called.")
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


    override fun onLoginSuccess() {
        val user=repo.getUser()
        showInfo(user)
        Toast.makeText(this,"Welcome! ${user?.email}",Toast.LENGTH_SHORT).show()
        val homeFragment=Home()
        repo.registerListener(homeFragment)
        setFragmentToContainer(homeFragment)
        Log.d("MAIN","set to homeFragment")
        startWorkManager()
    }

    override fun onLoginFailure(exception: Exception?) {
        val errorMessage = exception?.message ?: "Unknown error"
        Log.d(TAG, "Login Error: $errorMessage")
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun makeToast(m: String) {
        Toast.makeText(this,m,Toast.LENGTH_SHORT).show()
    }

    override fun logOut() {
        val loginFragment = LoginFragment()
        loginFragment.setLoginFragmentListener(this)
        setFragmentToContainer(loginFragment)
        cancelWorkManager()
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

    override fun gotoResetPwd() {
        val resetPwdFragment = resetPwdFragment()
        resetPwdFragment.setResetPwdListener(this)
        setFragmentToContainer(resetPwdFragment)
    }

    private fun startWorkManager(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this as Activity,  // Pass the activity reference
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                123
            )
            Log.d("NOTIF", "Missing permission.")
        }

        val likedCats=repo.get_Likes()
        val allCats=repo.get_Cats()
        Log.d(TAG,"Likes: ${likedCats}")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = Data.Builder()
            .putStringArray("catIds", likedCats.toTypedArray())
            //.putString("uid", repo.getUser()?.uid)

            .build()

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(10,TimeUnit.SECONDS)
            .setInputData(inputData)
            .setConstraints(constraints)
            .build()

        workId = workRequest.id

        WorkManager.getInstance(this).enqueue(workRequest)
    }

    override fun restartWorkManager() {
        WorkManager.getInstance(this).cancelWorkById(workId)

        startWorkManager()
        Log.d("NOTIF","work manager restarted")
    }

    private fun cancelWorkManager(){
        WorkManager.getInstance(this).cancelAllWork()
    }

    override fun goBackToLogin() {
        val loginFragment = LoginFragment()
        loginFragment.setLoginFragmentListener(this)
        setFragmentToContainer(loginFragment)
    }
}