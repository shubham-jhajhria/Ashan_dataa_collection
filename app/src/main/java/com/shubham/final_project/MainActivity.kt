package com.shubham.final_project

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.shubham.final_project.ui.theme.Final_projectTheme
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                val context = LocalContext.current
                Navigation(context)

            }
        }

        }
    override fun onDestroy() {
            super.onDestroy()
            // Release resources
            finish()
    }
}
@Composable
fun MyApp(content: @Composable ()-> Unit) {
    Final_projectTheme {
        Surface(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
            color = MaterialTheme.colorScheme.background) {
            content()
        }
    }

}


@Composable
fun Navigation(context: Context) {
    val navController = rememberNavController()
    val poseDetector = PoseDetector(context = context)
    val poseLandmarker = poseDetector.initializePoseLandmarker()
    lateinit var executor: Executor
    val poseResult = poseDetector.getPoseResult()
    executor = Executors.newSingleThreadExecutor()
    NavHost(navController = navController, startDestination = "mainScreen") {
        composable("mainScreen") {
            // This is the main screen
            MyAppContent(navController)
        }
        composable("PoseScreen") {
            // This is the camera preview screen
            PoseDetectionScreen(poseLandmarker, executor,poseResult)
        }
    }
}
@Composable
fun PoseDetectionScreen(poseLandmarker: PoseLandmarker, executor: Executor, poseResult: MutableState<PoseDetector.ResultBundle?>) {
    
    PoseDetectionPreview(poseLandmarker, executor, poseResult)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MyAppContent(navController: NavController) {

    AshanName(navController)
    CreateHeader()
}

@ExperimentalComposeUiApi
@Composable
fun AshanName(navController: NavController) {
    inputEnd(
        navigateToCamera = { navController.navigate("PoseScreen") }
    ) { asnName, timeData ->
        Log.d("Name", "Aasan Name: $asnName")
        Log.d("Time", "Time Data: $timeData")
    }
}


@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Final_projectTheme{
        MyApp{
            val context = LocalContext.current
            Navigation(context)

        }
    }
}



