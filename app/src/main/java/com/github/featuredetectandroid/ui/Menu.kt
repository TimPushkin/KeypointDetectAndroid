package com.github.featuredetectandroid.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.featuredetectandroid.utils.stringToAlgorithmMap

@Composable
fun Menu(currentAlgorithm: String, changeSelectedAlgorithmIntPreferences: (String) -> Unit) {
    val radioOptions = stringToAlgorithmMap().keys.toList()
    val (selectedAlgorithm, onAlgorithmSelection) = remember { mutableStateOf(currentAlgorithm) }

    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom) {
        Text(
            text = "Keypoint detection algorithm:",
            modifier = Modifier.padding(all = 20.dp),
            textAlign = TextAlign.Start,
            fontSize = 20.sp
        )
    }
    radioOptions.forEach { text ->
        Row(
            Modifier
                .fillMaxWidth()
                .selectable(
                    selected = (text == selectedAlgorithm),
                    onClick = {
                        onClickChangeSelectedAlgorithm(
                            changeSelectedAlgorithmIntPreferences,
                            onAlgorithmSelection,
                            text
                        )
                    }
                ),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = CenterVertically
        ) {
            RadioButton(
                selected = (text == selectedAlgorithm),
                modifier = Modifier.padding(horizontal = 8.dp),
                onClick = {
                    onClickChangeSelectedAlgorithm(
                        changeSelectedAlgorithmIntPreferences,
                        onAlgorithmSelection,
                        text
                    )
                }
            )
            Text(
                text = text,
                textAlign = TextAlign.Start
            )
        }
    }
}

fun onClickChangeSelectedAlgorithm(
    changeSelectedAlgorithmIntPreferences: (String) -> Unit,
    onAlgorithmSelection: (String) -> Unit,
    buttonText: String
) {
    changeSelectedAlgorithmIntPreferences(buttonText)
    onAlgorithmSelection(buttonText)
}
