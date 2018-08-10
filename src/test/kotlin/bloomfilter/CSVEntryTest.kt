package bloomfilter

import org.junit.Test
import java.util.*
import kotlin.test.assertEquals


class CSVEntryTest {

    @Test
    fun testFilterWithLengthOne(){
        CalculateBloomfilter.filterLength = 1
        val entry = CSVEntry("ab-0-org", "a", "", "", "")
        val filter = BitSet()
        filter.set(0)
        assertEquals(filter, entry.bloomFilter)
    }

//    @Test
//    fun testFilterWithLengthOnehundretAndEmptyString(){
//        CalculateBloomfilter.filterLength = 100
//        val entry = CSVEntry("ab-0-org", "", "", "", "")
//        val hashFunction = MessageDigest.getInstance("Md5")
//        val byteArr = "   ".toByteArray()
//        hashFunction.update(byteArr)
//        val n = BigInteger(1, hashFunction.digest())
//        assertEquals(1, entry.bloomFilter.get(n.mod(BigInteger.valueOf(100.toLong())).intValueExact()))
//    }

    @Test
    fun testFilterWithLengthTen(){
        CalculateBloomfilter.filterLength = 10
        val entry = CSVEntry("ab-0-org", "a", "", "", "")
        val filter = BitSet()
        filter.set(1)
        filter.set(0)
        filter.set(8)
        assertEquals(filter, entry.bloomFilter)
    }

    @Test
    fun testUser(){
        CalculateBloomfilter.filterLength = 10
        val entry = CSVEntry("ab-0-org", "a", "", "", "")
        assertEquals("0", entry.id)
    }

    @Test
    fun testUserTypeOriginal(){
        CalculateBloomfilter.filterLength = 10
        val entry = CSVEntry("ab-0-org", "a", "", "", "")
        assertEquals(TypeOfEntry.ORIGINAL, entry.type)
    }

    @Test
    fun testUserTypeDuplicate(){
        CalculateBloomfilter.filterLength = 10
        val entry = CSVEntry("ab-0-dup", "a", "", "", "")
        assertEquals(TypeOfEntry.DUBLICATE, entry.type)
    }
}