package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdhkarRepository
import com.example.data.db.DhikrProgressEntity
import com.example.data.model.DhikrItem
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdhkarScreen(
    progressList: List<DhikrProgressEntity>,
    onIncrementDhikr: (dhikrId: String, category: String, targetCount: Int) -> Unit,
    onResetCategory: (category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = AdhkarRepository.categories
    val selectedCategory = categories[selectedCategoryIndex]
    val dhikrItems = remember(selectedCategory) {
        AdhkarRepository.getAdhkarByCategory(selectedCategory)
    }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Scrollable Tab Row for Categories
        ScrollableTabRow(
            selectedTabIndex = selectedCategoryIndex,
            edgePadding = 8.dp,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedCategoryIndex]),
                    color = GoldDark,
                    height = 3.dp
                )
            },
            divider = {}
        ) {
            categories.forEachIndexed { index, categoryTitle ->
                Tab(
                    selected = selectedCategoryIndex == index,
                    onClick = { selectedCategoryIndex = index },
                    text = {
                        Text(
                            text = categoryTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedCategoryIndex == index) GoldDark else MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            )
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Header with Reset Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedCategory,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp
                )
            )

            OutlinedButton(
                onClick = { onResetCategory(selectedCategory) },
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(GoldDark, GoldAccent)))
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "إعادة ضبط العدادات",
                    tint = GoldDark,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("تصفير العدادات", style = MaterialTheme.typography.bodySmall.copy(color = GoldDark, fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Adhkar Cards List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dhikrItems, key = { it.id }) { item ->
                val progress = progressList.find { it.dhikrId == item.id }
                val currentCount = progress?.currentCount ?: 0
                val isCompleted = currentCount >= item.countTarget

                DhikrCard(
                    item = item,
                    currentCount = currentCount,
                    isCompleted = isCompleted,
                    onTap = {
                        onIncrementDhikr(item.id, item.category, item.countTarget)
                        vibratePhone(context)
                    }
                )
            }
        }
    }
}

@Composable
private fun DhikrCard(
    item: DhikrItem,
    currentCount: Int,
    isCompleted: Boolean,
    onTap: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isCompleted) BorderStroke(2.dp, GoldDark) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Dhikr Arabic Text
            Text(
                text = item.text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    lineHeight = 28.sp
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            if (item.virtue.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "الفضل: ${item.virtue}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GoldDark,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Counter Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isCompleted) "تَمَّ بِحَمْدِ اللَّهِ" else "التكرار المطلوب: ${item.countTarget}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) GoldDark else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Button(
                    onClick = onTap,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted) GoldDark else MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isCompleted) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مكتمل", color = Color.White, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.TouchApp, contentDescription = null, tint = GoldLight)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$currentCount / ${item.countTarget}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
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
            vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            @Suppress("DEPRECATION")
            vibrator.vibrate(40)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
