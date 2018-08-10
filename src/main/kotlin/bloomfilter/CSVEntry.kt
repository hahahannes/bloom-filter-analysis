package bloomfilter

class CSVEntry(userID: String, firstName: String, lastName: String, address: String, birthday: String) {
    val entry = "$firstName$lastName$address$birthday".replace("\\s".toRegex(), "")
    val bloomFilter = CalculateBloomfilter.calculateFilter(entry)
    val type = if(userID.contains("org"))  TypeOfEntry.ORIGINAL else TypeOfEntry.DUBLICATE
    val id = userID.split("-")[1]
}

enum class TypeOfEntry {
    ORIGINAL,
    DUBLICATE
}
