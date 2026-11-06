package com.cyberlab.app.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cyberlab.app.data.*

@Composable
fun LearnScreen(store: Store, nav: NavHostController) {
    var filter by remember { mutableStateOf<Level?>(null) }
    val list = Content.courses.filter { filter == null || it.level == filter }
    ScreenBg {
        LazyColumn(
            Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("Hello, ${store.name}", color = C.Muted, fontSize = 14.sp)
                Text("Learn", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            }
            item { XpCard(store) }
            item {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Pill("All", filter == null) { filter = null }
                    Level.values().forEach { l -> Pill(l.label, filter == l, Color(l.color)) { filter = l } }
                }
            }
            items(list, key = { it.id }) { c ->
                val accent = Color(c.level.color)
                val done = c.lessons.count { it.id in store.lessons }
                GlowCard(accent = accent, onClick = { nav.navigate("course/${c.id}") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Tag(c.level.label, accent)
                        Spacer(Modifier.weight(1f))
                        Text("$done/${c.lessons.size} lessons", color = C.Muted, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(c.title, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Text(c.blurb, color = C.Muted, fontSize = 13.sp)
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { done / c.lessons.size.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = accent, trackColor = C.Surface2
                    )
                }
            }
        }
    }
}

@Composable
fun CourseScreen(courseId: String, store: Store, nav: NavHostController) {
    val course = Content.courses.first { it.id == courseId }
    val accent = Color(course.level.color)
    ScreenBg {
        Column(Modifier.fillMaxSize()) {
            TopBar(course.title) { nav.popBackStack() }
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Text(course.blurb, color = C.Muted, fontSize = 14.sp) }
                itemsIndexed(course.lessons) { i, l ->
                    val done = l.id in store.lessons
                    GlowCard(accent = accent, onClick = { nav.navigate("lesson/${l.id}") }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (done) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked, null,
                                tint = if (done) C.Green else C.Muted
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Lesson ${i + 1}", color = C.Muted, fontSize = 12.sp)
                                Text(l.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Text("${l.minutes} min\n${l.xp} XP", color = accent, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonScreen(lessonId: String, store: Store, nav: NavHostController) {
    val lesson = Content.lesson(lessonId)
    val course = Content.courseOf(lessonId)
    val accent = Color(course.level.color)
    ScreenBg {
        Column(Modifier.fillMaxSize()) {
            TopBar(lesson.title) { nav.popBackStack() }
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Tag(course.level.label, accent)
                        Spacer(Modifier.width(8.dp))
                        Text("${lesson.minutes} min · ${lesson.xp} XP", color = C.Muted, fontSize = 12.sp)
                    }
                }
                itemsIndexed(lesson.sections) { i, sec ->
                    GlowCard(accent = accent) {
                        Text("${i + 1}. ${sec.title}", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(sec.body, color = Color(0xFFD6D9E5), fontSize = 14.sp, lineHeight = 21.sp)
                    }
                }
                item {
                    Spacer(Modifier.height(4.dp))
                    if (lesson.id in store.lessons) {
                        Text("Completed ✓  You can retake the quiz any time.", color = C.Green, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                    }
                    RedButton("Take the quiz") { nav.navigate("quiz/${lesson.id}") }
                }
            }
        }
    }
}

@Composable
fun QuizScreen(lessonId: String, store: Store, nav: NavHostController) {
    val lesson = Content.lesson(lessonId)
    var idx by remember { mutableIntStateOf(0) }
    var picked by remember { mutableIntStateOf(-1) }
    var score by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }
    var earned by remember { mutableStateOf(false) }
    val total = lesson.quiz.size
    val passed = score * 100 / total >= 60

    ScreenBg {
        Column(Modifier.fillMaxSize()) {
            TopBar("Quiz · ${lesson.title}") { nav.popBackStack() }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
                if (!done) {
                    val q = lesson.quiz[idx]
                    Text("Question ${idx + 1} of $total", color = C.Muted, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(q.q, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    q.options.forEachIndexed { i, o ->
                        val accent = when {
                            picked == -1 -> C.Blue
                            i == q.answer -> C.Green
                            i == picked -> C.Red
                            else -> Color(0xFF3A3F55)
                        }
                        GlowCard(accent = accent, onClick = {
                            if (picked == -1) {
                                picked = i
                                if (i == q.answer) score++
                            }
                        }) { Text(o, color = Color.White, fontSize = 15.sp) }
                        Spacer(Modifier.height(10.dp))
                    }
                    if (picked != -1) {
                        Spacer(Modifier.height(6.dp))
                        if (picked == q.answer) {
                            Text("Correct!", color = C.Green, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        } else {
                            Text("Not quite.", color = C.Red, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Text("Correct answer: ${q.options[q.answer]}", color = C.Muted, fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(14.dp))
                        RedButton(if (idx == total - 1) "Finish" else "Next") {
                            if (idx == total - 1) {
                                done = true
                                if (score * 100 / total >= 60) earned = store.completeLesson(lesson.id, lesson.xp)
                            } else {
                                idx++; picked = -1
                            }
                        }
                    }
                } else {
                    Spacer(Modifier.height(30.dp))
                    Text(if (passed) "Mission passed" else "Keep training", color = if (passed) C.Green else C.Orange,
                        fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(8.dp))
                    Text("Score: $score / $total", color = Color.White, fontSize = 20.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        when {
                            !passed -> "You need at least 60% to complete the lesson. Re-read the sections and try again."
                            earned -> "+${lesson.xp} XP earned. Lesson completed."
                            else -> "Already completed earlier, so no extra XP this time."
                        }, color = C.Muted, fontSize = 14.sp
                    )
                    Spacer(Modifier.height(24.dp))
                    if (passed) {
                        RedButton("Back to course") { nav.popBackStack("lesson/{lessonId}", inclusive = true) }
                    } else {
                        RedButton("Try again") { idx = 0; picked = -1; score = 0; done = false; earned = false }
                    }
                }
            }
        }
    }
}
