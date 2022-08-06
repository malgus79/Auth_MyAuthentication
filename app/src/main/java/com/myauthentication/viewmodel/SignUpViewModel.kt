package com.myauthentication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myauthentication.core.validateFormatEmail
import com.myauthentication.core.validateFormatName
import com.myauthentication.core.validateFormatPassword
import com.myauthentication.model.data.RegisterCredentials
import com.myauthentication.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

    //Internal MutableLiveData
    private val _nameLiveData = MutableLiveData("")
    private val _emailLiveData = MutableLiveData("")
    private val _passwordLiveData = MutableLiveData("")
    private val _repeatPasswordLiveData = MutableLiveData("")

    var validName = false
    var validEmail = false
    var validPassword = false
    var passMatch = false

    private val _registerStatus = MutableLiveData<Boolean>()
    val registerStatus: LiveData<Boolean> = _registerStatus

    // Internal MutableLiveData - Enable login button
    private val _signupButtonLiveData = MutableLiveData(false)

    // External MutableLiveData - Enable login button
    val signupButtonLiveData: LiveData<Boolean> = _signupButtonLiveData

    //Updates login button liveData
    fun setSignupButtonLiveData() {
        _signupButtonLiveData.postValue(validName && validEmail && validPassword && passMatch)
    }

    //Attempts login with credentials received from fragment
    fun register(registerCredentials: RegisterCredentials) {
        val request = repository.register(registerCredentials)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = request.execute()
                if (response.isSuccessful) {
                    if (response.body()?.success == true) {
                        _registerStatus.postValue(true)
                    } else {
                        _registerStatus.postValue(false)
                    }
                } else {
                    _registerStatus.postValue(false)
                }
            } catch (e: Exception) {
                _registerStatus.postValue(false)
            }
        }
    }

    //Name field validation
    fun validateName(name: String) {
        if (name.validateFormatName()) {
            _nameLiveData.value = name
            validName = true
        } else {
            validName = false
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
    }

    //Password field validation
    fun validatePassword(pass: String) {
        _passwordLiveData.value = pass
        validPassword = pass.validateFormatPassword()
        passMatch = (_passwordLiveData.value == _repeatPasswordLiveData.value)
    }

    //RepeatPassword field validation
    fun validateRepeatPass(pass: String) {
        _repeatPasswordLiveData.value = pass
        passMatch = (_passwordLiveData.value == _repeatPasswordLiveData.value)
    }


}