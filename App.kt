package com.cyberlab.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cyberlab.app.data.Store

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val TABS = listOf(
    Tab("learn", "Learn", Icons.Filled.School),
    Tab("labs", "Labs", Icons.Filled.Science),
    Tab("tools", "Tools", Icons.Filled.Build),
    Tab("secure", "Secure", Icons.Filled.VerifiedUser),
    Tab("profile", "Profile", Icons.Filled.Person)
)

@Composable
private fun FloatingNav(modifier: Modifier, route: String?, nav: NavHostController) {
    Row(
        modifier
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .fillMaxWidth().height(64.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xF2131622))
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TABS.forEach { t ->
            val sel = route == t.route
            Row(
                Modifier.clip(CircleShape)
                    .background(if (sel) C.Red.copy(alpha = .18f) else Color.Transparent)
                    .clickable {
                        nav.navigate(t.route) {
                            popUpTo("learn") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    .padding(horizontal = if (sel) 16.dp else 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(t.icon, null, tint = if (sel) C.Red else C.Muted)
                AnimatedVisibility(sel) {
                    Text(t.label, color = C.Red, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 6.dp))
                }
            }
        }
    }
}

@Composable
private fun AppContent(store: Store) {
    val nav = rememberNavController()
    val backEntry by nav.currentBackStackEntryAsState()
    val route = backEntry?.destination?.route
    val showBar = TABS.any { it.route == route }

    Box(Modifier.fillMaxSize().background(C.Bg)) {
        Box(Modifier.fillMaxSize().then(if (!showBar) Modifier.navigationBarsPadding() else Modifier)) {
            NavHost(nav, startDestination = if (store.signedIn) "learn" else "welcome") {
                composable("welcome") {
                    WelcomeScreen(store) {
                        nav.navigate("learn") { popUpTo("welcome") { inclusive = true } }
                    }
                }
                composable("learn") { LearnScreen(store, nav) }
                composable("labs") { LabsScreen(store, nav) }
                composable("tools") { ToolsScreen(nav) }
                composable("secure") { SecureScreen(store) }
                composable("profile") { ProfileScreen(store, nav) }
                composable("glossary") { GlossaryScreen(nav) }
                composable("course/{courseId}") { CourseScreen(it.arguments?.getString("courseId") ?: "", store, nav) }
                composable("lesson/{lessonId}") { LessonScreen(it.arguments?.getString("lessonId") ?: "", store, nav) }
                composable("quiz/{lessonId}") { QuizScreen(it.arguments?.getString("lessonId") ?: "", store, nav) }
                composable("lab/{labId}") { LabScreen(it.arguments?.getString("labId") ?: "", store, nav) }
            }
        }
        if (showBar) FloatingNav(Modifier.align(Alignment.BottomCenter), route, nav)
    }
}

@Composable
fun App(store: Store) {
    var booted by rememberSaveable { mutableStateOf(false) }
    if (!booted) BootScreen { booted = true } else AppContent(store)
}
