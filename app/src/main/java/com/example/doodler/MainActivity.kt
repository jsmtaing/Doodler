package com.example.doodler

import android.annotation.SuppressLint
import android.graphics.Color.HSVToColor
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.doodler.ui.theme.DoodlerTheme

class MainActivity : ComponentActivity() {

    private var brushSize by mutableIntStateOf(10)
    private var brushOpacity by mutableIntStateOf(255)
    private var brushColor by mutableIntStateOf(0xFF000000.toInt())

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DoodlerTheme {
                var showBrushSizeDialog by remember { mutableStateOf(false) }
                var showBrushOpacityDialog by remember { mutableStateOf(false) }
                var showColorPickerDialog by remember { mutableStateOf(false) }

                // DoodleView instance, retained using remember
                val doodleViewState = remember { mutableStateOf<DoodleView?>(null) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AndroidView(
                        factory = { _ ->
                            val layout = layoutInflater.inflate(R.layout.activity_main, null) as LinearLayout

                            val doodleView = layout.findViewById<DoodleView>(R.id.doodleView2)
                            doodleViewState.value = doodleView

                            // Initialize DoodleView properties
                            doodleView.setStrokeWidth(brushSize)
                            doodleView.setStrokeAlpha(brushOpacity)
                            doodleView.setStrokeColor(brushColor)

                            // Setup button click listeners
                            layout.findViewById<Button>(R.id.btn_size).setOnClickListener {
                                showBrushSizeDialog = true
                            }
                            layout.findViewById<Button>(R.id.btn_color).setOnClickListener {
                                showColorPickerDialog = true
                            }
                            layout.findViewById<Button>(R.id.btn_opacity).setOnClickListener {
                                showBrushOpacityDialog = true
                            }
                            layout.findViewById<Button>(R.id.btn_clear).setOnClickListener {
                                doodleView.clearCanvas()
                            }
                            layout.findViewById<Button>(R.id.btn_undo).setOnClickListener {
                                doodleView.undo()
                            }
                            layout.findViewById<Button>(R.id.btn_redo).setOnClickListener {
                                doodleView.redo()
                            }

                            layout
                        },
                        modifier = Modifier.padding(innerPadding)
                    )

                    // Update DoodleView properties on state changes
                    LaunchedEffect(brushSize, brushOpacity, brushColor) {
                        doodleViewState.value?.apply {
                            setStrokeWidth(brushSize)
                            setStrokeAlpha(brushOpacity)
                            setStrokeColor(brushColor)
                        }
                    }

                    // Dialogs for brush configuration
                    if (showBrushSizeDialog) {
                        BrushSizeDialog(
                            currentSize = brushSize,
                            onSizeChanged = {
                                brushSize = it
                                doodleViewState.value?.setStrokeWidth(it)
                            },
                            onDismiss = { showBrushSizeDialog = false }
                        )
                    }

                    if (showBrushOpacityDialog) {
                        BrushOpacityDialog(
                            currentOpacity = brushOpacity,
                            onOpacityChanged = {
                                brushOpacity = it
                                doodleViewState.value?.setStrokeAlpha(it)
                            },
                            onDismiss = { showBrushOpacityDialog = false }
                        )
                    }

                    if (showColorPickerDialog) {
                        ColorPickerDialog(
                            onColorPicked = {
                                brushColor = it
                                doodleViewState.value?.setStrokeColor(it)
                            },
                            onDismiss = { showColorPickerDialog = false }
                        )
                    }
                }
            }
        }
    }


    @Composable
    fun BrushSizeDialog(currentSize: Int, onSizeChanged: (Int) -> Unit, onDismiss: () -> Unit) {
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
                    currentValue = currentOpacity,
                    range = 0f..255f,
                    onValueChange = { opacity -> onOpacityChanged(opacity) }
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
        var hue by remember { mutableFloatStateOf(0f) }
        var saturation by remember { mutableFloatStateOf(1f) }
        var brightness by remember { mutableFloatStateOf(1f) }
        val color = HSVToColor(floatArrayOf(hue, saturation, brightness))

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Pick a Color") },
            text = {
                Column(modifier = Modifier.padding(16.dp)) {
                    //Hue
                    Slider(
                        value = hue,
                        onValueChange = { hue = it },
                        valueRange = 0f..360f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    //Saturation
                    Slider(
                        value = saturation,
                        onValueChange = { saturation = it },
                        valueRange = 0f..1f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    //Brightness
                    Slider(
                        value = brightness,
                        onValueChange = { brightness = it },
                        valueRange = 0f..1f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    //Preview
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color(color))
                            .padding(8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { onColorPicked(color); onDismiss() }) {
                    Text("Confirm")
                }
            }
        )
    }

    @Composable
    fun SeekBarWithValue(currentValue: Int, range: ClosedFloatingPointRange<Float>, onValueChange: (Int) -> Unit) {
        Column(modifier = Modifier.fillMaxWidth()) { // Ensure the column takes full width
            Text("Value: $currentValue")
            AndroidView(
                factory = { context ->
                    SeekBar(context).apply {
                        max = range.endInclusive.toInt()
                        progress = currentValue
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ) // Make SeekBar fill width
                        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                                if (fromUser) onValueChange(progress.toFloat().toInt())
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
