package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PrayerTimeCalculator
import com.example.data.model.NextPrayerInfo
import com.example.data.model.PrayerTimes
import com.example.ui.components.DedicationHeader
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    dedicationName: String,
    prayerTimes: PrayerTimes,
    nextPrayerInfo: NextPrayerInfo,
    selectedMethodId: Int,
    lastReadSurahName: String,
    onUpdateDedicationName: (String) -> Unit,
    onSelectMethod: (Int) -> Unit,
    onNavigateToQibla: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToAdhkar: () -> Unit,
    onNavigateToTasbeeh: () -> Unit,
    onNavigateToHadith: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMethodDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Dedication Header
        item {
            DedicationHeader(
                dedicationName = dedicationName,
                onUpdateName = onUpdateDedicationName
            )
        }

        // Hero Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = "مِشْكَاة - نور وهداية",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.75f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "مِشْكَاة - نُورٌ وَهُدَى",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldLight,
                                fontSize = 22.sp
                            )
                        )
                        Text(
                            text = "مهدى لروح $dedicationName • تطبيق إسلامي شامل",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }

        // Next Prayer & Date Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Hijri & Gregorian Date Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = prayerTimes.dateHijri,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = prayerTimes.dateGregorian,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldContainer,
                            modifier = Modifier.border(1.dp, GoldDark, RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = GoldDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = prayerTimes.city,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GoldDark,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Next Prayer Circular Banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "الصلاة القادمة: ${nextPrayerInfo.nameAr}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 17.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "متبقي: ${nextPrayerInfo.timeFormatted}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoldDark,
                                        fontSize = 24.sp
                                    )
                                )
                            }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(64.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { nextPrayerInfo.progressFraction },
                                    modifier = Modifier.fillMaxSize(),
                                    color = GoldDark,
                                    strokeWidth = 6.dp,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = GoldDark,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Shortcuts Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickShortcutCard(
                    title = "القبلة",
                    icon = Icons.Default.Explore,
                    onClick = onNavigateToQibla,
                    modifier = Modifier.weight(1f)
                )
                QuickShortcutCard(
                    title = "القرآن",
                    icon = Icons.Default.MenuBook,
                    onClick = onNavigateToQuran,
                    modifier = Modifier.weight(1f)
                )
                QuickShortcutCard(
                    title = "الأذكار",
                    icon = Icons.Default.FormatQuote,
                    onClick = onNavigateToAdhkar,
                    modifier = Modifier.weight(1f)
                )
                QuickShortcutCard(
                    title = "السبحة",
                    icon = Icons.Default.TouchApp,
                    onClick = onNavigateToTasbeeh,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Resume Reading Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigateToQuran() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GoldContainer),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GoldAccent, GoldDark)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = GoldDark,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "متابعة القراءة من آخر موضع",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldDark,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "سورة $lastReadSurahName",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        tint = GoldDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Daily Prayer Times Table
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مواقيت الصلاة اليومية",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 17.sp
                            )
                        )

                        TextButton(onClick = { showMethodDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = GoldDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "طريقة الحساب",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = GoldDark,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    PrayerRow("الفجر", prayerTimes.fajr, isNext = nextPrayerInfo.nameAr == "الفجر")
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    PrayerRow("الشروق", prayerTimes.shorooq, isNext = nextPrayerInfo.nameAr == "الشروق")
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    PrayerRow("الظهر", prayerTimes.dhuhr, isNext = nextPrayerInfo.nameAr == "الظهر")
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    PrayerRow("العصر", prayerTimes.asr, isNext = nextPrayerInfo.nameAr == "العصر")
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    PrayerRow("المغرب", prayerTimes.maghrib, isNext = nextPrayerInfo.nameAr == "المغرب")
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    PrayerRow("العشاء", prayerTimes.isha, isNext = nextPrayerInfo.nameAr == "العشاء")
                }
            }
        }

        // Daily Verse & Hadith Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onNavigateToHadith() },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "حديث اليوم من الأربعين النووية",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "«إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى...»",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "انقر لفتح الأحاديث الشريفة كاملاً",
                        style = MaterialTheme.typography.bodySmall.copy(color = GoldDark)
                    )
                }
            }
        }
    }

    // Calculation Method Selection Dialog
    if (showMethodDialog) {
        AlertDialog(
            onDismissRequest = { showMethodDialog = false },
            title = {
                Text(
                    text = "اختر طريقة حساب مواقيت الصلاة",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    PrayerTimeCalculator.calculationMethods.forEach { method ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectMethod(method.id)
                                    showMethodDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (method.id == selectedMethodId),
                                onClick = {
                                    onSelectMethod(method.id)
                                    showMethodDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = GoldDark)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = method.nameAr,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMethodDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
private fun PrayerRow(name: String, time: String, isNext: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isNext) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else Color.Transparent
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isNext) GoldDark else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
                    color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            )
        }

        Text(
            text = time,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = if (isNext) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = if (isNext) GoldDark else MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp
            )
        )
    }
}

@Composable
private fun QuickShortcutCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = GoldDark,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            )
        }
    }
}
