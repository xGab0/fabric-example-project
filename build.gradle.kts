plugins {
    id("fabric-loom") version "1.6-SNAPSHOT"
    id("maven-publish")
}

version = findProperty("mod_version")!!
group = findProperty("maven_group")!!

dependencies {
    val minecraft_version = findProperty("minecraft_version")
    val yarn_mappings = findProperty("yarn_mappings")
    val loader_version = findProperty("loader_version")
    val fabric_version = findProperty("fabric_version")

    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:$minecraft_version")
    //mappings("net.fabricmc:yarn:$yarn_mappings:v2")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$loader_version")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }

    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Javadoc> {
        options.encoding = "UTF-8"
    }

    tasks.withType<ProcessResources> {
        filteringCharset = "UTF-8"
    }

    tasks.withType<Jar> {
        from("LICENSE")
    }

    // make build reproducible
    tasks.withType<AbstractArchiveTask> {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    tasks.withType<ProcessResources> {
        filteringCharset = "UTF-8"

        val minecraft_version = findProperty("minecraft_version")
        val yarn_mappings = findProperty("yarn_mappings")
        val loader_version = findProperty("loader_version")
        val fabric_version = findProperty("fabric_version")

        inputs.property("version", project.version)
        inputs.property("minecraft_version", minecraft_version)
        inputs.property("yarn_mappings", yarn_mappings)
        inputs.property("loader_version", loader_version)
        inputs.property("fabric_version", fabric_version)

        filesMatching("fabric.mod.json") {
            expand(mapOf(
                "version" to project.version,
                "minecraft_version" to minecraft_version,
                "yarn_mappings" to yarn_mappings,
                "loader_version" to loader_version,
                "fabric_version" to fabric_version
            ))
        }
    }

    // Loom will automatically attach sourcesJar to a RemapSourcesJar task
    // and to the "build" task if it is present.
    //
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

loom {
    accessWidenerPath = file("src/main/resources/examplemod.accesswidener")
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            //artifactId = project.archives_base_name
            //from components.java

            //artifact(tasks.remapJar)
            //artifact(tasks.remapSourcesJar)
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.

        // uncomment to publish to the local maven
        // mavenLocal()
    }
}