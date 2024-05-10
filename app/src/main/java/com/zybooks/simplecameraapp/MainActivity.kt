package com.zybooks.simplecameraapp

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var surfaceView: SurfaceView
    private lateinit var mediaPlayer: MediaPlayer
    private var cameraPermissionGranted = false
    private lateinit var popUpAnimation: Animation
    private lateinit var captureButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        surfaceView = findViewById(R.id.surfaceView)
        captureButton = findViewById(R.id.captureButton)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.button_click_sound)
        popUpAnimation = AnimationUtils.loadAnimation(this, R.anim.pop_up_anim)
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun startCamera() {
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                // Open camera and start preview
                try {
                    // Open camera
                    val camera = android.hardware.Camera.open()

                    // Set camera preview display
                    camera.setPreviewDisplay(holder)
                    camera.startPreview()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                // Handle surface changes
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                // Release camera
            }
        })
    }

    fun captureImage(view: android.view.View) {
        // Capture image logic
        playButtonClickSound()

        // Apply animation to the capture button
        captureButton.startAnimation(popUpAnimation)

        // Show pop-up message
        showImageCapturedDialog()
    }

    private fun playButtonClickSound() {
        mediaPlayer.start()
    }

    private fun showImageCapturedDialog() {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Image Captured")
            .setMessage("Your image has been captured.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraPermissionGranted = true
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }
}