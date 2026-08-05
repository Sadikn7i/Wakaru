@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.example.wakaru

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import nl.siegmann.epublib.domain.Book
import java.io.File
import java.io.FileInputStream
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Wakaru") {
        var book by remember { mutableStateOf<Book?>(null) }
        var chapterIndex by remember { mutableStateOf(0) }
        var words by remember { mutableStateOf<List<WordToken>>(emptyList()) }
        var selectedWord by remember { mutableStateOf<WordToken?>(null) }

        fun loadChapter(index: Int) {
            book?.let {
                chapterIndex = index
                val text = getChapterText(it, index)
                words = tokenizeText(text)
                selectedWord = null
            }
        }

        fun isRealContent(index: Int): Boolean {
            val b = book ?: return false
            if (index < 0 || index >= getChapterCount(b)) return false
            val text = getChapterText(b, index)
            return text.length > 200
        }

        fun findNextContentChapter(from: Int, direction: Int): Int? {
            val b = book ?: return null
            var i = from + direction
            while (i in 0 until getChapterCount(b)) {
                if (isRealContent(i)) return i
                i += direction
            }
            return null
        }

        MaterialTheme {
            Surface {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Row {
                        Button(onClick = {
                            val chooser = JFileChooser()
                            chooser.fileFilter = FileNameExtensionFilter("EPUB files", "epub")
                            val result = chooser.showOpenDialog(null)
                            if (result == JFileChooser.APPROVE_OPTION) {
                                val file: File = chooser.selectedFile
                                val inputStream = FileInputStream(file)
                                val loadedBook = loadEpub(inputStream)
                                book = loadedBook
                                val firstContentChapter = (0 until getChapterCount(loadedBook)).firstOrNull {
                                    getChapterText(loadedBook, it).length > 200
                                } ?: 0
                                loadChapter(firstContentChapter)
                            }
                        }) {
                            Text("Pick EPUB File")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                findNextContentChapter(chapterIndex, -1)?.let { loadChapter(it) }
                            },
                            enabled = book != null && findNextContentChapter(chapterIndex, -1) != null
                        ) { Text("Previous") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                findNextContentChapter(chapterIndex, 1)?.let { loadChapter(it) }
                            },
                            enabled = book != null && findNextContentChapter(chapterIndex, 1) != null
                        ) { Text("Next") }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (book != null) {
                        Text("Chapter ${chapterIndex + 1} of ${getChapterCount(book!!)}")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    selectedWord?.let { w ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            color = Color(0xFFEFE6FF)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(w.surface, fontWeight = FontWeight.Bold)
                                Text("Reading: ${w.reading}")
                                Text("Part of speech: ${w.partOfSpeech}")
                            }
                        }
                    }

                    FlowRowWords(words = words, onWordClick = { selectedWord = it })
                }
            }
        }
    }
}

@Composable
fun FlowRowWords(words: List<WordToken>, onWordClick: (WordToken) -> Unit) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
    ) {
        words.forEach { token ->
            Text(
                text = token.surface,
                modifier = Modifier
                    .clickable { onWordClick(token) }
                    .padding(horizontal = 2.dp, vertical = 4.dp)
            )
        }
    }
}