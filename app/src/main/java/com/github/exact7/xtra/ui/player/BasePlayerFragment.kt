package com.github.exact7.xtra.ui.player

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.exact7.xtra.di.Injectable
import com.github.exact7.xtra.ui.view.draggableview.DraggableListener
import com.github.exact7.xtra.ui.view.draggableview.DraggableView
import com.github.exact7.xtra.util.LifecycleListener
import kotlinx.android.synthetic.main.player_stream.*
import javax.inject.Inject

@Suppress("PLUGIN_WARNING")
abstract class BasePlayerFragment : Fragment(), Injectable, LifecycleListener {

//    private var channelListener: OnChannelClickedListener? = null
    private var dragListener: DraggableListener? = null
    private lateinit var draggableView: DraggableView
    protected abstract val viewModel: PlayerViewModel
    protected var isPortraitOrientation: Boolean = false
        private set

    @Inject
    protected lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnChannelClickedListener) {
//            channelListener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnViewChannelClickedListener")
//        }
        if (context is DraggableListener) {
            dragListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement DraggableListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isPortraitOrientation = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.keepScreenOn = true
        val activity = requireActivity()

        if (isPortraitOrientation) {
            draggableView = view as DraggableView
            draggableView.setDraggableListener(dragListener)
            minimize.setOnClickListener { minimize() }
            fullscreenEnter.setOnClickListener { activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
        } else {
            activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
            //            slidingView = view.findViewById(R.id.fragment_player_sv);
            fullscreenExit.setOnClickListener {
                activity.apply {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }

            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (!isPortraitOrientation) {
            val activity = requireActivity()
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
//        channelListener = null
        dragListener = null
    }

    override fun onMovedToForeground() {
//        viewModel.play()
    }

    override fun onMovedToBackground() {
        viewModel.player.stop()
    }

    abstract fun play(obj: Parcelable)

    fun minimize() {
        if (isPortraitOrientation) {
            draggableView.minimize()
        } else {
//                        slidingView.minimize();
        }
    }
}
