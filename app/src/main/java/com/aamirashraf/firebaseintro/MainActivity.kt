package com.aamirashraf.firebaseintro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
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
        auth=FirebaseAuth.getInstance()
        auth.signOut()
        btnRegister.setOnClickListener {
            registerUser()
        }
        btnLogin.setOnClickListener{
            loginUser()
        }
    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
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
        if(auth.currentUser==null){
            tvLoggedIn.text="You are not logged inn..."
        }else{
            tvLoggedIn.text="You are logged inn successfully"
        }
    }
}