package com.github.featuredetectandroid.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.featuredetectandroid.ui.theme.Typography
import com.github.featuredetectandroid.utils.KeypointDetectionAlgorithm

@Composable
fun Menu(currentAlgorithm: String, onAlgorithmSelected: (String) -> Unit) {
    val radioOptions = KeypointDetectionAlgorithm.names
    var selectedAlgorithm by remember { mutableStateOf(currentAlgorithm) }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = "Keypoint detection algorithm:",
            modifier = Modifier.padding(all = 20.dp),
            textAlign = TextAlign.Start,
            style = Typography.body1
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
                            changeSelectedAlgorithmInPreferences = onAlgorithmSelected,
                            onClickSelectAlgorithm = { selectedAlgorithm = text },
                            buttonText = text
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
                        changeSelectedAlgorithmInPreferences = onAlgorithmSelected,
                        onClickSelectAlgorithm = { selectedAlgorithm = text },
                        buttonText = text
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
    changeSelectedAlgorithmInPreferences: (String) -> Unit,
    onClickSelectAlgorithm: (String) -> Unit,
    buttonText: String
) {
    changeSelectedAlgorithmInPreferences(buttonText)
    onClickSelectAlgorithm(buttonText)
}
