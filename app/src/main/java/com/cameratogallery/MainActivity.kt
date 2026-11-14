package com.cameratogallery

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

class MainActivity : Activity() {

    private val PICK_IMAGE = 1001
    private var outputUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        outputUri = intent.getParcelableExtra(
            "output_placeholder"
        )

        outputUri = intent.getParcelableExtra(
            "android.intent.extra.OUTPUT"
        )

        openGallery()
    }

    private fun openGallery() {
        val picker = Intent(Intent.ACTION_GET_CONTENT)
        picker.type = "image/*"
        picker.addCategory(Intent.CATEGORY_OPENABLE)

        startActivityForResult(picker, PICK_IMAGE)
    }

    @Deprecated("Legacy activity result")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data

                if (uri == null) {
                    finish()
                    return
                }

                try {
                    if (outputUri != null) {
                        contentResolver.openInputStream(uri)?.use { input ->
                            contentResolver.openOutputStream(outputUri!!, "w")?.use { output ->
                                input.copyTo(output)
                            }
                        }
                    }

                    val result = Intent()
                    result.data = uri

                    val options = BitmapFactory.Options()
                    options.inSampleSize = 4

                    contentResolver.openInputStream(uri)?.use {
                        val bitmap = BitmapFactory.decodeStream(it, null, options)
                        if (bitmap != null) {
                            result.putExtra("data", bitmap)
                        }
                    }

                    setResult(Activity.RESULT_OK, result)

                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Erreur : ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            finish()
        }
    }
}
