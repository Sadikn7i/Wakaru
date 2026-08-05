package com.example.wakaru

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File
import java.io.FileInputStream
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Wakaru") {
        var chapterText by remember { mutableStateOf("No book loaded yet.") }

        MaterialTheme {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Button(onClick = {
                        val chooser = JFileChooser()
                        chooser.fileFilter = FileNameExtensionFilter("EPUB files", "epub")
                        val result = chooser.showOpenDialog(null)
                        if (result == JFileChooser.APPROVE_OPTION) {
                            val file: File = chooser.selectedFile
                            val inputStream = FileInputStream(file)
                            val book = loadEpub(inputStream)
                            chapterText = getChapterText(book, 0)
                        }
                    }) {
                        Text("Pick EPUB File")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = chapterText,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            }
        }
    }
}