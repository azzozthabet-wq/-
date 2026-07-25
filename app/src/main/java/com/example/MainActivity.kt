package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.ui.screens.*
import com.example.ui.theme.GoldDark
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MishkatViewModel

enum class NavigationItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", "الرئيسية", Icons.Filled.Home, Icons.Outlined.Home),
    QIBLA("qibla", "القبلة", Icons.Filled.Explore, Icons.Outlined.Explore),
    QURAN("quran", "القرآن", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    ADHKAR("adhkar", "الأذكار", Icons.Filled.FormatQuote, Icons.Outlined.FormatQuote),
    TASBEEH("tasbeeh", "السبحة", Icons.Filled.TouchApp, Icons.Outlined.TouchApp)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MishkatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                // Ensure RTL layout for Arabic
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    MishkatApp(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MishkatApp(viewModel: MishkatViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val prayerTimes by viewModel.prayerTimes.collectAsStateWithLifecycle()
    val nextPrayerInfo by viewModel.nextPrayerInfo.collectAsStateWithLifecycle()
    val qiblaBearing by viewModel.qiblaBearing.collectAsStateWithLifecycle()
    val dhikrProgressList by viewModel.dhikrProgress.collectAsStateWithLifecycle()
    val selectedSurahDetail by viewModel.selectedSurahDetail.collectAsStateWithLifecycle()
    val isLoadingSurah by viewModel.isLoadingSurah.collectAsStateWithLifecycle()

    val showBottomBar = currentRoute != "quran_detail"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationItem.entries.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title,
                                    tint = if (isSelected) GoldDark else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 12.sp,
                                    color = if (isSelected) GoldDark else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.HOME.route) {
                HomeScreen(
                    dedicationName = userPreferences.dedicationName,
                    prayerTimes = prayerTimes,
                    nextPrayerInfo = nextPrayerInfo,
                    selectedMethodId = userPreferences.calculationMethod,
                    lastReadSurahName = userPreferences.lastSurahName,
                    onUpdateDedicationName = { viewModel.updateDedicationName(it) },
                    onSelectMethod = { viewModel.updateCalculationMethod(it) },
                    onNavigateToQibla = { navController.navigate(NavigationItem.QIBLA.route) },
                    onNavigateToQuran = { navController.navigate(NavigationItem.QURAN.route) },
                    onNavigateToAdhkar = { navController.navigate(NavigationItem.ADHKAR.route) },
                    onNavigateToTasbeeh = { navController.navigate(NavigationItem.TASBEEH.route) },
                    onNavigateToHadith = { navController.navigate("hadith") }
                )
            }

            composable(NavigationItem.QIBLA.route) {
                QiblaScreen(
                    qiblaBearing = qiblaBearing,
                    cityName = userPreferences.currentCity
                )
            }

            composable(NavigationItem.QURAN.route) {
                QuranScreen(
                    lastReadSurahNumber = userPreferences.lastSurahNumber,
                    lastReadSurahName = userPreferences.lastSurahName,
                    onSelectSurah = { surahNumber ->
                        viewModel.loadSurah(surahNumber)
                        navController.navigate("quran_detail")
                    }
                )
            }

            composable("quran_detail") {
                QuranDetailScreen(
                    surahDetail = selectedSurahDetail,
                    isLoading = isLoadingSurah,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavigationItem.ADHKAR.route) {
                AdhkarScreen(
                    progressList = dhikrProgressList,
                    onIncrementDhikr = { dhikrId, cat, target ->
                        viewModel.incrementDhikr(dhikrId, cat, target)
                    },
                    onResetCategory = { cat ->
                        viewModel.resetCategoryDhikr(cat)
                    }
                )
            }

            composable(NavigationItem.TASBEEH.route) {
                TasbeehScreen(
                    totalTasbeehCount = userPreferences.totalTasbeehCount,
                    onIncrementTasbeeh = { viewModel.incrementTasbeeh() }
                )
            }

            composable("hadith") {
                HadithScreen()
            }
        }
    }
}
