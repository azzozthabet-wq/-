package com.example.ui.screens

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranRepository
import com.example.data.model.SurahDetail
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranDetailScreen(
    surahDetail: SurahDetail?,
    isLoading: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fontSizeSp by remember { mutableFloatStateOf(24f) }
    var isPlayingAudio by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(surahDetail?.number) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = surahDetail?.let { "سورة ${it.nameAr}" } ?: "قراءة القرآن",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        mediaPlayer?.stop()
                        onBack()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    // Audio Player Toggle
                    if (surahDetail != null) {
                        IconButton(onClick = {
                            if (isPlayingAudio) {
                                mediaPlayer?.pause()
                                isPlayingAudio = false
                            } else {
                                if (mediaPlayer == null) {
                                    val audioUrl = QuranRepository.getAudioUrlForSurah(surahDetail.number)
                                    mediaPlayer = MediaPlayer().apply {
                                        setAudioAttributes(
                                            AudioAttributes.Builder()
                                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                                .build()
                                        )
                                        setDataSource(audioUrl)
                                        prepareAsync()
                                        setOnPreparedListener {
                                            start()
                                            isPlayingAudio = true
                                        }
                                        setOnCompletionListener {
                                            isPlayingAudio = false
                                        }
                                    }
                                } else {
                                    mediaPlayer?.start()
                                    isPlayingAudio = true
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (isPlayingAudio) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = "تلاوة صوتية",
                                tint = GoldDark,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        if (isLoading || surahDetail == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = GoldDark)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("جاري تحميل آيات السورة الكريمة...")
                }
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Font Size Control Bar
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "حجم الخط",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = { if (fontSizeSp > 18f) fontSizeSp -= 2f }) {
                                Text("A-", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            }
                            Text(
                                text = "${fontSizeSp.toInt()}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            TextButton(onClick = { if (fontSizeSp < 36f) fontSizeSp += 2f }) {
                                Text("A+", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // Header Surah Title & Basmala
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "سورة ${surahDetail.nameAr}",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Text(
                                        text = "${surahDetail.revelationType} • ${surahDetail.numberOfAyahs} آية",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = GoldDark)
                                    )
                                }
                            }

                            if (surahDetail.number != 9) { // Except Surah At-Tawbah
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GoldDark,
                                        fontSize = (fontSizeSp + 2f).sp,
                                        fontFamily = FontFamily.Serif
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Verses List
                    itemsIndexed(surahDetail.ayahs) { index, ayah ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (index % 2 == 0) MaterialTheme.colorScheme.surface
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "${ayah.text} ﴿${arabicNumber(ayah.numberInSurah)}﴾",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = fontSizeSp.sp,
                                        lineHeight = (fontSizeSp * 1.8f).sp,
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontFamily = FontFamily.Serif
                                    ),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun arabicNumber(number: Int): String {
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val str = number.toString()
    val sb = StringBuilder()
    for (ch in str) {
        if (ch.isDigit()) {
            sb.append(arabicDigits[ch - '0'])
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}
