package com.example.wakaru

import nl.siegmann.epublib.epub.EpubReader
import nl.siegmann.epublib.domain.Book
import java.io.InputStream

fun loadEpub(inputStream: InputStream): Book {
    val reader = EpubReader()
    return reader.readEpub(inputStream)
}

fun getChapterText(book: Book, chapterIndex: Int): String {
    val resource = book.contents[chapterIndex]
    val html = String(resource.data)
    return html.replace(Regex("<[^>]*>"), " ").trim()
}