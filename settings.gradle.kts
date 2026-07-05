rootProject.name = "Quests-Folia"

val questsDir = file("Quests-Patched")
if (questsDir.exists()) {
    includeBuild(questsDir)
}

val localeLibDir = file("LocaleLib-Patched")
if (localeLibDir.exists()) {
    includeBuild(localeLibDir)
}