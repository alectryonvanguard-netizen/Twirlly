package com.cyberlab.app.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Store(context: Context) {
    private val sp = context.getSharedPreferences("cyberlab", Context.MODE_PRIVATE)

    var name by mutableStateOf(sp.getString("name", "") ?: "")
        private set
    var xp by mutableIntStateOf(sp.getInt("xp", 0))
        private set
    var lessons by mutableStateOf((sp.getStringSet("lessons", emptySet()) ?: emptySet()).toSet())
        private set
    var labs by mutableStateOf((sp.getStringSet("labs", emptySet()) ?: emptySet()).toSet())
        private set
    var checks by mutableStateOf((sp.getStringSet("checks", emptySet()) ?: emptySet()).toSet())
        private set
    var bookmarks by mutableStateOf((sp.getStringSet("bookmarks", emptySet()) ?: emptySet()).toSet())
        private set
    var lastLesson by mutableStateOf(sp.getString("lastLesson", "") ?: "")
        private set

    private var streakCount by mutableIntStateOf(sp.getInt("streak", 0))
    private var lastDay: Long = sp.getLong("lastDay", 0L)
    private fun today(): Long = System.currentTimeMillis() / 86_400_000L

    val streak: Int get() = if (streakCount > 0 && lastDay >= today() - 1) streakCount else 0
    val signedIn: Boolean get() = name.isNotBlank()
    val level: Int get() = xp / 200 + 1
    val xpIntoLevel: Int get() = xp % 200
    val rank: String get() = RANKS[minOf(level - 1, RANKS.lastIndex)]

    fun signIn(n: String) {
        name = n.trim().ifBlank { "Guest" }
        sp.edit().putString("name", name).apply()
    }

    fun signOut() {
        name = ""
        sp.edit().remove("name").apply()
    }

    private fun touch() {
        val t = today()
        if (lastDay == t) return
        streakCount = if (lastDay == t - 1) streakCount + 1 else 1
        lastDay = t
    }

    fun openLesson(id: String) {
        lastLesson = id
        save()
    }

    fun toggleBookmark(id: String) {
        bookmarks = if (id in bookmarks) bookmarks - id else bookmarks + id
        save()
    }

    /** Returns true only the first time (XP is awarded once). */
    fun completeLesson(id: String, reward: Int): Boolean {
        touch()
        if (id in lessons) { save(); return false }
        lessons = lessons + id
        xp += reward
        save()
        return true
    }

    fun completeLab(id: String, reward: Int): Boolean {
        touch()
        if (id in labs) { save(); return false }
        labs = labs + id
        xp += reward
        save()
        return true
    }

    fun toggleCheck(id: String) {
        checks = if (id in checks) checks - id else checks + id
        save()
    }

    fun reset() {
        lessons = emptySet(); labs = emptySet(); checks = emptySet(); bookmarks = emptySet()
        xp = 0; streakCount = 0; lastDay = 0L; lastLesson = ""
        save()
    }

    private fun save() {
        sp.edit()
            .putInt("xp", xp)
            .putInt("streak", streakCount)
            .putLong("lastDay", lastDay)
            .putString("lastLesson", lastLesson)
            .putStringSet("lessons", HashSet(lessons))
            .putStringSet("labs", HashSet(labs))
            .putStringSet("checks", HashSet(checks))
            .putStringSet("bookmarks", HashSet(bookmarks))
            .apply()
    }

    companion object {
        val RANKS = listOf(
            "Script Novice", "Recon Rookie", "Packet Hunter", "Exploit Apprentice",
            "Red Teamer", "Shadow Operator", "Cyber Legend"
        )
    }
}
