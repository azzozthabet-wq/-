package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbeehScreen(
    totalTasbeehCount: Int,
    onIncrementTasbeeh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasbeehPhrases = remember {
        listOf(
            "سُبْحَانَ اللَّهِ",
            "الْحَمْدُ لِلَّهِ",
            "اللَّهُ أَكْبَرُ",
            "لا إِلَهَ إِلا اللَّهُ",
            "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
            "لا حَوْلَ وَلا قُوَّةَ إِلا بِاللَّهِ",
            "صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ"
        )
    }

    var selectedPhraseIndex by remember { mutableIntStateOf(0) }
    var sessionCount by remember { mutableIntStateOf(0) }
    var targetCount by remember { mutableIntStateOf(33) }
    var isPressed by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = 500f),
        label = "scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(8.dp))

            // Phrase Selector Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tasbeehPhrases.size) { idx ->
                    FilterChip(
                        selected = selectedPhraseIndex == idx,
                        onClick = {
                            selectedPhraseIndex = idx
                            sessionCount = 0
                        },
                        label = {
                            Text(
                                text = tasbeehPhrases[idx],
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display Active Selected Phrase Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tasbeehPhrases[selectedPhraseIndex],
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 24.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Circular Interactive Electronic Tasbeeh Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .scale(buttonScale)
                .size(240.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            GoldLight,
                            GoldDark
                        )
                    )
                )
                .clickable {
                    sessionCount++
                    onIncrementTasbeeh()
                    vibratePhone(context)
                }
                .border(8.dp, Color.White, CircleShape)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$sessionCount",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 54.sp
                    )
                )
                Text(
                    text = "من $targetCount",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = "اضغط للتسبيح",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Target Selector & Total Stats Row
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { targetCount = 33 },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (targetCount == 33) GoldContainer else Color.Transparent
                    )
                ) {
                    Text("33", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { targetCount = 100 },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (targetCount == 100) GoldContainer else Color.Transparent
                    )
                ) {
                    Text("100", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { targetCount = 1000 },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (targetCount == 1000) GoldContainer else Color.Transparent
                    )
                ) {
                    Text("1000", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "إجمالي التسبيحات الكلي",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = "$totalTasbeehCount تسبيحة",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldDark
                            )
                        )
                    }

                    IconButton(
                        onClick = { sessionCount = 0 },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(GoldDark)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "تصفير الجلسة",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun vibratePhone(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            @Suppress("DEPRECATION")
            vibrator.vibrate(35)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
