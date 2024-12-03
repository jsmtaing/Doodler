package com.example.doodler

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.doodler.ui.theme.DoodlerTheme



class MainActivity : ComponentActivity() {

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DoodlerTheme {
                var showBrushSizeDialog by remember { mutableStateOf(false) }
                var showBrushOpacityDialog by remember { mutableStateOf(false) }
                var showColorPickerDialog by remember { mutableStateOf(false) }
                var brushSize by remember { mutableFloatStateOf(10f) }
                var brushOpacity by remember { mutableIntStateOf(255) }
                var brushColor by remember { mutableIntStateOf(0xFF000000.toInt()) }

                val doodleView: DoodleView? by remember { mutableStateOf<DoodleView?>(null) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AndroidView(
                        factory = { context ->
                            val layout =
                                layoutInflater.inflate(R.layout.activity_main, null) as LinearLayout

                            val doodleViewInstance = layout.findViewById<DoodleView>(R.id.doodleView2)
                            //doodleView.value = doodleViewInstance

                            // Find buttons and assign click listeners
                            val btnSize = layout.findViewById<Button>(R.id.btn_size)
                            val btnColor = layout.findViewById<Button>(R.id.btn_color)
                            val btnOpacity = layout.findViewById<Button>(R.id.btn_opacity)
                            val btnClear = layout.findViewById<Button>(R.id.btn_clear)

                            btnSize.setOnClickListener { showBrushSizeDialog = true }
                            btnColor.setOnClickListener { showColorPickerDialog = true }
                            btnOpacity.setOnClickListener { showBrushOpacityDialog = true }
                            btnClear.setOnClickListener { doodleViewInstance.clearCanvas() }

                            doodleViewInstance.setStrokeWidth(brushSize)
                            doodleViewInstance.setStrokeAlpha(brushOpacity)
                            doodleViewInstance.setStrokeColor(brushColor)

                            layout
                        },
                        modifier = Modifier.padding(innerPadding)
                    )

                    LaunchedEffect(brushSize, brushOpacity, brushColor) {
                        doodleView?.apply {
                            setStrokeWidth(brushSize)
                            setStrokeAlpha(brushOpacity)
                            setStrokeColor(brushColor)
                        }
                    }

                    // Display dialogs as needed
                    if (showBrushSizeDialog) {
                        BrushSizeDialog(
                            currentSize = brushSize,
                            onSizeChanged = { newSize ->
                                brushSize = newSize
                                doodleView?.setStrokeWidth(newSize)
                            },
                            onDismiss = { showBrushSizeDialog = false }
                        )
                    }

                    if (showBrushOpacityDialog) {
                        BrushOpacityDialog(
                            currentOpacity = brushOpacity,
                            onOpacityChanged = { newOpacity ->
                                brushOpacity = newOpacity
                                doodleView?.setStrokeAlpha(newOpacity)
                            },
                            onDismiss = { showBrushOpacityDialog = false }
                        )
                    }

                    if (showColorPickerDialog) {
                        ColorPickerDialog(
                            onColorPicked = { newColor ->
                                brushColor = newColor
                                doodleView?.setStrokeColor(newColor)
                            },
                            onDismiss = { showColorPickerDialog = false }
                        )
                    }
                }
            }
        }
    }


    @Composable
    fun BrushSizeDialog(currentSize: Float, onSizeChanged: (Float) -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Brush Size") },
            text = {
                SeekBarWithValue(
                    currentValue = currentSize,
                    range = 1f..50f,
                    onValueChange = onSizeChanged
                )
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
    }

    @Composable
    fun BrushOpacityDialog(currentOpacity: Int, onOpacityChanged: (Int) -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Brush Opacity") },
            text = {
                SeekBarWithValue(
                    currentValue = currentOpacity.toFloat(),
                    range = 0f..255f,
                    onValueChange = { opacity -> onOpacityChanged(opacity.toInt()) }
                )
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
    }

    @Composable
    fun ColorPickerDialog(onColorPicked: (Int) -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Pick a Color") },
            text = {
                // Replace with a proper color picker composable
                Text("Color Picker goes here")
            },
            confirmButton = {
                TextButton(onClick = { onColorPicked(0xFF000000.toInt()); onDismiss() }) {
                    Text("Confirm")
                }
            }
        )
    }

    @Composable
    fun SeekBarWithValue(currentValue: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
        Column(modifier = Modifier.fillMaxWidth()) { // Ensure the column takes full width
            Text("Value: ${currentValue.toInt()}")
            AndroidView(
                factory = { context ->
                    SeekBar(context).apply {
                        max = range.endInclusive.toInt()
                        progress = currentValue.toInt()
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ) // Make SeekBar fill width
                        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                                if (fromUser) onValueChange(progress.toFloat())
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                        })
                    }
                },
                modifier = Modifier.fillMaxWidth() // Ensure SeekBar fills width in Compose
            )
        }
    }



}
