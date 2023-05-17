package com.knu.quickthink.google

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.knu.quickthink.R
import timber.log.Timber

class GoogleApiContract : ActivityResultContract<Int, Task<GoogleSignInAccount>?>() {
    override fun createIntent(context: Context, input: Int): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.gcp_web_client_id))
            .requestEmail()
            .build()
        val intent = GoogleSignIn.getClient(context, gso)
        Timber.tag("googleLogin").d("createIntent")
        Timber.tag("googleLogin").d("createIntent : ${intent.signInIntent}")
        return intent.signInIntent.putExtra("input",input)
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