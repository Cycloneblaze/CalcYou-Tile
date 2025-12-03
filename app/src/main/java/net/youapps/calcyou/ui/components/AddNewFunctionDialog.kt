package net.youapps.calcyou.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    var showCheatSheet by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.add_new_function),
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { showCheatSheet = true }) {
                    Icon(
                        imageVector = Icons.Rounded.Help,
                        contentDescription = stringResource(R.string.syntax_help),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
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
                )
            )
            AnimatedVisibility(isError) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }

            // Function template chips
            Text(
                text = stringResource(R.string.quick_templates),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val templates = remember {
                listOf(
                    "sin(x)" to "sin(x)",
                    "x^2" to "x^2",
                    "sin(cos(x))" to "sin(cos(x))",
                    "sqrt(x^2+1)" to "sqrt(x^2+1)",
                    "e^(-x^2)" to "exp(-x^2)",
                    "ln(abs(x))" to "ln(abs(x))",
                    "tan(x/2)" to "tan(x/2)",
                    "cbrt(x)" to "cbrt(x)",
                    "cot(x)" to "cot(x)",
                    "1/x" to "1/x"
                )
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(templates, key = { it.first }) { (template, label) ->

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
        ColorSelectionDialog(
            initialColor = remember { color },
            onDismiss = { showColorPicker = false }) {
            color = it
        }
    }
    if (showCheatSheet) {
        SyntaxCheatSheetDialog(onDismiss = { showCheatSheet = false })
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SyntaxCheatSheetDialog(onDismiss: () -> Unit) {
    Dialog(onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
                .heightIn(max = 600.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.function_syntax_reference),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                }
                item {
                    CheatSheetSection(
                        title = stringResource(R.string.operators),
                        items = listOf(
                            "+" to stringResource(R.string.addition),
                            "-" to stringResource(R.string.subtraction),
                            "*" to stringResource(R.string.multiplication),
                            "/" to stringResource(R.string.division),
                            "^" to stringResource(R.string.exponentiation_x_2),
                            "%" to stringResource(R.string.modulo_remainder)
                        )
                    )
                }
                item {
                    CheatSheetSection(
                        title = stringResource(R.string.basic_functions),
                        items = listOf(
                            "abs(x)" to stringResource(R.string.absolute_value),
                            "sqrt(x)" to stringResource(R.string.square_root),
                            "cbrt(x)" to stringResource(R.string.cube_root),
                            "exp(x)" to stringResource(R.string.exponential_e_x),
                            "ln(x)" to stringResource(R.string.natural_logarithm),
                            "log10(x)" to stringResource(R.string.base_10_logarithm),
                            "log2(x)" to stringResource(R.string.base_2_logarithm),
                            "log(x; base)" to stringResource(R.string.logarithm_with_custom_base),
                            "pow(base; exp)" to stringResource(R.string.power_function)
                        )
                    )
                }

                item {
                    CheatSheetSection(
                        title = stringResource(R.string.trigonometric_functions),
                        items = listOf(
                            "sin(x), cos(x), tan(x)" to stringResource(R.string.basic_trig),
                            "asin(x), acos(x), atan(x)" to stringResource(R.string.inverse_trig),
                            "sinh(x), cosh(x), tanh(x)" to stringResource(R.string.hyperbolic),
                            "asinh(x), acosh(x), atanh(x)" to stringResource(R.string.inverse_hyperbolic),
                            "cot(x), sec(x), csc(x)" to stringResource(R.string.cotangent_secant_cosecant)
                        )
                    )
                }

                item {
                    CheatSheetSection(
                        title = stringResource(R.string.rounding_functions),
                        items = listOf(
                            "ceil(x)" to stringResource(R.string.round_up),
                            "floor(x)" to stringResource(R.string.round_down),
                            "round(x)" to stringResource(R.string.round_to_nearest),
                            "truncate(x)" to stringResource(R.string.remove_decimals)
                        )
                    )
                }
                item {
                    CheatSheetSection(
                        title = stringResource(R.string.advanced_functions),
                        items = listOf(
                            "fac(n)" to stringResource(R.string.factorial_n),
                            "min(a; b; ...)" to stringResource(R.string.minimum_value),
                            "max(a; b; ...)" to stringResource(R.string.maximum_value),
                            "sign(x)" to stringResource(R.string.sign_1_0_or_1),
                            "hypot(x; y)" to stringResource(R.string.hypotenuse_sqrt_x_y),
                            "gcd(a; b)" to stringResource(R.string.greatest_common_divisor),
                            "lcm(a; b)" to stringResource(R.string.least_common_multiple),
                            "clamp(val; min; max)" to stringResource(R.string.clamp_value),
                            "radians(deg)" to stringResource(R.string.degrees_to_radians),
                            "degrees(rad)" to stringResource(R.string.radians_to_degrees)
                        )
                    )
                }

                item {
                    CheatSheetSection(
                        title = stringResource(R.string.constants),
                        items = listOf(
                            "pi or π" to stringResource(R.string.pi_3_14159),
                            "e" to stringResource(R.string.euler_s_number_2_71828)
                        )
                    )
                }


                item {
                    CheatSheetSection(
                        title = stringResource(R.string.nested_functions),
                        items = listOf(
                            "sin(cos(x))" to stringResource(R.string.functions_inside_functions),
                            "sqrt(x^2 + 1)" to stringResource(R.string.combine_operations),
                            "exp(-x^2)" to stringResource(R.string.gaussian_like_curves),
                            "ln(abs(x))" to stringResource(R.string.avoid_domain_errors)
                        )
                    )
                }

                item {
                    Divider()
                }

                item {
                    Text(
                        text = stringResource(R.string.note_use_semicolons_to_separate_multiple_arguments),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    )
                }

                item {
                    Text(
                        text = stringResource(R.string.supports_nested_functions_like_sin_cos_x),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    )
                }


                item {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                    ) {
                        Text(text = stringResource(R.string.close))
                    }
                }

            }
        }
    }
}

@Composable
private fun CheatSheetSection(title: String, items: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        items.forEach { (syntax, description) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = syntax,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(0.5f)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.5f)
                )
            }
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
        errorMessage = stringResource(R.string.invalid_token_at_index_0),
        functionName = "f"
    )
}