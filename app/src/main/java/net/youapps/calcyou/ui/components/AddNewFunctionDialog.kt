package net.youapps.calcyou.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import net.youapps.calcyou.R
import net.youapps.calcyou.viewmodels.GraphViewModel

@Composable
fun AddNewFunctionDialog(
    graphViewModel: GraphViewModel,
    functionName: String,
    initialExpression: String,
    initialColor: Color,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest) {
        DialogContent(
            onConfirm = { expression, color ->
                graphViewModel.addFunction(expression, color, functionName)
                onDismissRequest()
            },
            onCancel = onDismissRequest,
            checkExpression = graphViewModel::checkExpression,
            isError = graphViewModel.isError,
            errorMessage = graphViewModel.errorText,
            functionName = functionName,
            initialExpression = initialExpression,
            initialColor = initialColor
        )
    }
}

@Composable
private fun DialogContent(
    modifier: Modifier = Modifier,
    onConfirm: (expression: String, color: Color) -> Unit,
    onCancel: () -> Unit,
    checkExpression: (String) -> Unit,
    isError: Boolean,
    errorMessage: String,
    functionName: String,
    initialExpression: String = "",
    initialColor: Color = rainbowColors.first(),
) {
    var color by remember { mutableStateOf(initialColor) }
    var showColorPicker by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(R.string.add_new_function),
                style = MaterialTheme.typography.titleLarge
            )
            var text by remember { mutableStateOf(initialExpression) }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = {
                    text = it
                    checkExpression(it)
                },
                isError = isError,
                prefix = {
                    Text(
                        text = "${functionName}(x) = ", style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            fontStyle = FontStyle.Italic
                        )
                    )
                },
                trailingIcon = {
                    Box(
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable {
                                showColorPicker = true
                            })
                },
                textStyle = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontStyle = FontStyle.Italic
                ),
                supportingText = {
                    Text(
                        text = "Supports nested functions like sin(cos(x))",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
            AnimatedVisibility(isError) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }

            // Function template chips
            Text(
                text = "Quick Templates:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val templates = listOf(
                    "sin(x)" to "sin(x)",
                    "x^2" to "x^2",
                    "sin(cos(x))" to "sin(cos(x))",
                    "sqrt(x^2+1)" to "sqrt(x^2+1)",
                    "e^(-x^2)" to "exp(-x^2)",
                    "ln(abs(x))" to "ln(abs(x))",
                    "tan(x/2)" to "tan(x/2)"
                )

                templates.forEach { (label, template) ->
                    AssistChip(
                        onClick = {
                            text = template
                            checkExpression(template)
                        },
                        label = {
                            Text(
                                text = label,
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    )
                }
            }
            Row(Modifier.align(Alignment.End)) {
                TextButton(onClick = onCancel) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
                TextButton(onClick = {
                    onConfirm(text, color)
                }, enabled = !isError) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            }
        }
    }
    if (showColorPicker) {
        ColorSelectionDialog(initialColor = remember { color },
            onDismiss = { showColorPicker = false }) {
            color = it
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun DialogContentPreview() {
    DialogContent(
        onConfirm = { _, _ -> },
        onCancel = {},
        checkExpression = {},
        isError = true,
        errorMessage = "Invalid token at index 0",
        functionName = "f"
    )
}