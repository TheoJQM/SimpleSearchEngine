package search

import java.io.File

const val menu =
"""=== Menu ===
1. Search information.
2. Print all data.
0. Exit."""

class SearchEngine(private val file: String) {
    private val dataLines = mutableListOf<String>()
    private val invertedIndex = mutableMapOf<String, IntArray>()
    private var exit = true

    fun search() {
        fillData()

        while (exit) {
            println(menu)
            when (readln()) {
                "1" -> searchPerson()
                "2" -> printAllData()
                "0" -> exit = false
                else -> println("\nIncorrect option! Try again.")
            }
        }
        println("\nBye!")
    }

    private fun fillData() {
        val content = File(file).readLines()
        content.forEach { dataLines.add(it)}
        createInvertedIndex()
    }

    private fun createInvertedIndex() {
        for (i in dataLines.indices) {
            val words = dataLines[i].split(" ")
            words.forEach { invertedIndex[it.uppercase()] = (invertedIndex[it.uppercase()] ?: intArrayOf()).plus(i) }
        }
    }

    private fun searchPerson() {
        val strategy = println("\nSelect a matching strategy: ALL, ANY, NONE").run { readln() }
        val data = println("\nEnter a name or email to search all suitable people.").run { readln() }

        when (strategy) {
            "ALL" -> searchAll(data)
            "ANY" -> searchAny(data)
            "NONE" -> searchNone(data)
        }

    }

    private fun searchAll(data: String) {
        val result = mutableListOf<String>()
        val queryWords = data.split(" ")
        val indices = invertedIndex[queryWords.first().uppercase()]

        if (indices == null) {
            println("No matching people found.")
        } else {
            indices.forEach { index ->
                val line = dataLines[index]
                if (queryWords.all { line.uppercase().contains(it.uppercase())  }) result.add(dataLines[index])
            }
            println("${result.size} person${if (indices.size > 1) "s" else ""} found:")
            result.forEach { println(it) }
        }
        println()
    }

    private fun searchAny(data: String) {
        val result = mutableListOf<String>()
        val queryWords = data.split(" ")
        var indices = intArrayOf()
        queryWords.indices.forEach {
            indices += invertedIndex[queryWords[it].uppercase()] ?: intArrayOf()
        }
        indices = indices.distinct().toIntArray()

        if (indices.isEmpty()) {
            println("No matching people found.")
        } else {
            println("${indices.size} person${if (indices.size > 1) "s" else ""} found:")
            indices.forEach { index ->
                result.add(dataLines[index])
            }
            result.forEach { println(it) }
        }
        println()
    }

    private fun searchNone(data: String) {
        val result = mutableListOf<String>()
        val queryWords = data.split(" ")
        var indices = intArrayOf()
        queryWords.indices.forEach {
            indices += invertedIndex[queryWords[it].uppercase()] ?: intArrayOf()
        }
        indices = indices.distinct().sorted().toIntArray()

        if (indices.isEmpty()) {
            for (i in dataLines.indices) {
                result.add(dataLines[i])
            }
        } else {
            for (i in dataLines.indices) {
                if (!indices.contains(i)) result.add(dataLines[i])
            }
            println("${result.size} person${if (indices.size > 1) "s" else ""} found:")
            result.forEach { println(it) }
        }
        println()
    }

    private fun printAllData() {
        println("\n=== List of people ===")
        dataLines.forEach { println(it) }
        println()
    }
}

fun main(args: Array<String>) {
    val engine = SearchEngine(args[1])
    engine.search()
}