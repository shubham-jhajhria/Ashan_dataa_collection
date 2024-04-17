package com.shubham.final_project

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.shubham.final_project.components.CreateHeader
import com.shubham.final_project.components.basicCountdownTimer
import com.shubham.final_project.ui.theme.Final_projectTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?){
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(context: Context) {
    val navController = rememberNavController()
    val poseDetector = PoseDetector(context = context)
    val context = LocalContext.current
//    val poseLandmarker = poseDetector.initializePoseLandmarker()
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
            PoseDetectionScreen(navController= navController,context=context)

        }
    }
}
@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PoseDetectionScreen(navController: NavController, context: Context) {
    LaunchedEffect(Unit) {
        (context as MainActivity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    }
    DisposableEffect(context) {
        onDispose {
            (context as MainActivity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    val success by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success))
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    if(basicCountdownTimer(time = GlobalValues.time.toInt())==0 && basicCountdownTimer(10)==0) {
        Box(modifier = Modifier.fillMaxSize()) {
            LottieAnimation(
                composition = success,
                iterations = 1,
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.Center)
            )
        }
        coroutineScope.launch {
            delay(2000)
            navController.navigate("mainScreen") {
                popUpTo("mainScreen")
            }
        }
    }
    else{
        Column(
            Modifier
        ) {

            Row(
                Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    PoseDetectionPreview( lifecycleOwner = lifecycleOwner, modifier = Modifier )

                }
//            Box(
//                Modifier
//                    .fillMaxHeight()
//                    .fillMaxWidth()
//                    .background(Color.Black),
//                contentAlignment = Alignment.Center,
//            ) {
//                Text(text = "Video")
//            }
            }
        }
    }
}



@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MyAppContent(navController: NavController) {
    AasanName(navController)
    CreateHeader()
}

@ExperimentalComposeUiApi
@Composable
fun AasanName(navController: NavController) {
    InputEnd(
        navigateToCamera = { navController.navigate("PoseScreen") }
    ) { asnName, timeData ->
        Log.d("Name", "Aasan Name: $asnName")
        Log.d("Time", "Time Data: $timeData")
    }
}


@RequiresApi(Build.VERSION_CODES.O)
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




