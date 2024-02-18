package com.shubham.final_project
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.FlowColumnScopeInstance.align
//import androidx.compose.foundation.layout.FlowColumnScopeInstance.align

//import androidx.compose.foundation.layout.FlowRowScopeInstance.align
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Preview
@Composable
fun Ani(){
    var animationActive by remember { mutableStateOf(true) }

    if (animationActive) {
        val infiniteTransition = rememberInfiniteTransition()
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 8f,
            animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
            label = "animation"
        )
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)) {
            Text(text = "Starts In", modifier = Modifier
                .padding(10.dp,60.dp,10.dp,10.dp)
                .align(Alignment.TopCenter), fontSize = 40.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "${basicCountdownTimer(10)}",
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin.Center
                    }
                    .align(Alignment.Center),
                style = LocalTextStyle.current.copy(textMotion = TextMotion.Animated)
            )
        }
    }

    LaunchedEffect(key1 = animationActive) {
        delay(10000L) // Wait for 5 seconds
        animationActive = false // Disable animation after 5 seconds
    }
}

@Composable
fun basicCountdownTimer(time:Int): Int {
    var timeLeft by remember { mutableIntStateOf(time) }

    LaunchedEffect(key1 = timeLeft) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }
    return timeLeft
}
