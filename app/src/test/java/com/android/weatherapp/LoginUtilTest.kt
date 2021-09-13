package com.android.weatherapp

import com.android.weatherapp.model.LoginUtil
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LoginUtilTest {

    @Test
    fun EmptyUsername() {
        val result = LoginUtil.validateLoginField(
            "",
            "p@ss654"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun ValidUsernamePassword() {
        val result = LoginUtil.validateLoginField(
            "Lapulapu",
            "p@ss123"
        )
        assertThat(result).isTrue()
    }

    @Test
    fun UserAlreadyExist() {
        val result = LoginUtil.validateLoginField(
            "Rizal",
            "p@ss654"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun EmptyPassword() {
        val result = LoginUtil.validateLoginField(
            "Sampaguita",
            ""
        )
        assertThat(result).isFalse()
    }

    @Test
    fun PasswordLess4Characters() {
        val result = LoginUtil.validateLoginField(
            "",
            "p@s"
        )
        assertThat(result).isFalse()
    }


}