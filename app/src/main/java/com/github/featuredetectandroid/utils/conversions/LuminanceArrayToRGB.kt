package com.github.featuredetectandroid.utils.conversions

private const val NUMBER_OF_COMPONENTS = 3

fun luminanceArrayToRGB(luminanceByteArray: ByteArray) = ByteArray(
    NUMBER_OF_COMPONENTS * luminanceByteArray.size
) { luminanceByteArray[it / NUMBER_OF_COMPONENTS] }
