# CyberLab (Kotlin + Jetpack Compose)

Open this folder in Android Studio (Koala or newer, JDK 17), let Gradle sync, then Run.

Flow: Welcome -> sign in -> Learn | Labs | Tools | Secure | Profile (v2 flat redesign)
- Learn: 8 courses, 16 lessons (Beginner to Expert), quizzes, XP and levels
- Labs: 5 simulated terminal labs with flags (no real systems touched)
- Tools: reference cards with defender view
- Secure: security score checklist + offline password checker
- Profile: rank, achievements, sign out, reset

Google/GitHub buttons create a local profile. Real OAuth needs Firebase/GitHub keys.

v2 additions: floating nav, hero + daily challenge, search, saved lessons, streaks, glossary, cheat sheets, share progress.

v3: app icon (adaptive + themed) in res/drawable + res/mipmap-*, system splash (core-splashscreen), animated boot screen (ui/Boot.kt).
