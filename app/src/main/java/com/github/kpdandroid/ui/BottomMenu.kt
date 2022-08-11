package com.github.kpdandroid.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomAppBar
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun UnitedBottomMenu(
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    items: @Composable RowScope.() -> Unit
) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = Alignment.CenterVertically,
            content = items
        )
    }
}

@Composable
fun DualBottomMenu(
    startItems: @Composable RowScope.() -> Unit,
    endItems: @Composable RowScope.() -> Unit,
    spacing: Dp = 0.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start
) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = horizontalArrangement,
                verticalAlignment = Alignment.CenterVertically,
                content = startItems
            )

            Spacer(
                modifier = Modifier.width(spacing)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = horizontalArrangement,
                verticalAlignment = Alignment.CenterVertically,
                content = endItems
            )
        }
    }
}

private val MIN_LEADING_ICON_SIZE = 48.dp

@Composable
fun BottomMenuItem(
    title: String,
    leadingIcon: (@Composable BoxScope.() -> Unit)? = null,
    onClicked: () -> Unit
) {
    Row(
        modifier = Modifier.clickable(
            role = Role.Button,
            onClick = onClicked
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon?.let { icon ->
            Box(
                modifier = Modifier.requiredSize(MIN_LEADING_ICON_SIZE),
                contentAlignment = Alignment.Center,
                content = icon
            )
        }

        Text(title)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExpandableBottomMenuItem(
    options: List<String>,
    selectedOption: String,
    leadingIcon: (@Composable BoxScope.() -> Unit)? = null,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            leadingIcon?.let { icon ->
                Box(
                    modifier = Modifier.requiredSize(MIN_LEADING_ICON_SIZE),
                    contentAlignment = Alignment.Center,
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
                        expanded = false
                    }
                ) {
                    Text(option)
                }
            }
        }
    }
}

@Composable
fun ExpandableBottomMenuItem(
    title: String,
    leadingIcon: (@Composable BoxScope.() -> Unit)? = null,
    optionsWithAction: List<Pair<String, () -> Unit>>
) {
    ExpandableBottomMenuItem(
        options = optionsWithAction.map { it.first },
        selectedOption = title,
        onSelected = { option -> optionsWithAction.find { it.first == option }?.run { second() } },
        leadingIcon = leadingIcon
    )
}
