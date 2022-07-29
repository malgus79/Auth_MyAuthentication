package com.myauthentication.core

import java.util.regex.Pattern

private const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~\$^+=<>]).{8,20}\$"
private const val EMAIL_PATTERN = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?!-)(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$"
private val passPattern = Pattern.compile(PASSWORD_PATTERN)
private val emailPattern = Pattern.compile(EMAIL_PATTERN)

// Validate if the field is not empty, have a minimum length of 5 characters
// (3 firstname + 1 space + 1 lastname = 5 totals) and are only letters
fun String.validateFormatName() = this.length >= 5 && this.isNotEmpty()

// Validate format email (Patterns.java)
fun String.validateFormatEmail(): Boolean = emailPattern.matcher(this).matches()

//Validate at least one digit, at least one lowercase, at least one uppercase,
//at least one special character and length between 8-20
fun String.validateFormatPassword(): Boolean = passPattern.matcher(this).matches()