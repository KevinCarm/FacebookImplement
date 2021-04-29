package com.example.facebookimplement

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.facebook.share.model.*
import com.facebook.share.widget.ShareDialog


class MainActivity : AppCompatActivity() {
    private lateinit var buttonLogin: LoginButton
    private lateinit var callBackManager: CallbackManager
    private lateinit var link: Button
    private lateinit var fromGallery: Button
    private lateinit var video: Button
    private val CODE_GALLERY = 10
    private val CODE_VIDEO = 11
    private var bitMap: Bitmap? = null
    private lateinit var shareDialog: ShareDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FacebookSdk.sdkInitialize(applicationContext)

        link = findViewById(R.id.btnLink)
        fromGallery = findViewById(R.id.btnGallery)
        shareDialog = ShareDialog(this)
        buttonLogin = findViewById(R.id.login_button)
        video = findViewById(R.id.btnVideo)
        callBackManager = CallbackManager.Factory.create()

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        fromGallery.isEnabled = isLoggedIn
        link.isEnabled = isLoggedIn
        video.isEnabled = isLoggedIn

        setup()


    }

    private fun setup() {
        fromGallery.setOnClickListener {
            val accessToken = AccessToken.getCurrentAccessToken()
            val isLoggedIn = accessToken != null && !accessToken.isExpired
            if (isLoggedIn) {
                takePhotoFromGallery()
            } else {
                Toast.makeText(applicationContext, "Debes iniciar sesion", Toast.LENGTH_LONG).show()
            }
        }
        link.setOnClickListener {
            val accessToken = AccessToken.getCurrentAccessToken()
            val isLoggedIn = accessToken != null && !accessToken.isExpired
            if (isLoggedIn) {
                sendLink()
            } else {
                Toast.makeText(applicationContext, "Debes iniciar sesion", Toast.LENGTH_LONG).show()
            }
        }
        video.setOnClickListener {
            val accessToken = AccessToken.getCurrentAccessToken()
            val isLoggedIn = accessToken != null && !accessToken.isExpired
            if (isLoggedIn) {
                takeVideoFromGallery()
            } else {
                Toast.makeText(applicationContext, "Debes iniciar sesion", Toast.LENGTH_LONG).show()
            }
        }
        //266979551778840
        buttonLogin.setOnClickListener {
            val accessToken = AccessToken.getCurrentAccessToken()
            val isLoggedIn = accessToken != null && !accessToken.isExpired
            fromGallery.isEnabled = !isLoggedIn
            link.isEnabled = !isLoggedIn
            video.isEnabled = !isLoggedIn
        }

        buttonLogin.registerCallback(callBackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    Toast.makeText(applicationContext, "Login correcto", Toast.LENGTH_LONG).show()
                }

                override fun onCancel() {
                    Toast.makeText(applicationContext, "Login cancelado", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(
                        applicationContext,
                        "Hubo un error ${error?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            });
    }


    private fun sendLink() {
        Toast.makeText(applicationContext, "Cargando link, por favor espere", Toast.LENGTH_SHORT)
            .show()
        val linkContent = ShareLinkContent.Builder()
            .setQuote("Link enjambre")
            .setContentUrl(Uri.parse("https://www.youtube.com/channel/UCeWrTIZXmnw70czlsOHt0sg"))
            .build()
        shareDialog.show(linkContent)
    }

    private fun takePhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Selecciona imagen"), CODE_GALLERY)
    }

    private fun takeVideoFromGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Video"), CODE_VIDEO)
    }

    private fun sendImageToFacebook(bitmap: Bitmap?) {
        Toast.makeText(applicationContext, "Cargando imagen, por favor espere", Toast.LENGTH_SHORT)
            .show()
        val photo = SharePhoto.Builder()
            .setBitmap(bitmap)
            .build()
        val sharePhotoContent = SharePhotoContent.Builder()
            .addPhoto(photo)
            .build()
        shareDialog.show(sharePhotoContent)
    }

    private fun sendVideoToFacebook(uri: Uri?) {
        Toast.makeText(applicationContext, "Cargando video, por favor espere", Toast.LENGTH_SHORT)
            .show()
        val shareVideo = ShareVideo.Builder()
            .setLocalUrl(uri)
            .build()
        val shareVideoContent = ShareVideoContent.Builder()
            .setVideo(shareVideo)
            .build()
        shareDialog.show(shareVideoContent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callBackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CODE_GALLERY -> {
                    val uri = data?.data
                    bitMap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    sendImageToFacebook(bitMap)
                }
                CODE_VIDEO -> {
                    sendVideoToFacebook(data?.data)
                }
            }
        }
    }
}