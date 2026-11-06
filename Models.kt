package com.cyberlab.app.data

enum class Level(val label: String, val color: Long) {
    BEGINNER("Beginner", 0xFF22C55E),
    INTERMEDIATE("Intermediate", 0xFF2F6BFF),
    ADVANCED("Advanced", 0xFFF97316),
    EXPERT("Expert", 0xFFFF1E2D)
}

data class Section(val title: String, val body: String)
data class Question(val q: String, val options: List<String>, val answer: Int)
data class Lesson(
    val id: String, val title: String, val minutes: Int, val xp: Int,
    val sections: List<Section>, val quiz: List<Question>
)
data class Course(
    val id: String, val title: String, val level: Level, val blurb: String, val lessons: List<Lesson>
)
data class Lab(
    val id: String, val title: String, val level: Level, val xp: Int, val brief: String,
    val files: Map<String, String>, val hints: List<String>, val flag: String
)
data class Tool(val name: String, val category: String, val what: String, val use: String, val defend: String)
data class CheckItem(val id: String, val title: String)
