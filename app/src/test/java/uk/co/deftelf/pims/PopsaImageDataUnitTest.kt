package uk.co.deftelf.pims

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PopsaImageDataUnitTest {

    @Mock
    lateinit var context: Context

    @Test
    fun getUrl() {
        val image = PopsaImageData("name", "jpg")
        assertEquals("name", image.name)
        assertEquals("https://imagethumb.popsa.io/popsa-test-images/techtest/name.jpg", image.url)
        assertEquals("https://imagethumb.popsa.io/popsa-test-images/techtest/name.jpg?width=262", image.getUrl(262))
    }

}
