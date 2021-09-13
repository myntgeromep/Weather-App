package com.android.weatherapp.view

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.weatherapp.R
import com.android.weatherapp.model.Login.Companion.isLogged
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var cancellationSignal: CancellationSignal? = null
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    toastNotify("$errString")
                    startActivity(Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    isLogged = true
                    goToWeatherActivity()
                }
            }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.hide();

        // Biometric Auth
        checkBiometricSupport()
        btn_authenticate.setOnClickListener {
            val biometricPrompt = BiometricPrompt.Builder(this)
                .setTitle("Authentication")
                .setNegativeButton(
                    "Cancel",
                    this.mainExecutor,
                    DialogInterface.OnClickListener { dialog, which ->
                        toastNotify("Authentication cancelled")
                    }).build()
            biometricPrompt.authenticate(
                getCancellationSignal(),
                mainExecutor,
                authenticationCallback
            )
        }

        // Check if field is empty
        btn_login.setOnClickListener {
            val username: String = et_username.text.toString()
            val password: String = et_password.text.toString()
            // Check if fields are empty
            if (username.trim().length > 0 && password.trim().length > 0) {
                isLogged = true
                goToWeatherActivity()
            } else {
                Toast.makeText(this, "Enter a username and password", Toast.LENGTH_SHORT).show()
            }
        }

        tv_guest.setOnClickListener {
            isLogged = false
            goToWeatherActivity() }

    }

    private fun goToWeatherActivity() {
        startActivity(Intent(this, WeatherActivity::class.java))
    }

    private fun toastNotify(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            toastNotify("Authentication was cancelled")
        }
        return cancellationSignal as CancellationSignal
    }

    private fun checkBiometricSupport(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isKeyguardSecure) {
            toastNotify("Enable fingerprint authentication in the settings")
            return false
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            toastNotify("Enable fingerprint authentication permission")
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true

    }

}