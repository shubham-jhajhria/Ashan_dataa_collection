package com.shubham.final_project

import android.os.Environment
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File


@Composable
fun writeCsv(resultBundle: PoseDetector.ResultBundle){
    val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    val csv=File(folder,"${GlobalValues.asanName}.csv")
    csv.createNewFile()
    csvWriter().open(csv,append = true){

        writeRow(appendLandmarkCoordinates(resultBundle))
    }
}

fun appendLandmarkCoordinates(resultBundle: PoseDetector.ResultBundle): List<String> {
    val coordinatesList: MutableList<String> = mutableListOf()

    resultBundle.results.forEach { result ->
        result.landmarks().forEach { poseLandmarks ->
            poseLandmarks.forEach { landmark ->
                coordinatesList.add("${landmark.x()},${landmark.y()}")
            }
        }
    }
    coordinatesList.add(GlobalValues.asanName)
    return coordinatesList
}

object GlobalValues {
    var asanName: String by mutableStateOf("")
    var time: String by mutableStateOf("")
}