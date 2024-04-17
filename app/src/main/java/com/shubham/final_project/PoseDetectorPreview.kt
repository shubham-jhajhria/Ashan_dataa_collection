package com.shubham.final_project

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Build
import android.os.SystemClock
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor
import kotlin.math.max

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedMutableState")
@Composable
fun PoseDetectionPreview(
    poseLandmarker: PoseLandmarker,
    executor: Executor,
    resultBundleState: MutableState<PoseDetector.ResultBundle?>,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = {
            val view = PreviewView(it)
            view.scaleType=PreviewView.ScaleType.FILL_END
            view

        },
        update = { previewView ->
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

            val cameraProvider = ProcessCameraProvider.getInstance(context)
            cameraProvider.addListener(
                {
                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                    val cameraProvider = cameraProvider.get()
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        imageAnalyzer
                    )
                    val preview = Preview.Builder().build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)

                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                },
                ContextCompat.getMainExecutor(context)
            )
        }
    )

//    CreateHeader()
    DrawPosesOnPreview(modifier,resultBundleState)
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DrawPosesOnPreview(modifier: Modifier,resultBundleState: MutableState<PoseDetector.ResultBundle?>) {
    val resultBundle = resultBundleState.value
    Ani()
    if (resultBundle != null && basicCountdownTimer(10)==0) {
        if(basicCountdownTimer(time = GlobalValues.time.toInt())>0){
            writeCsv(resultBundle)

        Canvas(modifier = modifier) { 
            val scale = max(size.width * 1f / resultBundle.inputImageWidth, size.height * 1f / resultBundle.inputImageHeight)
            resultBundle.results.forEachIndexed { resultIndex, result ->
                result.landmarks().forEachIndexed { landmarkIndex, poseLandmarks ->
                    poseLandmarks.forEach { landmark ->
                        drawCircle(
                            color = Color.Yellow,
                            radius = 10F,
                            center = Offset(landmark.x() * scale*resultBundle.inputImageWidth-300f, landmark.y() * scale*resultBundle.inputImageHeight)
                        )

                    }
                }
            }
        }}

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