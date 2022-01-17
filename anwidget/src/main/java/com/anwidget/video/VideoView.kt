package com.anwidget.video

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView

class VideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : PlayerView(context, attrs, defStyleAttr) {

    val coverView: ImageView

    private val coverListener: Player.Listener

    init {
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        coverView = ImageView(context)
        addView(coverView, params)

        coverListener = object : Player.Listener {
            override fun onRenderedFirstFrame() {
                postDelayed({
                    coverView.visibility = View.GONE
                }, 500)
            }
        }
    }


    override fun setPlayer(player: Player?) {
        if (null == player) {
            coverView.visibility = VISIBLE
            this.player?.removeListener(coverListener)
        } else {
            coverView.visibility = GONE
            player.addListener(coverListener)
        }
        super.setPlayer(player)
    }

}