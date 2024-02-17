package com.shubham.final_project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter

@Composable
fun CsvWriter(){
    val writer= csvWriter { delimiter = '\t' }
    val rows = listOf(listOf("a", "b", "c"), listOf("d", "e", "f"))
    writer.writeAll(rows, "test.csv", append = true)
}