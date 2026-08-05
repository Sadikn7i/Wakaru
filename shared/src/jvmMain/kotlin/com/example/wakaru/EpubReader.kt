package com.example.wakaru

import nl.siegmann.epublib.epub.EpubReader
import nl.siegmann.epublib.domain.Book
import java.io.InputStream

fun loadEpub(inputStream: InputStream): Book {
    val reader = EpubReader()
    return reader.readEpub(inputStream)
}

fun getChapterCount(book: Book): Int {
    return book.contents.size
}

fun getChapterText(book: Book, chapterIndex: Int): String {
    val resource = book.contents[chapterIndex]
    var html = String(resource.data)

    // Remove <style>...</style> blocks entirely, including their content
    html = html.replace(Regex("(?s)<style.*?</style>"), "")
    // Remove <script>...</script> blocks entirely too, just in case
    html = html.replace(Regex("(?s)<script.*?</script>"), "")
    // Now strip remaining HTML tags
    html = html.replace(Regex("<[^>]*>"), " ")
    // Collapse excess whitespace
    html = html.replace(Regex("\\s+"), " ").trim()

    return html
}