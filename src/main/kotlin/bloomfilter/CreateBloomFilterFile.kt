package bloomfilter

import org.apache.flink.api.java.ExecutionEnvironment
import java.io.File
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class CreateBloomFilterFile(val inputPath: String, val outputPath: String) {
    fun createBoolfilter(){
        val inputStream = File(inputPath).inputStream()
        val outputStreamOrg = File(outputPath + "_org.csv").outputStream()
        val outputStreamDup = File(outputPath + "_dup.csv").outputStream()
        val lineList = mutableListOf<String>()
        inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it)} }

        val env = ExecutionEnvironment.getExecutionEnvironment()
        val dataSet = env.readCsvFile(inputPath).types(String::class.java,  String::class.java, String::class.java, String::class.java, String::class.java)

        dataSet
                .map{ line -> CSVEntry(line.f0, line.f1, line.f2, line.f3, line.f4) }
                .returns(CSVEntry::class.java)
                .collect()
                .forEach { it ->
                    if (it.type == TypeOfEntry.ORIGINAL) {
                        outputStreamOrg.write(it.id.toByteArray())
                        outputStreamOrg.write(",".toByteArray())
                        val filterString = CalculateBloomfilter.toString(it.bloomFilter)
                        outputStreamOrg.write(filterString.toByteArray())
                        outputStreamOrg.write("\n".toByteArray())
                    } else {
                        outputStreamDup.write(it.id.toByteArray())
                        outputStreamDup.write(",".toByteArray())
                        val filterString = CalculateBloomfilter.toString(it.bloomFilter)
                        outputStreamDup.write(filterString.toByteArray())
                        outputStreamDup.write("\n".toByteArray())
                    }
                }
    }
}