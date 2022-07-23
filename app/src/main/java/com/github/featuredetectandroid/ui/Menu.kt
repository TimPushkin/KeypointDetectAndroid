package com.github.featuredetectandroid.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Menu(
    header: String,
    options: List<String>,
    selectedOption: String,
    onSelected: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = header,
            modifier = Modifier.padding(all = 20.dp),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.h6
        )
    }

    options.forEach { option ->
        Row(
            Modifier
                .fillMaxWidth()
                .selectable(
                    selected = (option == selectedOption),
                    onClick = { onSelected(option) }
                ),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = CenterVertically
        ) {
            RadioButton(
                selected = (option == selectedOption),
                modifier = Modifier.padding(
                    vertical = 10.dp,
                    horizontal = 20.dp
                ),
                onClick = null
            )
            Text(
                text = option,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.body1
            )
        }
    }
}
