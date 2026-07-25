package com.example.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import kotlin.math.roundToInt

@Composable
fun QiblaScreen(
    qiblaBearing: Double,
    cityName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var azimuth by remember { mutableFloatStateOf(0f) }
    var hasSensor by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (accelerometer == null || magnetometer == null) {
            hasSensor = false
        }

        var gravity: FloatArray? = null
        var geomagnetic: FloatArray? = null

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    gravity = event.values
                }
                if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    geomagnetic = event.values
                }
                if (gravity != null && geomagnetic != null) {
                    val R = FloatArray(9)
                    val I = FloatArray(9)
                    if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(R, orientation)
                        azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                        if (azimuth < 0) azimuth += 360f
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (hasSensor) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            if (hasSensor) {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    // Animated Smooth Rotation Angle
    val animatedAzimuth by animateFloatAsState(
        targetValue = azimuth,
        animationSpec = spring(stiffness = 300f),
        label = "azimuth"
    )

    val qiblaRelativeAngle = (qiblaBearing.toFloat() - animatedAzimuth + 360f) % 360f

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = GoldDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "موقعك الحالي: $cityName",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Text(
                text = "اتجه نحو الكعبة المشرفة",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldDark,
                    fontSize = 22.sp
                ),
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "زاویة القبلة: ${qiblaBearing.roundToInt()}° من الشمال",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // Circular Interactive Compass Dial
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(300.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .border(6.dp, GoldDark, CircleShape)
        ) {
            // Compass Dial Plate Rotating with Azimuth
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(-animatedAzimuth)
            ) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2 - 16.dp.toPx()

                // Draw Outer Ring Ticks
                for (i in 0 until 360 step 15) {
                    val angleRad = Math.toRadians(i.toDouble())
                    val tickLength = if (i % 90 == 0) 18.dp.toPx() else 10.dp.toPx()
                    val start = Offset(
                        (center.x + (radius - tickLength) * Math.sin(angleRad)).toFloat(),
                        (center.y - (radius - tickLength) * Math.cos(angleRad)).toFloat()
                    )
                    val end = Offset(
                        (center.x + radius * Math.sin(angleRad)).toFloat(),
                        (center.y - radius * Math.cos(angleRad)).toFloat()
                    )
                    drawLine(
                        color = if (i % 90 == 0) GoldDark else GoldAccent.copy(alpha = 0.5f),
                        start = start,
                        end = end,
                        strokeWidth = if (i % 90 == 0) 3.dp.toPx() else 1.5.dp.toPx()
                    )
                }

                // Draw Kaaba Pointer Indicator
                val qiblaRad = Math.toRadians(qiblaBearing)
                val kaabaX = (center.x + (radius - 30.dp.toPx()) * Math.sin(qiblaRad)).toFloat()
                val kaabaY = (center.y - (radius - 30.dp.toPx()) * Math.cos(qiblaRad)).toFloat()

                drawCircle(
                    color = GoldDark,
                    radius = 16.dp.toPx(),
                    center = Offset(kaabaX, kaabaY)
                )
            }

            // Fixed Kaaba Icon & Qibla Direction Arrow Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(qiblaRelativeAngle),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = (-80).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "اتجاه الكعبة",
                        tint = GoldDark,
                        modifier = Modifier.size(48.dp)
                    )
                    Surface(
                        color = GoldDark,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "الكعبة المشرفة",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Center Compass Cap
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(GoldDark)
                    .border(2.dp, Color.White, CircleShape)
            )
        }

        // Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!hasSensor) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CompassCalibration,
                            contentDescription = null,
                            tint = GoldDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تنبيه: حساس البوصلة غير مدعوم في الجهاز، يتم عرض زاوية القبلة حسابياً.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Text(
                        text = "قم ببرم بوصلة جهازك حتى يتطابق السهم الذهبي مع أعلى الدائرة للمحاذاة المباشرة للقبلة.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
