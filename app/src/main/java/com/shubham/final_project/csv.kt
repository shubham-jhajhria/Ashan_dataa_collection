package com.shubham.final_project

import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
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
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import java.io.File
import java.time.LocalDateTime
import java.util.Calendar


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun writeCsv(resultBundle: PoseDetector.ResultBundle){
    val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    val csv=File(folder,"${GlobalValues.asanName}.csv")
    val fileExists = csv.exists()
    csv.createNewFile()

    csvWriter().open(csv,append = true){
        if (!fileExists) {
            writeRow(getColumnHeadings())
        }
        writeRow(appendLandmarkCoordinates(resultBundle))
    }
}

fun getColumnHeadings(): List<String> {
    val headings = mutableListOf<String>()
    // Add column headings for landmarks
    headings.add("TimeStamp")
    for (i in 0 until 33) {
        headings.add("Landmark_${i + 1}_X")
        headings.add("Landmark_${i + 1}_Y")
        headings.add("Landmark_${i + 1}_Z")
        headings.add("Landmark_${i + 1}_Presence")
        headings.add("Landmark_${i + 1}_Visibility")
    }
    // Add additional column headings

    headings.add("Asan_Name")
    return headings
}

@RequiresApi(Build.VERSION_CODES.O)
fun appendLandmarkCoordinates(resultBundle: PoseDetector.ResultBundle): List<String> {
    val coordinatesList: MutableList<String> = mutableListOf()
    val calendar = Calendar.getInstance()

    val current = LocalDateTime.of(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        calendar.get(Calendar.SECOND)
    )
    coordinatesList.add(current.toString())
    resultBundle.results.forEach { result ->
        result.landmarks().forEach { poseLandmarks ->
            poseLandmarks.forEach { landmark ->
                coordinatesList.add("${landmark.x()}")
                coordinatesList.add("${landmark.y()}")
                coordinatesList.add("${landmark.z()}")
                val presence = landmark.presence().toString().removePrefix("Optional[").removeSuffix("]")
                val visibility = landmark.visibility().toString().removePrefix("Optional[").removeSuffix("]")
                coordinatesList.add(presence)
                coordinatesList.add(visibility)
//                coordinatesList.add("${landmark.presence()}")
//                coordinatesList.add("${landmark.visibility()}")
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
