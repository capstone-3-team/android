package com.knu.quickthink.google

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.knu.quickthink.R
import timber.log.Timber
import javax.inject.Inject

class GoogleApiContract(
    private val googleSignInClient: GoogleSignInClient
)
: ActivityResultContract<Int, Task<GoogleSignInAccount>?>() {
    override fun createIntent(context: Context, input: Int): Intent {
        val signInIntent = googleSignInClient.signInIntent
        Timber.tag("googleLogin").d("createIntent : $signInIntent")
        return signInIntent.putExtra("input",input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {
        return when (resultCode) {
            Activity.RESULT_OK -> {
                GoogleSignIn.getSignedInAccountFromIntent(intent)
            }

            else -> {
                Timber.tag("googleLogin").d("parseResult resultCode: $resultCode")
                Timber.tag("googleLogin").d("parseResult intent: $intent")
                null
            }
        }
    }
}