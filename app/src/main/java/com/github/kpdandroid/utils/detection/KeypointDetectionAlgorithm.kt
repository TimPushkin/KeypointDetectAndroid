package com.github.kpdandroid.utils.detection

import android.content.Context
import com.github.kpdlib.learned.SuperPoint
import com.github.kpdlib.traditional.Orb
import com.github.kpdlib.traditional.Sift
import com.github.kpdlib.traditional.Surf

enum class KeypointDetectionAlgorithm(val formattedName: String) {
    NONE("None"),
    SIFT("SIFT"),
    SURF("SURF"),
    ORB("ORB"),
    SUPERPOINT("SuperPoint");

    companion object {
        val names = values().map { it.formattedName }

        fun constructDetectorFrom(
            context: Context,
            algorithmName: String,
            width: Int,
            height: Int
        ) = when (algorithmName) {
            SIFT.formattedName -> Sift(width, height)
            SURF.formattedName -> Surf(width, height)
            ORB.formattedName -> Orb(width, height)
            SUPERPOINT.formattedName -> SuperPoint.Builder(context, width, height).build()
            else -> null
        }
    }
}
