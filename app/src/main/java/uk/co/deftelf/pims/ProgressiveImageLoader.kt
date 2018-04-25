package uk.co.deftelf.pims

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.Closeable
import java.lang.Exception

class ProgressiveImageLoader(val imageView: ImageView) : Closeable, Target {

    private val loadFractionOrder = arrayOf(0.05f, 0.2f, 1f) // We load in these fractions of the target size
    private var currentStep = 0
    private var targetWidth = 0
    private var data: PopsaImageData? = null

    fun loadItem(data: PopsaImageData, targetWidth: Int) {
        close()
        this.data = data
        this.targetWidth = targetWidth
        fetchImage()
    }

    private fun fetchImage() {
        val url = data?.getUrl(getCurrentStepWidth())
        Picasso.get().load(url).into(this)
    }

    /**
     * Get the width of the current step, or if we are out of range somehow then return the full width
     */
    private fun getCurrentStepWidth() = (loadFractionOrder.getOrNull(currentStep)?.let { targetWidth * it }?.toInt() ?: targetWidth)

    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
    }

    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
        imageView.setImageResource(android.R.drawable.stat_sys_warning)
    }

    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
        imageView.setImageBitmap(bitmap)
        if (currentStep < loadFractionOrder.size - 1 &&
                bitmap.width == getCurrentStepWidth()) { // Check server hasn't already sent us the biggest version, if it did then the width would be less than we asked for
            currentStep++
            fetchImage()
        }
    }


    override fun close() {
        currentStep = 0
        Picasso.get().cancelRequest(imageView)
        imageView.setImageDrawable(null)
    }

}
