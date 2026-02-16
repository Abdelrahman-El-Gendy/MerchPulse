package com.merchpulse.core.common

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object BiometricUtils {
    
    fun canAuthenticate(context: Context): Int {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
    }

    fun authenticate(
        context: Context,
        title: String = "Biometric Login",
        subtitle: String = "Log in using your biometric credential",
        negativeButtonText: String = "Cancel",
        onResult: (Boolean, String?) -> Unit
    ) {
        val activity = context.findFragmentActivity()
        if (activity == null) {
            onResult(false, "Could not find FragmentActivity")
            return
        }

        val biometricManager = BiometricManager.from(context)
        val canAuth = biometricManager.canAuthenticate(BIOMETRIC_STRONG)
        
        if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
            val error = when (canAuth) {
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "No biometric hardware found"
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "Biometric hardware is currently unavailable"
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "No biometrics enrolled. Please set up fingerprint or face unlock in your device settings."
                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> "Security update required"
                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> "Biometric authentication is not supported"
                else -> "Biometric authentication currently unavailable"
            }
            onResult(false, error)
            return
        }

        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onResult(true, null)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        onResult(false, errString.toString())
                    } else {
                        onResult(false, null)
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // This is called per-attempt (e.g. wrong fingerprint).
                    // The biometric dialog stays open for retry.
                    // Do NOT call onResult here — let the system handle retries.
                    // If too many attempts fail, onAuthenticationError() will be called.
                }
            })

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setNegativeButtonText(negativeButtonText)

        try {
            biometricPrompt.authenticate(promptInfoBuilder.build())
        } catch (e: Exception) {
            onResult(false, "Fatal error: ${e.message}")
        }
    }

    private fun Context.findFragmentActivity(): FragmentActivity? {
        var context = this
        while (context is ContextWrapper) {
            if (context is FragmentActivity) return context
            context = context.baseContext
        }
        return if (context is FragmentActivity) context else null
    }
}

