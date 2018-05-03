package uk.co.deftelf.pims

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.res.AssetManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.apache.commons.io.IOUtils

class MainActivityViewmodel(assets: AssetManager, lifecycle: Lifecycle) : LifecycleObserver {

    // We push complete UI states to client UIs.
    // If this were for eg loading from a paged endpoint we'd continuously push states as we got more data
    val updater: BehaviorSubject<State> = BehaviorSubject.create()

    private lateinit var generator: Disposable

    init {
        lifecycle.addObserver(this)

        // Side effect of including rx is that we can easily push this potentially slow parsing onto a computation thread now rather than doing it on UI thread
        generator = Observable.just("droid-techtest-photos.json")
                .observeOn(Schedulers.computation())
                .subscribe {
                    val listType = Types.newParameterizedType(List::class.java, PopsaImageData::class.java)
                    val items = Moshi.Builder().build().adapter<List<PopsaImageData>>(listType).fromJson(IOUtils.toString(assets.open(it)))!!
                    updater.onNext(State(items))
                }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun close() {
        generator.dispose()
    }

    data class State (
            val images: List<PopsaImageData>
    )
}
