package com.shubham.final_project

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.shubham.final_project.components.Ann
import com.shubham.final_project.components.basicCountdownTimer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.math.max

class PoseDetectionViewmodel(
) :ViewModel() {
    private val _resultBundleState = MutableLiveData<PoseDetector.ResultBundle?>()
    val resultBundleState: LiveData<PoseDetector.ResultBundle?> get() = _resultBundleState
    private val _cameraInitialized = MutableStateFlow(false)
    //To initialize posedetector
    private val _poseDetector = MutableLiveData<PoseDetector>()
    val poseDetector: LiveData<PoseDetector> get() = _poseDetector
    fun initPoseDetector(context: Context) {
        _poseDetector.value = PoseDetector(context = context as MainActivity)
    }
    // for poseresult processing
    fun cameraInitialized()= _cameraInitialized.asStateFlow()
    @OptIn(ExperimentalCoroutinesApi::class)
    fun initializeCamera(context: Context, previewView: PreviewView, lifecycleOwner: LifecycleOwner, poseLandmarker: PoseLandmarker) {

        viewModelScope.launch {
            if (lifecycleOwner.lifecycle.currentState != Lifecycle.State.DESTROYED) {
                withContext(Dispatchers.IO.limitedParallelism(4)) {
                    val cameraProvider = ProcessCameraProvider.getInstance(context)

                    cameraProvider.addListener(
                        {
                            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                            val cameraProvider1 = cameraProvider.get()
                            val imageAnalyzer = setupImageAnalyzer(poseLandmarker)
                            val preview = Preview.Builder().build()
                            preview.setSurfaceProvider(previewView.surfaceProvider)
                            _cameraInitialized.update { true }
                            cameraProvider1.unbindAll()
                            cameraProvider1.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                imageAnalyzer, preview
                            )

                        },
                        ContextCompat.getMainExecutor(context)
                    )
                }
            } else {
                // Handle the case where the LifecycleOwner is destroyed
                Log.w("PoseDetectionViewModel", "LifecycleOwner is destroyed, cannot initialize camera")
            }
        }
    }

    private fun setupImageAnalyzer( poseLandmarker: PoseLandmarker ): ImageAnalysis {
        lateinit var executor: Executor
        executor = Executors.newFixedThreadPool(4)

        return ImageAnalysis.Builder()

            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .also {
                it.setAnalyzer(executor) { image ->
                    processImageAnalysis(poseLandmarker, image)
                }
            }
    }
    private fun mpimage(
        imageProxy: ImageProxy,
        isFrontCamera: Boolean,
    ): MPImage? {
        // Copy out RGB bits from the frame to a bitmap buffer
        val bitmapBuffer =
            Bitmap.createBitmap(
                imageProxy.width,
                imageProxy.height,
                Bitmap.Config.ARGB_8888
            )

        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
        imageProxy.close()

        val matrix = Matrix().apply {
            // Rotate the frame received from the camera to be in the same direction as it'll be shown
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            if (isFrontCamera) {
                postScale(
                    -1f,
                    1f,
                    imageProxy.width.toFloat(),
                    imageProxy.height.toFloat()
                )
            }
        }
        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
            matrix, true
        )

        // Convert the input Bitmap object to an MPImage object to run inference
        val mpImage = BitmapImageBuilder(rotatedBitmap).build()
        return  mpImage
    }
    private fun processImageAnalysis(poseLandmarker: PoseLandmarker, image: ImageProxy) {
        viewModelScope.launch {
            val mpImage = mpimage(image, isFrontCamera = true)
            val frameTime = SystemClock.uptimeMillis()
            poseLandmarker.detectAsync(mpImage, frameTime)

        }
    }
    private val _poseResult = MutableLiveData<PoseDetector.ResultBundle>()
    val poseResult: LiveData<PoseDetector.ResultBundle> get() = _poseResult
    fun processPoseDetection() {
        viewModelScope.launch {
            try {
                val resultBundle = poseDetector.value?.getPoseResult()
                if (resultBundle != null) {
                    _poseResult.value=resultBundle.value
                }
            } catch (e: Exception) {
                // Handle any exceptions that might occur during pose detection or inference
                Log.e("PoseDetection", "Error processing pose detection: ${e.message}", e)
            }
        }
    }
}