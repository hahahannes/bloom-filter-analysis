package bloomfilter

import metricSpace.MetricSpace
import metricSpace.data.Match
import metricSpace.data.Pivot
import metricSpace.data.Record
import metricSpace.distance.HammingDistance
import metricSpace.distance.JaccardSimilarity
import metricSpace.pivots.FFTFinder
import metricSpace.pivots.MaxSepFinder
import metricSpace.pivots.RandomFinder
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

val SEPERATOR = ","
val ID_FIELD = 0
val BIT_FIELD = 1

class CustomMetricSpace(): MetricSpace() {

    private val distanceFunction = HammingDistance()
    private val similarityFunction = JaccardSimilarity()


    fun calculateFilter(inputPath: String, filter: PivotFinderType, numberOfPivot: Number, threshold: Double): Triple<Number, Number, Number> {
        val indexDataPath = inputPath + "_org.csv"
        val queryDataPath = inputPath + "_dup.csv"

        val thresholdFactor = (1.0 - threshold) / threshold
        val pivotFinder = when ( filter ) {
            PivotFinderType.FFT ->  FFTFinder(distanceFunction)
            PivotFinderType.MS ->  MaxSepFinder(distanceFunction)
            PivotFinderType.RANDOM ->  RandomFinder()
        }

        val indexData = readData(indexDataPath, ID_FIELD, BIT_FIELD, SEPERATOR)
        val pivots = pivotFinder.findPivots(indexData, numberOfPivot.toInt())
        assignPivots(indexData, pivots)
        val queryData = readData(queryDataPath, ID_FIELD, BIT_FIELD, SEPERATOR)
        val matches: Triple<List<Match>, Number, Number> = this.match(pivots, queryData, threshold, thresholdFactor)
        return Triple(matches.first.size, matches.second, matches.third)
    }

    private fun match(pivotsWithIndex: List<Pivot>, queryData: List<Record>, treshold: Double, tresholdFactor: Double): Triple<List<Match>, Number, Number> {
        var truematchCount = 0L
        var copiesQueriesCount = 0L
        var matchingCompTECount = 0L
        var matchingCompCount = 0L
        var matchingCompSimCount = 0L
        val matches = mutableListOf<Match>()
        val var26 = queryData.iterator()
        val start = LocalDateTime.now()

        label37@ while (var26.hasNext()) {
            val q = var26.next()
            val queryRadius = Math.ceil(tresholdFactor * q.value.cardinality().toDouble()).toLong()
            val var28 = pivotsWithIndex.iterator()

            while (true) {
                var queryDistance: Long
                var p: Pivot
                do {
                    if (!var28.hasNext()) {
                        continue@label37
                    }

                    p = var28.next()
                    queryDistance = distanceFunction.calculateDistance(q.value, p.value)
                } while (queryDistance > p.radius + queryRadius)

                ++copiesQueriesCount
                val var30 = p.assignedRecords.iterator()

                while (var30.hasNext()) {
                    val i = var30.next() as Record
                    ++matchingCompTECount
                    if (Math.abs(queryDistance - i.distanceToPivot) <= queryRadius) {
                        val distance = distanceFunction.calculateDistance(q.value, i.value)
                        ++matchingCompCount
                        if (distance <= queryRadius) {
                            val similarity = similarityFunction.calculateSimilarity(i.value, q.value)
                            ++matchingCompSimCount
                            if (treshold <= similarity) {
                                matches.add(Match(i, q, similarity))
                                if (i.id == q.id) {
                                    ++truematchCount
                                }
                            }
                        }
                    }
                }
            }
        }
        val done = LocalDateTime.now()
        val matchingTime = ChronoUnit.MILLIS.between(start, done)
        println(matches.size)
        println(truematchCount)
        return Triple(matches, truematchCount, matchingTime)
    }
}

enum class PivotFinderType {
    FFT,
    MS,
    RANDOM
}