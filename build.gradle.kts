plugins {
    id("ca.stellardrift.gitpatcher") version "2.0.0"
}

gitPatcher {
    patchedRepos {
        register("quests") {
            submodule = "Quests"
            target = file("Quests-Patched")
            patches = file("patches/quests")
        }
        register("localelib") {
            submodule = "LocaleLib"
            target = file("LocaleLib-Patched")
            patches = file("patches/localelib")
        }
    }
}

val upstreamRepos = mapOf(
    "Quests" to "quests.upstream.commit",
    "LocaleLib" to "localelib.upstream.commit",
)

tasks.register("updateUpstream") {
    group = "git patcher"
    description = "Met à jour les submodules vers les commits définis dans gradle.properties"
    doLast {
        fun exec(cmd: List<String>, dir: File) {
            val result = ProcessBuilder(cmd)
                .directory(dir)
                .redirectInput(ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor()
            if (result != 0) error("Commande échouée : $cmd (dans $dir)")
        }

        upstreamRepos.forEach { (submoduleName, commitProperty) ->
            val repoDir = file(submoduleName)
            val targetCommit = project.property(commitProperty) as String

            exec(listOf("git", "fetch", "origin"), repoDir)
            exec(listOf("git", "checkout", targetCommit), repoDir)
            exec(listOf("git", "add", submoduleName), projectDir)
        }
    }
}

tasks.register("buildQuests") {
    group = "build"
    description = "Compile LocaleLib patché puis le jar final Quests"
    dependsOn(gradle.includedBuild("Quests-Patched").task(":quests-dist:assemble"))
}

tasks.register("buildLocaleLib") {
    group = "build"
    description = "Compile LocaleLib patché seul"
    dependsOn(gradle.includedBuild("LocaleLib-Patched").task(":build"))
}