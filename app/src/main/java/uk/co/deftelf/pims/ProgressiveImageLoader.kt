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
        if (currentStep == 0) // If we failed on the current step show an error icon
            imageView.setImageResource(android.R.drawable.stat_sys_warning)
        // If we weren't on the first step then we actually did get a low-res image, so don't clear it, just leave it
    }

    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) { // We got a new image
        if (currentStep == 0 && // If we are getting our first image then fade in, else just snap replace
                from != Picasso.LoadedFrom.MEMORY) { // If we got from Picasso mem cache then don't fade as it will be applied immediately to the view before user sees it
            imageView.alpha = 0f
            imageView.animate().alpha(1f).setDuration(200L).start()
        }
        imageView.setImageBitmap(bitmap)
        if (currentStep < loadFractionOrder.size - 1 && // If there are more steps to do
                bitmap.width == getCurrentStepWidth()) { // Check server hasn't already sent us the biggest version, if it did then the width would be less than we asked for
            currentStep++
            fetchImage()
        }
    }


    override fun close() {
        currentStep = 0
        Picasso.get().cancelRequest(imageView)
        imageView.animate().cancel()
        imageView.alpha = 1f
        imageView.setImageDrawable(null)
    }

}
