package uk.co.deftelf.pims

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_photo.view.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSource
import org.apache.commons.io.IOUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import com.squareup.moshi.Types.newParameterizedType



class MainActivity : AppCompatActivity() {

    private lateinit var items: List<PopsaImageData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listType = Types.newParameterizedType(List::class.java, PopsaImageData::class.java)
        items = Moshi.Builder().build().adapter<List<PopsaImageData>>(listType).fromJson(IOUtils.toString(assets.open("droid-techtest-photos.json")))!!

        listView.layoutManager = LinearLayoutManager(this).apply {
            isItemPrefetchEnabled = true
        }
        // Fastscrolling is enabled but is essentially useless since handle is too small
        // Something like this needs implementing https://stackoverflow.com/questions/47846873/recyclerview-fast-scroll-thumb-height-too-small-for-large-data-set
        listView.adapter = Adapter()
    }

    inner class PhotoHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(this).inflate(R.layout.item_photo, parent, false)) {
        val imageView = itemView.imageView
        val loader = ProgressiveImageLoader(imageView)
    }

    inner class Adapter : RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PhotoHolder(parent)

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            holder.loader.loadItem(items[position], listView.width) // Note we can't use the holder imageView width because it won't have been laid out yet
        }

    }
}
