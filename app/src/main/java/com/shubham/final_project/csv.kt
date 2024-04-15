package com.shubham.final_project

import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import java.time.LocalDateTime
import java.util.Calendar


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun writeCsv(resultBundle: PoseDetector.ResultBundle){
    val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
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


    resultBundle.results.forEach { result ->
        coordinatesList.add(result.timestampMs().toString())
        result.worldLandmarks().forEach { poseLandmarks ->
            poseLandmarks.forEach { landmark ->
                coordinatesList.add("${landmark.x()}")
                coordinatesList.add("${landmark.y()}")
                coordinatesList.add("${landmark.z()}")
                val presence = landmark.presence().toString().removePrefix("Optional[").removeSuffix("]")
                val visibility = landmark.visibility().toString().removePrefix("Optional[").removeSuffix("]")
                coordinatesList.add(presence)
                coordinatesList.add(visibility)

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
