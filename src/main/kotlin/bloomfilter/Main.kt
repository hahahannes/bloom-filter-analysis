package bloomfilter

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

val INPUT_FILE_NAME = System.getenv("input_file_name") ?: "data_test"
val THRESHOlD = System.getenv("threshold").toDouble() ?: 0.8
val FILE_PATH = "resources/" + INPUT_FILE_NAME + ".csv"
val OUT_FILE_PATH = "resources/data"

// Combinations to test filter length with optimal amount of hash functions or same amount ?
val combinationsLength = listOf(
        Triple(300, 5, 0),
        Triple(600, 10, 0),
        Triple(900, 15, 0),
        Triple(1200, 20, 0),
        Triple(1500, 25, 0),
        Triple(1800, 30, 0)
)

// Combinations to test amount of hash functions
val combinationsHash = listOf(
        Triple(1200, 1, 0),
        Triple(1200, 5, 0),
        Triple(1200, 10, 0),
        Triple(1200, 15, 0),
        Triple(1200, 20, 0),
        Triple(1200, 25, 0),
        Triple(1200, 30, 0)
)

// Combinations to test amount of folding
val combinationsFolding = listOf(
        Triple(1200, 20, 0),
        Triple(1200, 20, 1),
        Triple(1200, 20, 2)
)

fun runTest(combinations: List<Triple<Int, Int, Int>>, testCase: String): List<Result> {
    val results = mutableListOf<Result>()
    val customMetricSpace = CustomMetricSpace()
    for (combination in combinations) {
        println(combination)
        CalculateBloomfilter.filterLength = combination.first
        CalculateBloomfilter.amountOfHashFunctions = combination.second
        CalculateBloomfilter.amountOfFolding = combination.third
        val createBloomFilterFile = CreateBloomFilterFile(FILE_PATH, OUT_FILE_PATH)
        val start = LocalDateTime.now()
        createBloomFilterFile.createBoolfilter()
        val done = LocalDateTime.now()
        val computingTime = ChronoUnit.MILLIS.between(start, done)
        val resultTriple = customMetricSpace.calculateFilter(OUT_FILE_PATH, PivotFinderType.FFT, 500, THRESHOlD)
        results.add(Result(combination.first, combination.second, combination.third, resultTriple.first, resultTriple.second, resultTriple.third, computingTime, testCase))
    }

    results.forEach{
        println("filterlength ${it.filterLength}, hash ${it.amountOfHashFunctions}, folding ${it.amountOfFolding} matches ${it.match}, truematches ${it.trueMatch}")
    }

    Plotter.plotResults(results.toTypedArray(), "plot", testCase)
    return results
}

fun generateResultsCSV(results: List<List<Result>>) {
    var fileWriter: FileWriter? = null

    try {
        fileWriter = FileWriter("results.csv")
        val useDockerVolume = System.getenv("use_volume").toBoolean()

        if(useDockerVolume) {
            fileWriter = FileWriter("/data/results.csv")
        }

        val CSV_HEADER = "testCase,fileName,threshold,length,hash,folding,matches,trueMatches,computingTime,matchingTime,precision,recall,fmeasure"
        fileWriter.append(CSV_HEADER)
        fileWriter.append('\n')

        for (result in results) {
            for (combination in result) {
                val truematch = combination.trueMatch.toFloat()
                val match = combination.match.toFloat()
                val precision = truematch / match
                var expectedTruePositives = System.getenv("expected_true_positives")
                if(expectedTruePositives == null) {
                    expectedTruePositives = "15000"
                }
                val recall = truematch / expectedTruePositives.toFloat()
                val fmeasure = 2 * (precision * recall) / (precision + recall)
                fileWriter.append(combination.testCase.toString())
                fileWriter.append(',')
                fileWriter.append(INPUT_FILE_NAME.toString())
                fileWriter.append(',')
                fileWriter.append(THRESHOlD.toString())
                fileWriter.append(',')
                fileWriter.append(combination.filterLength.toString())
                fileWriter.append(',')
                fileWriter.append(combination.amountOfHashFunctions.toString())
                fileWriter.append(',')
                fileWriter.append(combination.amountOfFolding.toString())
                fileWriter.append(',')
                fileWriter.append(combination.match.toString())
                fileWriter.append(',')
                fileWriter.append(combination.trueMatch.toString())
                fileWriter.append(',')
                fileWriter.append(combination.computingTime.toString())
                fileWriter.append(',')
                fileWriter.append(combination.matchingTime.toString())
                fileWriter.append(',')
                fileWriter.append(precision.toString())
                fileWriter.append(',')
                fileWriter.append(recall.toString())
                fileWriter.append(',')
                fileWriter.append(fmeasure.toString())
                fileWriter.append('\n')
            }
        }
        println("Write CSV successfully!")
    } catch (e: Exception) {
        println("Writing CSV error!")
        e.printStackTrace()
    } finally {
        try {
            fileWriter!!.flush()
            fileWriter.close()
        } catch (e: IOException) {
            println("Flushing/closing error!")
            e.printStackTrace()
        }
    }
}

fun main(args: Array<String>) {
    // Calculate Bloomfilters and compare metrics like precision and computing time based on parameters like length, amount of folding and amount of hash functions
    var results = mutableListOf<List<Result>>()
    results.add(runTest(combinationsLength, "length"))
    results.add(runTest(combinationsFolding, "folding"))
    results.add(runTest(combinationsHash, "hash"))
    generateResultsCSV(results)
}
