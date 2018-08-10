package bloomfilter

import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

object CalculateBloomfilter{
    var filterLength = 1000
    var amountOfHashFunctions = 1
    var amountOfFolding = 0

    fun calculateFilter(_string: String): BitSet {
        var filter = BitSet(filterLength)
        val trigrams = getTrigramms(_string)
        trigrams.forEach { trigram ->
            val md5HashFunction = MessageDigest.getInstance("MD5")
            val shaHashFunction = MessageDigest.getInstance("SHA-1")
            val byteArr = trigram.toByteArray()
            md5HashFunction.update(byteArr)
            val md5HashValue = BigInteger(1, md5HashFunction.digest())
            shaHashFunction.update(byteArr)
            val shaHashValue = BigInteger(1, shaHashFunction.digest())
            for (i in 1..amountOfHashFunctions) {
                val endHash = ((md5HashValue * BigInteger(1, i.toString().toByteArray())) + shaHashValue).mod(BigInteger.valueOf(filterLength.toLong())).intValueExact()
                filter.set(endHash)
            }
        }

        if(amountOfFolding != 0) {
            filter = foldFilter(filter)
        }
        return filter
    }

    fun toString(filter: BitSet): String {
        val builder = StringBuilder()
        // dont use BitSet.size or Bitset.length - they dont represent the real length - instead use corrected filter length
        var length = filterLength
        if(amountOfFolding != 0) {
           length = filterLength / (2 * amountOfFolding)
        }
        for (i in 0..length-1) {
            if(filter.get(i)) {
                builder.append("1")
            } else {
                builder.append("0")
            }
        }
        return builder.toString()
    }

    fun foldFilter(filter: BitSet): BitSet {
        var foldedFilter = filter
        for (i in 0..amountOfFolding-1) {
            val center =  foldedFilter.length() / 2
            val firstHalf = foldedFilter.get(0, center)
            val secondHalf = foldedFilter.get(center, foldedFilter.length())

            foldedFilter = firstHalf
            foldedFilter.xor(secondHalf)
        }
        return foldedFilter
    }

    fun getTrigramms(_string: String): List<String> {
        return ngrams(3, "  $_string  ")
    }

    fun ngrams(n: Int, str: String): List<String> {
        val ngrams = ArrayList<String>()
        for (i in 0..str.length - n + 1 - 1)
            ngrams.add(str.substring(i, i + n))
        return ngrams
    }
}