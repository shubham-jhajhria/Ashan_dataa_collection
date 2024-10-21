package com.shubham.final_project


import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.shubham.final_project.components.Ann
import com.shubham.final_project.components.basicCountdownTimer
import kotlin.math.max

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PoseDetectionPreview(
    context: Context,
    poseLandmarker: PoseLandmarker?,
    lifecycleOwner: LifecycleOwner,
    viewModel: PoseDetectionViewmodel,
    modifier: Modifier
){

    if (poseLandmarker != null) {
        AndroidView(
            factory = {
                val previewView = PreviewView(it)
                previewView.scaleType=PreviewView.ScaleType.FILL_END
                viewModel.initializeCamera(context,previewView, lifecycleOwner,poseLandmarker)
                previewView
            },
            modifier = modifier,
        )
    }

    DrawPosesOnPreview(viewModel = viewModel, lifecycleOwner = lifecycleOwner)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DrawPosesOnPreview(viewModel: PoseDetectionViewmodel,
                               lifecycleOwner: LifecycleOwner,csvViewModel: CsvViewModel = viewModel()) {
    val poseDetector = remember(viewModel.poseDetector.value) { mutableStateOf(viewModel.poseDetector.value) }
    val poseResult = remember(viewModel.poseResult.value) { mutableStateOf(viewModel.poseResult.value) }
    LaunchedEffect(poseDetector.value?.getPoseResult()?.value) {
            viewModel.processPoseDetection()
    }
    DisposableEffect(viewModel.poseResult) {
        val observer = Observer<PoseDetector.ResultBundle?> { result ->
            poseResult.value = result
        }
        viewModel.poseResult.observe(lifecycleOwner, observer)

        onDispose {
            viewModel.poseResult.removeObserver(observer)
        }
    }
    Ann()
    if (basicCountdownTimer(10)==0) {
        if(basicCountdownTimer(time = GlobalValues.time.toInt())>0){
            poseResult.value?.let { csvViewModel.writeCsv(it) }
            Box(Modifier.fillMaxSize()
                ,
                contentAlignment = Alignment.BottomCenter){
                Text(text = "Detection Data Started Writing", fontSize = 20.sp)
            }
        }
    }
}