package uk.co.deftelf.pims

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.test.UiThreadTest
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.PicassoProvider

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ProgressiveImageLoaderTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("uk.co.deftelf.pims", appContext.packageName)
    }

    @Before
    fun setup() {
    }

    // Hmmm... this doesn't work. I think the Picasso library is not running due to being under instrumentation
    // Ultimately we want to provide some internal assets in the test build that we can provide in a custom PopsaImageData (so we're not actually relying on server access)
    // Probably one example that is complete, and one with missing images, and one with no images at all
    // Then we can fire that at the loader and wait for the updates, and then see what the ImageView was changed to
    // Picasso is being uncooperative so I'll leave this as a task to talk about
    @UiThreadTest
    @Test
    @Ignore("Picasso isn't running in instrumentation")
    fun loadInto() {
        val syncObject = Object()
        val imageView = object : ImageView(InstrumentationRegistry.getTargetContext()) {
            override fun setImageBitmap(bm: Bitmap?) {
                super.setImageBitmap(bm)
                synchronized(syncObject) {
                    syncObject.notify()
                }
            }

            override fun setImageDrawable(drawable: Drawable?) {
                super.setImageDrawable(drawable)
                synchronized(syncObject) {
                    syncObject.notify()
                }
            }

            override fun setImageResource(resId: Int) {
                super.setImageResource(resId)
                synchronized(syncObject) {
                    syncObject.notify()
                }
            }
        }

        val loader = ProgressiveImageLoader(imageView)
        val image = object : PopsaImageData("photo-0002", "jpg") {

        }
        loader.loadItem(image, 200)
        assertNull(imageView.drawable)
        synchronized(syncObject) {
            syncObject.wait(10000)
        }
        assertNotNull(imageView.drawable)
    }
}
