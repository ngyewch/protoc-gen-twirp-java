plugins {
    id("com.autonomousapps.build-health") version "3.2.0"
}

rootProject.name = "twirp"

rootDir.listFiles()?.filter { f ->
    f.isDirectory && (f.name != "buildSrc")
            && (File(f, "build.gradle").isFile || File(f, "build.gradle.kts").isFile)
}?.forEach { f ->
    include(f.name)
}
