package com.github.radlance.autodispatch.request.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestCategories(
    selectedOption: String,
    filterOptions: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.clip(RoundedCornerShape(16.dp))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.height(TextFieldDefaults.MinHeight)
                .width(300.dp)
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .clip(RoundedCornerShape(16.dp))
                .background(SearchBarDefaults.colors().containerColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple()
                ) { }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterAlt,
                    contentDescription = null,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = selectedOption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(2f)
                )
                val trailingIcon = if (expanded) {
                    Icons.Outlined.ExpandLess
                } else Icons.Outlined.ExpandMore
                Icon(
                    imageVector = trailingIcon, contentDescription = null,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(16.dp)
        ) {
            filterOptions.forEach { optionName ->
                DropdownMenuItem(
                    text = {
                        Text(
                            optionName,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onOptionSelected(optionName)
                        expanded = false
                    },
                    trailingIcon = {
                        if (optionName == selectedOption) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null
                            )
                        }
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}