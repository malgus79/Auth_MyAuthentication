package com.myauthentication.viewmodel

import android.app.Activity
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.myauthentication.R
import com.myauthentication.core.ApiStatus
import com.myauthentication.core.MyAuthenticationApp
import com.myauthentication.core.validateFormatEmail
import com.myauthentication.core.validateFormatPassword
import com.myauthentication.model.data.LoginCredentials
import com.myauthentication.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

    //Internal MutableLiveData
    private val _emailLiveData = MutableLiveData("")
    private val _passwordLiveData = MutableLiveData("")

    var validEmail = false
    var validPassword = false

    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

    // Internal/External MutableLiveData - Enable login button
    private val _loginButtonLiveData = MutableLiveData(false)
    val loginButtonLiveData: LiveData<Boolean> = _loginButtonLiveData

    private val _isLoading = MutableLiveData(false)
    private val _hasErrors = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    val hasErrors: LiveData<Boolean> = _hasErrors

    // Internal/External MutableLiveData - Api state
    private val _postContactStatus = MutableLiveData<ApiStatus>()
    val postContactStatus: LiveData<ApiStatus> = _postContactStatus

    // Internal MutableLiveData - Login state
    private val _postLoginStatus = MutableLiveData<LoginStatus>()
    val postLoginStatus: LiveData<LoginStatus> = _postLoginStatus

    enum class LoginStatus { SUCCESS, ERROR200, ERROR }


    //Updates login button liveData
    private fun setLoginButtonLiveData() {
        _loginButtonLiveData.postValue(validEmail && validPassword)
    }

    //Attempts login with credentials received from fragment
    fun logIn(loginCredentials: LoginCredentials) {

        //_postContactStatus.value = ApiStatus.LOADING
        val request = repository.logIn(loginCredentials)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = request.execute()
                if (response.isSuccessful) {
                    if (response.body()?.success == true) {
                        MyAuthenticationApp.prefs.saveToken(response.body()!!.data.token) //Saves token on shared preferences
                        //_postLoginStatus.value = LoginStatus.SUCCESS
                        _loginStatus.postValue(true)
                    } else {
                        _loginStatus.postValue(false)
                        //_postLoginStatus.postValue(LoginStatus.ERROR200)
                        // _postContactStatus.value = ApiStatus.ERROR
                        //TODO IMPL ERROR WITH CODE 200
                    }
                } else {
                    _loginStatus.postValue(false)
                    //_postLoginStatus.postValue(LoginStatus.ERROR)
                    //_postContactStatus.value = ApiStatus.ERROR
                    //TODO IMPL ERROR WITH CODE != 200
                }
            } catch (e: Exception) {
                _loginStatus.postValue(false)
            }
        }
    }

    //Email field validation
    fun validateEmail(email: String) {
        if (email.validateFormatEmail()) {
            _emailLiveData.value = email
            validEmail = true
        } else {
            validEmail = false
        }
        setLoginButtonLiveData()
    }

    //Password field validation
    fun validatePassword(pass: String) {
        if (pass.validateFormatPassword()) {
            _passwordLiveData.value = pass
            validPassword = true
        } else {
            validPassword = false
        }
        setLoginButtonLiveData()
    }

    fun singInGoogle(activity: Activity) {
        _isLoading.postValue(true)
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.services_client_id))
            .requestEmail()
            .build()

        // Getting the client
        val client = GoogleSignIn.getClient(activity, gso)
        // Initiate login intent
        val signInIntent = client.signInIntent
        activity.startActivityForResult(signInIntent, 200)
    }
    fun endUpGoogleLogIn(accountTask: Task<GoogleSignInAccount>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val account = accountTask.getResult(ApiException::class.java)

                // Signed in successfully, show authenticated UI.
                account.idToken.let { token ->
                    val auth = FirebaseAuth.getInstance()
                    val credentials = GoogleAuthProvider.getCredential(token, null)

                    auth.signInWithCredential(credentials)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val user = auth.currentUser
                                Log.d("ACA", "Ingreso ${user?.displayName}")

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.d("ACA", "Failed ${task.exception?.message}")
                                _hasErrors.postValue(true)
                            }
                            _isLoading.postValue(false)
                        }
                        .addOnCanceledListener {
                            Log.d("ACA", "Error")
                        }
                }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(ContentValues.TAG, "Google sign in failed", e)
                _hasErrors.postValue(true)
                _isLoading.postValue(false)
            }
        }
    }

}