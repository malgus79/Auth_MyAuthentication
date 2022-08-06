package com.myauthentication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}