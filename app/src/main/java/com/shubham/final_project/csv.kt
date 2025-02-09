package com.shubham.final_project

import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

class CsvViewModel() : ViewModel() {
    @RequiresApi(Build.VERSION_CODES.O)
    fun writeCsv(resultBundle: PoseDetector.ResultBundle) {
        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val csv = File(folder, "${GlobalValues.asanName}.csv")
        val fileExists = csv.exists()
        csv.createNewFile()
        csvWriter().open(csv, append = true) {
            if (!fileExists) {
                writeRow(getColumnHeadings())
            }
            writeRow(appendLandmarkCoordinates(resultBundle))
        }
    }
    private fun getColumnHeadings(): List<String> {
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
        headings.add("Asan_Name")
        return headings
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun appendLandmarkCoordinates(resultBundle: PoseDetector.ResultBundle): List<String> {
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
}