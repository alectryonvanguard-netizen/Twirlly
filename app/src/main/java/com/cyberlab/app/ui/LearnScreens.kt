package com.cyberlab.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cyberlab.app.data.*

@Composable
private fun Hero(store: Store, course: Course, lesson: Lesson, onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().padding(top = 18.dp).clip(RoundedCornerShape(30.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFB4081A), Color(0xFF3B0A6B), Color(0xFF0B1B5E))))
            .clickable { onClick() }.padding(22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("CONTINUE LEARNING", color = Color.White.copy(alpha = .7f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.height(6.dp))
                Text(lesson.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 26.sp)
                Text(course.title, color = Color.White.copy(alpha = .7f), fontSize = 13.sp)
                Spacer(Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.PlayArrow, null, tint = Color.White)
                    Text(" Resume · ${lesson.minutes} min", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
            Ring(store.xpIntoLevel / 200f, 84.dp, Color.White, 7.dp) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("LV", color = Color.White.copy(alpha = .7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("${store.level}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
fun LearnScreen(store: Store, nav: NavHostController) {
    var filter by remember { mutableStateOf<Level?>(null) }
    var query by remember { mutableStateOf("") }
    val all = remember { Content.courses.flatMap { c -> c.lessons.map { l -> c to l } } }
    val next = all.firstOrNull { it.second.id == store.lastLesson && it.second.id !in store.lessons }
        ?: all.firstOrNull { it.second.id !in store.lessons } ?: all.first()
    val daily = all[((System.currentTimeMillis() / 86_400_000L) % all.size).toInt()]
    val courses = Content.courses.filter { filter == null || it.level == filter }
    val saved = all.filter { it.second.id in store.bookmarks }

    ScreenBg {
        LazyColumn(Modifier.fillMaxSize().statusBarsPadding(), contentPadding = listPad()) {
            item { ScreenTitle("Hello, ${store.name}", "Learn") { StreakBadge(store.streak) } }
            item { SearchField(query, "Search lessons, topics, tools") { query = it } }
            if (query.isBlank()) {
                item { Hero(store, next.first, next.second) { nav.navigate("lesson/${next.second.id}") } }
                item {
                    SectionTitle("Today's challenge")
                    ListRow(
                        Icons.Filled.Bolt, C.Gold, daily.second.title,
                        "${daily.first.title} · +${daily.second.xp} XP",
                        onClick = { nav.navigate("lesson/${daily.second.id}") },
                        trailing = { Icon(Icons.Filled.ChevronRight, null, tint = C.Muted) }
                    )
                }
                item {
                    SectionTitle("Courses")
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Pill("All", filter == null) { filter = null }
                        Level.values().forEach { l -> Pill(l.label, filter == l, levelColors(l).first) { filter = l } }
                    }
                    Spacer(Modifier.height(14.dp))
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(end = 20.dp)) {
                        items(courses, key = { it.id }) { c ->
                            val (a, b) = levelColors(c.level)
                            val done = c.lessons.count { it.id in store.lessons }
                            PosterTile(c.title, c.level.label, a, b, courseIcon(c.id), done / c.lessons.size.toFloat()) {
                                nav.navigate("course/${c.id}")
                            }
                        }
                    }
                }
                if (saved.isNotEmpty()) {
                    item { SectionTitle("Saved") }
                    items(saved, key = { "s" + it.second.id }) { (c, l) ->
                        ListRow(
                            Icons.Filled.Star, C.Gold, l.title, c.title,
                            onClick = { nav.navigate("lesson/${l.id}") },
                            trailing = { Icon(Icons.Filled.ChevronRight, null, tint = C.Muted) }
                        )
                    }
                }
                item {
                    SectionTitle("Reference")
                    ListRow(
                        Icons.Filled.Book, C.Blue, "Glossary", "${Glossary.terms.size} security terms explained",
                        onClick = { nav.navigate("glossary") },
                        trailing = { Icon(Icons.Filled.ChevronRight, null, tint = C.Muted) }
                    )
                }
            } else {
                val q = query.trim()
                val hits = all.filter { (_, l) ->
                    l.title.contains(q, true) || l.sections.any { it.title.contains(q, true) || it.body.contains(q, true) }
                }
                item { SectionTitle(if (hits.isEmpty()) "No results" else "${hits.size} result(s)") }
                items(hits, key = { "h" + it.second.id }) { (c, l) ->
                    ListRow(
                        Icons.Filled.PlayCircle, levelColors(c.level).first, l.title, c.title,
                        onClick = { nav.navigate("lesson/${l.id}") },
                        trailing = { Icon(Icons.Filled.ChevronRight, null, tint = C.Muted) }
                    )
                }
            }
        }
    }
}

@Composable
fun CourseScreen(courseId: String, store: Store, nav: NavHostController) {
    val course = Content.courses.first { it.id == courseId }
    val (c1, c2) = levelColors(course.level)
    val done = course.lessons.count { it.id in store.lessons }
    ScreenBg {
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 32.dp)) {
            item {
                Box(
                    Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(c1.copy(alpha = .85f), c2.copy(alpha = .45f), Color.Transparent)))
                ) {
                    Icon(
                        courseIcon(course.id), null, tint = Color.White.copy(alpha = .14f),
                        modifier = Modifier.size(170.dp).align(Alignment.TopEnd).offset(x = 30.dp, y = 40.dp)
                    )
                    Column(Modifier.statusBarsPadding().padding(start = 6.dp, end = 20.dp, bottom = 30.dp)) {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                        }
                        Column(Modifier.padding(start = 14.dp)) {
                            Kicker(course.level.label, Color.White)
                            Text(course.title, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 36.sp)
                            Spacer(Modifier.height(6.dp))
                            Text(course.blurb, color = Color.White.copy(alpha = .85f), fontSize = 14.sp)
                            Spacer(Modifier.height(16.dp))
                            Text("$done of ${course.lessons.size} lessons · ${course.lessons.sumOf { it.xp }} XP", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { done / course.lessons.size.toFloat() },
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                color = Color.White, trackColor = Color.White.copy(alpha = .25f)
                            )
                        }
                    }
                }
            }
            itemsIndexed(course.lessons) { _, l ->
                val isDone = l.id in store.lessons
                Column(Modifier.padding(horizontal = 20.dp)) {
                    ListRow(
                        if (isDone) Icons.Filled.CheckCircle else Icons.Filled.PlayCircle,
                        if (isDone) C.Green else c1, l.title, "${l.minutes} min · ${l.xp} XP",
                        onClick = { nav.navigate("lesson/${l.id}") },
                        trailing = {
                            IconButton(onClick = { store.toggleBookmark(l.id) }) {
                                Icon(
                                    if (l.id in store.bookmarks) Icons.Filled.Star else Icons.Filled.StarBorder,
                                    null, tint = C.Gold
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LessonScreen(lessonId: String, store: Store, nav: NavHostController) {
    val lesson = Content.lesson(lessonId)
    val course = Content.courseOf(lessonId)
    val c1 = levelColors(course.level).first
    val listState = rememberLazyListState()
    val progress by remember {
        derivedStateOf {
            val n = listState.layoutInfo.totalItemsCount
            if (n == 0) 0f else (listState.firstVisibleItemIndex + 1f) / n
        }
    }
    LaunchedEffect(lessonId) { store.openLesson(lessonId) }

    ScreenBg {
        Column(Modifier.fillMaxSize()) {
            TopBar(course.title, onBack = { nav.popBackStack() }, trailing = {
                IconButton(onClick = { store.toggleBookmark(lessonId) }) {
                    Icon(
                        if (lessonId in store.bookmarks) Icons.Filled.Star else Icons.Filled.StarBorder,
                        null, tint = C.Gold
                    )
                }
            })
            LinearProgressIndicator(
                progress = { progress }, modifier = Modifier.fillMaxWidth().height(2.dp),
                color = c1, trackColor = Color.Transparent
            )
            LazyColumn(
                state = listState, modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            ) {
                item {
                    Kicker(course.level.label, c1)
                    Spacer(Modifier.height(6.dp))
                    Text(lesson.title, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 34.sp, letterSpacing = (-0.5).sp)
                    Spacer(Modifier.height(8.dp))
                    Text("${lesson.minutes} min read · ${lesson.xp} XP", color = C.Muted, fontSize = 13.sp)
                }
                itemsIndexed(lesson.sections) { i, sec ->
                    Column(Modifier.padding(top = 30.dp)) {
                        Text("0${i + 1}", color = c1, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(sec.title, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(sec.body, color = Color(0xFFD6D9E5), fontSize = 16.sp, lineHeight = 25.sp)
                    }
                }
                item {
                    Spacer(Modifier.height(36.dp))
                    if (lesson.id in store.lessons) {
                        Text("Completed. Retake the quiz any time.", color = C.Green, fontSize = 13.sp)
                        Spacer(Modifier.height(10.dp))
                    }
                    RedButton("Take the quiz", icon = Icons.Filled.Quiz) { nav.navigate("quiz/${lesson.id}") }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun QuizScreen(lessonId: String, store: Store, nav: NavHostController) {
    val lesson = Content.lesson(lessonId)
    val c1 = levelColors(Content.courseOf(lessonId).level).first
    var idx by remember { mutableIntStateOf(0) }
    var picked by remember { mutableIntStateOf(-1) }
    var score by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }
    var earned by remember { mutableStateOf(false) }
    val total = lesson.quiz.size
    val passed = score * 100 / total >= 60

    ScreenBg {
        Column(Modifier.fillMaxSize()) {
            TopBar("Quiz", onBack = { nav.popBackStack() })
            Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(total) { i ->
                    Box(
                        Modifier.weight(1f).height(4.dp).clip(CircleShape)
                            .background(if (i <= idx || done) c1 else Color.White.copy(alpha = .1f))
                    )
                }
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(24.dp)) {
                if (!done) {
                    val q = lesson.quiz[idx]
                    Kicker("Question ${idx + 1} of $total", c1)
                    Spacer(Modifier.height(10.dp))
                    Text(q.q, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 30.sp)
                    Spacer(Modifier.height(20.dp))
                    q.options.forEachIndexed { i, o ->
                        val state = when {
                            picked == -1 -> 0
                            i == q.answer -> 1
                            i == picked -> 2
                            else -> 3
                        }
                        val accent = when (state) { 1 -> C.Green; 2 -> C.Red; else -> Color.White }
                        Row(
                            Modifier.fillMaxWidth().clickable(enabled = picked == -1) {
                                picked = i
                                if (i == q.answer) score++
                            }.padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier.size(36.dp).clip(CircleShape).background(accent.copy(alpha = if (state == 0 || state == 3) .08f else .25f)),
                                contentAlignment = Alignment.Center
                            ) { Text("ABCD"[i].toString(), color = accent, fontWeight = FontWeight.Bold) }
                            Spacer(Modifier.width(14.dp))
                            Text(o, color = if (state == 3) C.Muted else Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
                            if (state == 1) Icon(Icons.Filled.CheckCircle, null, tint = C.Green)
                            if (state == 2) Icon(Icons.Filled.Cancel, null, tint = C.Red)
                        }
                        Hairline()
                    }
                    if (picked != -1) {
                        Spacer(Modifier.height(18.dp))
                        if (picked == q.answer) {
                            Text("Correct!", color = C.Green, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        } else {
                            Text("Not quite.", color = C.Red, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Correct answer: ${q.options[q.answer]}", color = C.Muted, fontSize = 14.sp)
                        }
                        Spacer(Modifier.height(16.dp))
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
                    Spacer(Modifier.height(24.dp))
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Ring(score / total.toFloat(), 150.dp, if (passed) C.Green else C.Orange, 12.dp) {
                            Text("$score/$total", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(Modifier.height(24.dp))
                        Text(if (passed) "Mission passed" else "Keep training", color = if (passed) C.Green else C.Orange, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            when {
                                !passed -> "You need at least 60% to complete the lesson. Re-read and try again."
                                earned -> "+${lesson.xp} XP earned. Lesson completed."
                                else -> "Already completed earlier, so no extra XP this time."
                            }, color = C.Muted, fontSize = 15.sp
                        )
                        Spacer(Modifier.height(32.dp))
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
}
