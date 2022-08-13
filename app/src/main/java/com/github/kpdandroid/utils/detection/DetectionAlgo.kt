package com.github.kpdandroid.utils.detection

import android.content.Context
import com.github.kpdlib.KeypointDetector
import com.github.kpdlib.learned.SuperPoint
import com.github.kpdlib.traditional.Orb
import com.github.kpdlib.traditional.Sift
import com.github.kpdlib.traditional.Surf

enum class DetectionAlgo(val title: String) {
    NONE("None"),
    SIFT("SIFT"),
    SURF("SURF"),
    ORB("ORB"),
    SUPERPOINT("SuperPoint");

    companion object {
        val titles = values().map { it.title }

        fun from(detector: KeypointDetector?) = when (detector) {
            is Sift -> SIFT
            is Surf -> SURF
            is Orb -> ORB
            is SuperPoint -> SUPERPOINT
            null -> NONE
            else -> null
        }

        fun constructDetectorFrom(
            context: Context,
            algorithmName: String,
            width: Int,
            height: Int
        ) = when (algorithmName) {
            SIFT.title -> Sift(width, height)
            SURF.title -> Surf(width, height)
            ORB.title -> Orb(width, height)
            SUPERPOINT.title -> SuperPoint.Builder(context, width, height).build()
            else -> null
        }
    }
}
