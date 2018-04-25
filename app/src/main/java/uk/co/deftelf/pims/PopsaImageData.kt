package uk.co.deftelf.pims

open class PopsaImageData(
        val name: String,
        val format: String
) {
    val url
        get() = "https://imagethumb.popsa.io/popsa-test-images/techtest/$name.$format"
    fun getUrl(width: Int) = "$url?width=$width"
    // In reality for security this should probably be composed using a Uri object as this doesn't feel very safe,
    // anything could be put in the data and cause the client to make dodgy requests
}
