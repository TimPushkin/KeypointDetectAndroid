package com.github.kpdandroid.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomMenuItem(
    options: List<String>,
    initialOption: String,
    onSelected: (String) -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(initialOption) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            leadingIcon?.let { icon ->
                IconButton( // Required to have the same offsets as the trailing icon has
                    onClick = {},
                    content = icon
                )
            }
            Text(selectedOption)
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for (option in options) {
                DropdownMenuItem(
                    onClick = {
                        onSelected(option)
                        selectedOption = option
                        expanded = false
                    }
                ) {
                    Text(option)
                }
            }
        }
    }
}
