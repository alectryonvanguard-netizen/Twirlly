package com.cyberlab.app.data

import android.util.Base64

/** A tiny simulated shell. Nothing here touches the real device or network. */
object Terminal {
    fun rot13(s: String): String = s.map { c ->
        when (c) {
            in 'a'..'z' -> 'a' + (c - 'a' + 13) % 26
            in 'A'..'Z' -> 'A' + (c - 'A' + 13) % 26
            else -> c
        }
    }.joinToString("")

    fun exec(lab: Lab, raw: String): String {
        val p = raw.trim().split(Regex("\\s+"))
        return when (p[0].lowercase()) {
            "help" -> "Commands:\n  ls [dir]\n  cat <file>\n  grep <word> <file>\n" +
                "  decode <b64|rot13|hex> <text>\n  hint\n  submit flag{...}\n  clear\n  whoami"
            "whoami" -> "analyst"
            "ls" -> ls(lab, p.getOrNull(1))
            "cat" -> if (p.size < 2) "usage: cat <file>"
            else lab.files[p[1].trim('/')] ?: "cat: ${p[1]}: No such file"
            "grep" -> if (p.size < 3) "usage: grep <word> <file>" else {
                val text = lab.files[p[2].trim('/')]
                if (text == null) "grep: ${p[2]}: No such file"
                else text.lines().filter { it.contains(p[1], ignoreCase = true) }
                    .ifEmpty { listOf("(no matches)") }.joinToString("\n")
            }
            "decode" -> if (p.size < 3) "usage: decode <b64|rot13|hex> <text>" else {
                val text = p.drop(2).joinToString(" ")
                when (p[1].lowercase()) {
                    "b64" -> try {
                        String(Base64.decode(text, Base64.DEFAULT))
                    } catch (e: Exception) {
                        "decode error: invalid base64"
                    }
                    "rot13" -> rot13(text)
                    "hex" -> try {
                        text.replace(" ", "").chunked(2).map { it.toInt(16).toChar() }.joinToString("")
                    } catch (e: Exception) {
                        "decode error: invalid hex"
                    }
                    else -> "unknown decoder. use b64, rot13 or hex"
                }
            }
            else -> "${p[0]}: command not found. Type help."
        }
    }

    private fun ls(lab: Lab, dir: String?): String {
        val prefix = dir?.trim('/')?.let { if (it.isEmpty() || it == ".") "" else "$it/" } ?: ""
        val names = lab.files.keys.filter { it.startsWith(prefix) }.map {
            val rest = it.removePrefix(prefix)
            if ('/' in rest) rest.substringBefore('/') + "/" else rest
        }.distinct().sorted()
        return if (names.isEmpty()) "ls: cannot access '${dir ?: ""}': No such directory"
        else names.joinToString("  ")
    }
}
