package bloomfilter

import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class CalculateBloomfilterTest {

    @Test
    fun testTrigrams(){
        val trigrams = CalculateBloomfilter.getTrigramms("a")
        assertEquals(Arrays.asList("  a", " a ", "a  "), trigrams)
    }

    @Test
    fun test1Grams(){
        val ngram = CalculateBloomfilter.ngrams(1, "a")
        assertEquals(Arrays.asList("a"), ngram)
    }

    @Test
    fun test2Grams(){
        val ngram = CalculateBloomfilter.ngrams(2, "aab")
        assertEquals(Arrays.asList("aa", "ab"), ngram)
    }

    @Test
    fun test2GramsWithWhiteSpace(){
        val ngram = CalculateBloomfilter.ngrams(2, " aa")
        assertEquals(Arrays.asList(" a", "aa"), ngram)
    }

    @Test
    fun test0Folding(){
        CalculateBloomfilter.amountOfFolding = 0
        val filter = BitSet()
        filter.set(0)
        filter.set(1)
        filter.set(2)
        filter.set(3)
        val foldedFilter = CalculateBloomfilter.foldFilter(filter)
        assertEquals(foldedFilter, foldedFilter)
    }

    @Test
    fun test1Folding(){
        CalculateBloomfilter.amountOfFolding = 1
        var filter = BitSet()
        filter.set(0)
        filter.set(1)
        filter.set(2)
        filter.set(3)
        var foldedFilter = CalculateBloomfilter.foldFilter(filter)
        var expectedFolderFilter = BitSet()
        assertEquals(expectedFolderFilter, foldedFilter)

        filter = BitSet()
        filter.set(0)
        filter.set(3)
        foldedFilter = CalculateBloomfilter.foldFilter(filter)
        expectedFolderFilter = BitSet()
        expectedFolderFilter.set(0)
        expectedFolderFilter.set(1)
        assertEquals(expectedFolderFilter, foldedFilter)
    }

    @Test
    fun test2Folding(){
        CalculateBloomfilter.amountOfFolding = 2
        val filter = BitSet()
        filter.set(0)
        filter.set(1)
        filter.set(2)
        filter.set(3)
        val foldedFilter = CalculateBloomfilter.foldFilter(filter)
        val expectedFolderFilter = BitSet()
        assertEquals(expectedFolderFilter, foldedFilter)
    }

    @Test
    fun testFilterToString(){
        val filter = BitSet()
        filter.set(0)
        filter.set(1)
        filter.set(2)
        filter.set(3)
        CalculateBloomfilter.filterLength = 5
        val filterString = CalculateBloomfilter.toString(filter)
        val expectedFilterString = "11110"
        assertEquals(expectedFilterString, filterString)
    }
}