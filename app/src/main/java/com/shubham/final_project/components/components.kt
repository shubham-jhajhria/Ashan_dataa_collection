package com.shubham.final_project.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CreateHeader() {
    Column(verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {


        Surface (modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
            color = Color(0xFFF7F9FA)
        ){
            Column(modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Yoga Data Collection",
                    style = TextStyle(color = Color(0xFF0A8ED9),
                        fontSize =24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

        }
    }
}

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    leadingIcon: ImageVector? = null,
    leadingIconContentDescription: String? = null, // New parameter for content description
    isSingleLine: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = valueState.value,
        onValueChange = { valueState.value = it },
        label = { Text(text = labelId) },
        singleLine = isSingleLine,
        textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
        modifier = modifier.padding(bottom = 10.dp, start = 10.dp, end = 10.dp),
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = leadingIconContentDescription ?: "", // Use provided content description or empty string if null
                )
            }
        },
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = onAction
    )
}

@Composable
fun Ann(){
    var animationActive by remember { mutableStateOf(true) }
    if (animationActive) {
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 8f,
            animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
            label = "animation"
        )
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)) {
            Text(text = "Starts In",
                modifier = Modifier
                    .padding(10.dp,60.dp,10.dp,10.dp)
                    .align(Alignment.TopCenter),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
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
        delay(10000L)
        animationActive = false
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
