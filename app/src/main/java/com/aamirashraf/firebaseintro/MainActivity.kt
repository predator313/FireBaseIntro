package com.aamirashraf.firebaseintro

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    private lateinit var etEmailRegister:EditText
    private lateinit var etEmailLogin:EditText
    private lateinit var etPasswordRegister:EditText
    private lateinit var etPasswordLogin:EditText
    lateinit var tvLoggedIn:TextView
    private lateinit var btnRegister:Button
    lateinit var btnLogin:Button
    lateinit var etUsername:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etPasswordRegister=findViewById(R.id.etPasswordRegister)
        etEmailRegister=findViewById(R.id.etEmailRegister)
        etEmailLogin=findViewById(R.id.etEmailLogin)
        etPasswordLogin=findViewById(R.id.etPasswordLogin)
        tvLoggedIn=findViewById(R.id.tvLoggedIn)
        btnRegister=findViewById(R.id.btnRegister)
        btnLogin=findViewById(R.id.btnLogin)
        etUsername=findViewById(R.id.etUsername)
        auth=FirebaseAuth.getInstance()
//        auth.signOut()   //we kept user sign inned
        btnRegister.setOnClickListener {
            registerUser()
        }
        btnLogin.setOnClickListener{
            loginUser()
        }
        val btnUpdateProfile=findViewById<Button>(R.id.btnUpdateProfile)
        btnUpdateProfile.setOnClickListener{
            updateProfile()
        }
    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }
    private fun updateProfile(){
        auth.currentUser?.let {user->
            val username=etUsername.text.toString()
            val photoUri=Uri.parse("android.resource://$packageName/${R.drawable.logo_black_square}")
            val profileUpdates=UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photoUri)
                .build()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdates).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                        Toast.makeText(this@MainActivity,"successfully updated profile",Toast.LENGTH_LONG).show()
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    private fun registerUser(){
        val email=etEmailRegister.text.toString()
        val password=etPasswordRegister.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email,password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
 private fun loginUser(){
        val email=etEmailLogin.text.toString()
        val password=etPasswordLogin.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email,password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun checkLoggedInState() {
        val user=auth.currentUser
        if(user==null){
            tvLoggedIn.text="You are not logged inn..."
        }else{
            tvLoggedIn.text="You are logged inn successfully"
            etUsername.setText(user.displayName)
            val ivProfilePicture=findViewById<ImageView>(R.id.ivProfilePicture)
            ivProfilePicture.setImageURI(user.photoUrl)
        }
    }
}