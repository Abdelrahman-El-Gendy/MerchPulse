package com.merchpulse.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun MerchPulseLogo(
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary
) {
    val brightBlue = Color(0xFF3B82F6) // Vibrant Blue from design
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Soft Glow Effect matching the new blue theme
        Canvas(modifier = Modifier.size(220.dp)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        brightBlue.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width / 2.2f
                )
            )
        }

        // Outer Container with corner accents (Crosshairs)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val strokeWidth = 1.5.dp.toPx()
            val cornerLen = w * 0.18f
            val alpha = 0.4f
            
            // Corner accents
            // Top-left
            drawLine(brightBlue.copy(alpha = alpha), start = Offset(0f, cornerLen), end = Offset(0f, 0f), strokeWidth = strokeWidth)
            drawLine(brightBlue.copy(alpha = alpha), start = Offset(0f, 0f), end = Offset(cornerLen, 0f), strokeWidth = strokeWidth)
            
            // Top-right
            drawLine(brightBlue.copy(alpha = alpha), start = Offset(w - cornerLen, 0f), end = Offset(w, 0f), strokeWidth = strokeWidth)
            drawLine(brightBlue.copy(alpha = alpha), start = Offset(w, 0f), end = Offset(w, cornerLen), strokeWidth = strokeWidth)
            
            // Bottom-right
            drawLine(brightBlue.copy(alpha = alpha), start = Offset(w, h - cornerLen), end = Offset(w, h), strokeWidth = strokeWidth)
            drawLine(brightBlue.copy(alpha = alpha), start = Offset(w, h), end = Offset(w - cornerLen, h), strokeWidth = strokeWidth)
            
            // Bottom-left
            drawLine(brightBlue.copy(alpha = alpha), start = Offset(cornerLen, h), end = Offset(0f, h), strokeWidth = strokeWidth)
            drawLine(brightBlue.copy(alpha = alpha), start = Offset(0f, h), end = Offset(0f, h - cornerLen), strokeWidth = strokeWidth)
        }

        // Central Rounded Box in Vibrant Bright Blue
        Surface(
            modifier = Modifier.fillMaxSize(0.82f),
            color = brightBlue,
            shape = RoundedCornerShape(40.dp),
            shadowElevation = 12.dp,
            tonalElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize(0.55f)) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f
                    val cy = h / 2f
                    val s = w * 0.45f
                    
                    // Isometric Cube Path
                    val cubePath = Path()
                    
                    // Top Face
                    cubePath.moveTo(cx, cy - s)
                    cubePath.lineTo(cx + s, cy - s * 0.5f)
                    cubePath.lineTo(cx, cy)
                    cubePath.lineTo(cx - s, cy - s * 0.5f)
                    cubePath.close()
                    
                    // Left Face
                    cubePath.moveTo(cx - s, cy - s * 0.5f)
                    cubePath.lineTo(cx, cy)
                    cubePath.lineTo(cx, cy + s)
                    cubePath.lineTo(cx - s, cy + s * 0.5f)
                    cubePath.close()
                    
                    // Right Face
                    cubePath.moveTo(cx + s, cy - s * 0.5f)
                    cubePath.lineTo(cx, cy)
                    cubePath.lineTo(cx, cy + s)
                    cubePath.lineTo(cx + s, cy + s * 0.5f)
                    cubePath.close()
                    
                    // Fill cube with soft white overlay to contrast with blue background
                    drawPath(
                        path = cubePath,
                        color = Color.White.copy(alpha = 0.15f),
                        style = Fill
                    )
                    
                    // Cube Outline Edges in sharp white
                    val edgeColor = Color.White.copy(alpha = 0.7f)
                    val edgeWidth = 1.2.dp.toPx()
                    
                    // Hexagon Exterior
                    drawLine(edgeColor, Offset(cx, cy - s), Offset(cx + s, cy - s * 0.5f), edgeWidth)
                    drawLine(edgeColor, Offset(cx + s, cy - s * 0.5f), Offset(cx + s, cy + s * 0.5f), edgeWidth)
                    drawLine(edgeColor, Offset(cx + s, cy + s * 0.5f), Offset(cx, cy + s), edgeWidth)
                    drawLine(edgeColor, Offset(cx, cy + s), Offset(cx - s, cy + s * 0.5f), edgeWidth)
                    drawLine(edgeColor, Offset(cx - s, cy + s * 0.5f), Offset(cx - s, cy - s * 0.5f), edgeWidth)
                    drawLine(edgeColor, Offset(cx - s, cy - s * 0.5f), Offset(cx, cy - s), edgeWidth)
                    
                    // Center connections
                    drawLine(edgeColor, Offset(cx, cy), Offset(cx, cy + s), edgeWidth)
                    drawLine(edgeColor, Offset(cx, cy), Offset(cx - s, cy - s * 0.5f), edgeWidth)
                    drawLine(edgeColor, Offset(cx, cy), Offset(cx + s, cy - s * 0.5f), edgeWidth)
                    
                    // White Pulse Line for maximum visibility and "seamless" feel on blue
                    val pulseY = cy
                    val pulsePath = Path()
                    pulsePath.moveTo(0f, pulseY)
                    pulsePath.lineTo(w * 0.25f, pulseY)
                    pulsePath.lineTo(w * 0.32f, pulseY + 12f)
                    pulsePath.lineTo(w * 0.40f, pulseY - h * 0.45f) // Spike UP
                    pulsePath.lineTo(w * 0.48f, pulseY + h * 0.45f) // Spike DOWN
                    pulsePath.lineTo(w * 0.55f, pulseY - 10f)
                    pulsePath.lineTo(w * 0.65f, pulseY)
                    pulsePath.lineTo(w, pulseY)
                    
                    drawPath(
                        path = pulsePath,
                        color = Color.White,
                        style = Stroke(
                            width = 3.5.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                    
                    // Highlight pulse dot (Indicator Cyan)
                    drawCircle(
                        color = Color(0xFF00FFFF), 
                        radius = 4.5.dp.toPx(),
                        center = Offset(w * 0.40f, pulseY - h * 0.45f)
                    )
                }
            }
        }
    }
}
