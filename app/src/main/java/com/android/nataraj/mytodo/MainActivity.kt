package com.android.nataraj.mytodo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.nataraj.mytodo.utils.AttentionUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private val RC_SIGN_IN: Int = 1

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private lateinit var prefs: SharedPreferences

    private var actionBar: ActionBar? = null

    private val attentionUtil: AttentionUtil = AttentionUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pb_login.visibility = View.VISIBLE

        prefs = getSharedPreferences(getString(R.string.pref_file_name), Context.MODE_PRIVATE)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // to check if user already logged in
        if (checkIfUserLogin()) {
            signInUsingGoogle()
        } else {
            pb_login.visibility = View.GONE
            btn_google_login.visibility = View.VISIBLE
        }

        initScreen()
    }

    private fun initScreen() {
        setSupportActionBar(main_toolbar)
        actionBar = supportActionBar

        btn_google_login.setOnClickListener {
            signInUsingGoogle()
        }

        btn_sign_out.setOnClickListener {
            pb_login.visibility = View.VISIBLE
            mGoogleSignInClient.signOut().addOnCompleteListener {
                doSignOut()
            }
        }

    }

    private fun checkIfUserLogin(): Boolean {
        return prefs.getBoolean(getString(R.string.is_logged_in), false)
    }

    private fun signInUsingGoogle() {
        pb_login.visibility = View.VISIBLE
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            setLoggedIn(true)
            handleRequest(task)
        }
    }

    private fun handleRequest(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            updateUi(account)
        } catch (e: ApiException) {
            attentionUtil.alertMessage(this, getString(R.string.failure_sign_in), Toast.LENGTH_SHORT)
        }
    }

    private fun updateUi(account: GoogleSignInAccount) {
        Log.d(TAG, account.toJson())
        pb_login.visibility = View.GONE
        tv_username.text = account.displayName
        tv_username.visibility = View.VISIBLE
        btn_sign_out.visibility = View.VISIBLE
        btn_google_login.visibility = View.GONE
        loadAvatar(account.photoUrl)
    }

    private fun loadAvatar(imgUrl: Uri?) {
        iv_avatar.visibility = View.VISIBLE
        Glide.with(this)
                .load(imgUrl)
                .transition(withCrossFade())
                .apply(circleCropTransform())
                .into(iv_avatar)
    }

    private fun doSignOut() {
        setLoggedIn(false)
        pb_login.visibility = View.GONE
        tv_username.text = getString(R.string.nothing)
        tv_username.visibility = View.GONE
        iv_avatar.visibility = View.GONE
        btn_sign_out.visibility = View.GONE
        btn_google_login.visibility = View.VISIBLE
        attentionUtil.alertMessage(this, "Sign out successful", Toast.LENGTH_LONG)
    }

    private fun setLoggedIn(isLogged: Boolean) {
        var editor: Editor = prefs.edit()
        editor.putBoolean(getString(R.string.is_logged_in), isLogged)
        editor.apply()
    }
}
