package com.shubham.final_project


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shubham.final_project.components.Ann
import com.shubham.final_project.components.basicCountdownTimer
import kotlin.math.max

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PoseDetectionPreview(
    lifecycleOwner: LifecycleOwner,
    poseDetectionViewModel: PoseDetectionViewmodel = viewModel(),
    modifier: Modifier
){

    AndroidView(
        factory = {
            val previewView = PreviewView(it)
            previewView.scaleType=PreviewView.ScaleType.FILL_END
            previewView

        },
        modifier = modifier,
        update = { previewView ->
            poseDetectionViewModel.initializeCamera(previewView, lifecycleOwner)

        }
    )
    DrawPosesOnPreview(modifier, poseDetectionViewModel)
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DrawPosesOnPreview(modifier: Modifier,viewModel: PoseDetectionViewmodel) {
    val resultBundle = viewModel.resultBundleState.value
    val csvViewModel: CsvViewModel = viewModel()
    Ann()
    if (resultBundle != null && basicCountdownTimer(10)==0) {
        if(basicCountdownTimer(time = GlobalValues.time.toInt())>0){
            csvViewModel.writeCsv(resultBundle)

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
            }
        }
    }
}
//@RequiresApi(Build.VERSION_CODES.O)
//@SuppressLint("UnrememberedMutableState")
//@Composable
//fun PoseDetectionPreview(
//    poseLandmarker: PoseLandmarker,
//    executor: Executor,
//    resultBundleState: MutableState<PoseDetector.ResultBundle?>,
//    modifier: Modifier = Modifier
//) {
//
//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    AndroidView(
//        modifier = modifier,
//        factory = {
//            val view = PreviewView(it)
//            view.scaleType=PreviewView.ScaleType.FILL_END
//            view
//
//        },
//        update = { previewView ->
//            val imageAnalyzer = ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//                .build()
//                .also {
//                    it.setAnalyzer(executor) { image ->
//                        val mpImage = mpimage(image,isFrontCamera = true)
//                        val frameTime = SystemClock.uptimeMillis()
//                        poseLandmarker.detectAsync(mpImage, frameTime)
//                        image.close()
//                    }
//                }
//
//            val cameraProvider = ProcessCameraProvider.getInstance(context)
//            cameraProvider.addListener(
//                {
//                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
//                    val cameraProvider = cameraProvider.get()
//                    cameraProvider.unbindAll()
//                    cameraProvider.bindToLifecycle(
//                        lifecycleOwner,
//                        cameraSelector,
//                        imageAnalyzer
//                    )
//                    val preview = Preview.Builder().build()
//                    preview.setSurfaceProvider(previewView.surfaceProvider)
//
//                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
//                },
//                ContextCompat.getMainExecutor(context)
//            )
//        }
//    )
//
//    DrawPosesOnPreview(modifier,resultBundleState)
//}
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//private fun DrawPosesOnPreview(modifier: Modifier,resultBundleState: MutableState<PoseDetector.ResultBundle?>) {
//    val resultBundle = resultBundleState.value
//    Ann()
//    if (resultBundle != null && basicCountdownTimer(10)==0) {
//        if(basicCountdownTimer(time = GlobalValues.time.toInt())>0){
//            writeCsv(resultBundle)
//
//        Canvas(modifier = modifier) {
//            val scale = max(size.width * 1f / resultBundle.inputImageWidth, size.height * 1f / resultBundle.inputImageHeight)
//            resultBundle.results.forEachIndexed { resultIndex, result ->
//                result.landmarks().forEachIndexed { landmarkIndex, poseLandmarks ->
//                    poseLandmarks.forEach { landmark ->
//                        drawCircle(
//                            color = Color.Yellow,
//                            radius = 10F,
//                            center = Offset(landmark.x() * scale*resultBundle.inputImageWidth-300f, landmark.y() * scale*resultBundle.inputImageHeight)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//private fun mpimage(
//    imageProxy: ImageProxy,
//    isFrontCamera: Boolean,
//): MPImage? {
//    // Copy out RGB bits from the frame to a bitmap buffer
//    val bitmapBuffer =
//        Bitmap.createBitmap(
//            imageProxy.width,
//            imageProxy.height,
//            Bitmap.Config.ARGB_8888
//        )
//
//    imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
//    imageProxy.close()
//
//    val matrix = Matrix().apply {
//        // Rotate the frame received from the camera to be in the same direction as it'll be shown
//        postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
//        if (isFrontCamera) {
//            postScale(
//                -1f,
//                1f,
//                imageProxy.width.toFloat(),
//                imageProxy.height.toFloat()
//            )
//        }
//    }
//    val rotatedBitmap = Bitmap.createBitmap(
//        bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
//        matrix, true
//    )
//
//    // Convert the input Bitmap object to an MPImage object to run inference
//    val mpImage = BitmapImageBuilder(rotatedBitmap).build()
//    return  mpImage
//}