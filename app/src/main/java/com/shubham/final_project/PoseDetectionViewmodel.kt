package com.shubham.final_project

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.math.max

class PoseDetectionViewmodel(
    private val poseLandmarker: PoseLandmarker,
    private val executor: Executor,
    private val context: Context,
) :ViewModel() {
    private val _resultBundleState = MutableLiveData<PoseDetector.ResultBundle?>()
    val resultBundleState: LiveData<PoseDetector.ResultBundle?> get() = _resultBundleState

    @OptIn(ExperimentalCoroutinesApi::class)
    fun initializeCamera(
//        context: Context,
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO.limitedParallelism(4)) {

                val cameraProvider = ProcessCameraProvider.getInstance(context)
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build()
                    .also {
                        it.setAnalyzer(executor) { image ->
                            val mpImage = mpimage(image,isFrontCamera = true)
                            val frameTime = SystemClock.uptimeMillis()
                            poseLandmarker.detectAsync(mpImage, frameTime)
                            image.close()
                        }
                    }

                cameraProvider.addListener(
                    {
                        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                        val cameraProvider = cameraProvider.get()
//                        val imageAnalyzer = setupImageAnalyzer(poseLandmarker)
                        val preview = Preview.Builder().build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            imageAnalyzer,
                        )
                        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                    },
                    ContextCompat.getMainExecutor(context)
                )
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
}