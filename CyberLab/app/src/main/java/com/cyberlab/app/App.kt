package com.cyberlab.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cyberlab.app.data.Store

private data class Tab(val route: String, val label: String, val icon: ImageVector)

@Composable
fun App(store: Store) {
    val nav = rememberNavController()
    val backEntry by nav.currentBackStackEntryAsState()
    val route = backEntry?.destination?.route
    val tabs = listOf(
        Tab("learn", "Learn", Icons.Filled.School),
        Tab("labs", "Labs", Icons.Filled.Science),
        Tab("tools", "Tools", Icons.Filled.Build),
        Tab("secure", "Secure", Icons.Filled.VerifiedUser),
        Tab("profile", "Profile", Icons.Filled.Person)
    )
    val showBar = tabs.any { it.route == route }

    Scaffold(
        containerColor = C.Bg,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBar) {
                NavigationBar(containerColor = C.Surface) {
                    tabs.forEach { t ->
                        NavigationBarItem(
                            selected = route == t.route,
                            onClick = {
                                nav.navigate(t.route) {
                                    popUpTo("learn") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(t.icon, null) },
                            label = { Text(t.label, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = C.Red, selectedTextColor = C.Red,
                                indicatorColor = C.Red.copy(alpha = .15f),
                                unselectedIconColor = C.Muted, unselectedTextColor = C.Muted
                            )
                        )
                    }
                }
            }
        }
    ) { pad ->
        Box(
            Modifier
                .padding(bottom = pad.calculateBottomPadding())
                .then(if (!showBar) Modifier.navigationBarsPadding() else Modifier)
        ) {
            NavHost(nav, startDestination = if (store.signedIn) "learn" else "welcome") {
                composable("welcome") {
                    WelcomeScreen(store) {
                        nav.navigate("learn") { popUpTo("welcome") { inclusive = true } }
                    }
                }
                composable("learn") { LearnScreen(store, nav) }
                composable("labs") { LabsScreen(store, nav) }
                composable("tools") { ToolsScreen() }
                composable("secure") { SecureScreen(store) }
                composable("profile") { ProfileScreen(store, nav) }
                composable("course/{courseId}") { CourseScreen(it.arguments?.getString("courseId") ?: "", store, nav) }
                composable("lesson/{lessonId}") { LessonScreen(it.arguments?.getString("lessonId") ?: "", store, nav) }
                composable("quiz/{lessonId}") { QuizScreen(it.arguments?.getString("lessonId") ?: "", store, nav) }
                composable("lab/{labId}") { LabScreen(it.arguments?.getString("labId") ?: "", store, nav) }
            }
        }
    }
}
