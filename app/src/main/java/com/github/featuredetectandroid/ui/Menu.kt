package com.github.featuredetectandroid.ui

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.github.featuredetectandroid.utils.stringToAlgorithmMap

private const val TAG = "Menu"

@Composable
fun Menu(currentKeypointExtractionMethod: SharedPreferences) {
    val radioOptions = stringToAlgorithmMap().keys.toList()
    var selectedAlgorithm by remember {
        mutableStateOf(currentKeypointExtractionMethod.getString("method", "None"))
    }

    Row(horizontalArrangement = Arrangement.Center) {
        Text(
            text = "Keypoint detection algorithm:",
            modifier = Modifier.padding(all = 20.dp).align(Bottom),
            textAlign = TextAlign.Start,
            fontSize = 20.sp
        )
    }
    radioOptions.forEach { text ->
        Row(
            Modifier.fillMaxWidth().selectable(
                selected = (text == selectedAlgorithm),
                onClick = {
                    currentKeypointExtractionMethod.edit {
                        putString("method", text)
                        apply()
                    }
                    selectedAlgorithm = text
                    Log.i(
                        TAG,
                        "${
                        currentKeypointExtractionMethod.getString(
                            "method",
                            "None"
                        )
                        } was selected."
                    )
                }
            ),
            horizontalArrangement = Arrangement.Start
        ) {
            RadioButton(
                selected = (text == selectedAlgorithm),
                modifier = Modifier.padding(horizontal = 8.dp),
                onClick = {
                    currentKeypointExtractionMethod.edit {
                        putString("method", text)
                        apply()
                    }
                    selectedAlgorithm = text
                }
            )
            Text(
                text = text,
                modifier = Modifier.align(CenterVertically),
                textAlign = TextAlign.Start
            )
        }
    }
}
