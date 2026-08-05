package com.example.wakaru

import com.atilika.kuromoji.ipadic.Tokenizer
import com.atilika.kuromoji.ipadic.Token

data class WordToken(
    val surface: String,      // the actual word as it appears
    val reading: String,      // katakana reading
    val partOfSpeech: String  // noun, verb, particle, etc.
)

private val tokenizer = Tokenizer()

fun tokenizeText(text: String): List<WordToken> {
    val tokens: List<Token> = tokenizer.tokenize(text)
    return tokens.map {
        WordToken(
            surface = it.surface,
            reading = it.reading ?: "",
            partOfSpeech = it.partOfSpeechLevel1 ?: ""
        )
    }
}