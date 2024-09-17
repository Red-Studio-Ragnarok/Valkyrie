import org.jetbrains.gradle.ext.settings
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.runConfigurations

plugins {
    id("org.jetbrains.kotlin.jvm") version embeddedKotlinVersion
    id("com.gtnewhorizons.retrofuturagradle") version "1.3.34"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
    id("com.github.gmazzo.buildconfig") version "5.5.0"
    id("io.freefair.lombok") version "8.10"
}

group = "dev.redstudio"
version = "0.2" // Versioning must follow Ragnarök versioning convention: https://shor.cz/ragnarok_versioning_convention

val id = project.name.lowercase()

val redCoreVersion = "MC-1.8-1.12-" + "0.6-Dev-3"
val configanytimeVersion = "1.0"
val jomlVersion = "1.10.5"

minecraft {
    mcVersion = "1.12.2"
    username = "Desoroxxx"
    extraRunJvmArguments = listOf("-Dforge.logging.console.level=debug", "-Dfml.coreMods.load=${project.group}.${id}.asm.ValkyriePlugin", "-Dmixin.hotSwap=true", "-Dmixin.checks.mixininterfaces=true", "-Dmixin.debug.export=true")
}

repositories {
    maven {
        name = "Cleanroom"
        url = uri("https://maven.cleanroommc.com")
    }

    maven {
        name = "SpongePowered"
        url = uri("https://repo.spongepowered.org/maven")
    }

    listOf("release", "beta", "dev").forEach { repoType ->
        maven {
            name = "Red Studio - ${repoType.capitalize()}"
            url = uri("https://repo.redstudio.dev/$repoType")
        }
    }

    exclusiveContent {
        forRepository {
            maven {
                name = "Curse Maven"
                url = uri("https://cursemaven.com")
            }
        }
        filter {
            includeGroup("curse.maven")
        }
    }
}

dependencies {
    implementation("dev.redstudio", "Red-Core", redCoreVersion)

    implementation("com.cleanroommc", "configanytime", configanytimeVersion)

    implementation("org.joml", "joml", jomlVersion)

    implementation(rfg.deobf("curse.maven:appleskin-248787:2987247"))
    implementation(rfg.deobf("curse.maven:tinyinv-455299:3251677"))
    implementation(rfg.deobf("curse.maven:mantle-74924:2713386"))
    implementation(rfg.deobf("curse.maven:overloadedarmorbar-314002:2719400"))

    annotationProcessor("org.ow2.asm", "asm-debug-all", "5.2")
    annotationProcessor("com.google.guava", "guava", "32.1.2-jre")
    annotationProcessor("com.google.code.gson", "gson", "2.8.9")

    val mixinBooter: String = modUtils.enableMixins("zone.rong:mixinbooter:8.6", "mixins.${id}.refmap.json") as String
    api(mixinBooter) {
        isTransitive = false
    }
    annotationProcessor(mixinBooter) {
        isTransitive = false
    }
}

buildConfig {
    packageName("${project.group}.${id}")
    className("ProjectConstants")
    documentation.set("This class defines constants for ${project.name}.")

    useJavaOutput()
    buildConfigField("String", "ID", provider { """"${id}"""" })
    buildConfigField("String", "NAME", provider { """"${project.name}"""" })
    buildConfigField("String", "VERSION", provider { """"${project.version}"""" })
    buildConfigField("org.apache.logging.log4j.Logger", "LOGGER", "org.apache.logging.log4j.LogManager.getLogger(NAME)")
    buildConfigField("String[]", "VALKYRIE_RECOMFORT_MESSAGES", """new String[]{
        "Exception encountered! Fear not, for Valkyries fear no code.",
        "Forge ahead! A Valkyrie's mettle is tested by challenges.",
        "Patience, as Valkyries know, leads to triumph in the end.",
        "A glitch in the matrix? Valkyries are skilled in rewriting fate.",
        "Hold your ground! Valkyries embrace challenges as opportunities.",
        "Remember, even Valkyries stumble before they learn to fly gracefully."
    }""")
    buildConfigField("dev.redstudio.redcore.logging.RedLogger", "RED_LOGGER", """new RedLogger(NAME, "https://linkify.cz/ValkyrieBugReport", LOGGER, VALKYRIE_RECOMFORT_MESSAGES)""")
}

idea {
    module {
        inheritOutputDirs = true

        excludeDirs = setOf(
                file(".github"), file(".gradle"), file(".idea"), file("build"), file("gradle"), file("run")
        )
    }

    project {
        settings {
            jdkName = "1.8"
            languageLevel = IdeaLanguageLevel("JDK_1_8")

            runConfigurations {
                create("Client", Gradle::class.java) {
                    taskNames = setOf("runClient")
                }
                create("Server", Gradle::class.java) {
                    taskNames = setOf("runServer")
                }
                create("Obfuscated Client", Gradle::class.java) {
                    taskNames = setOf("runObfClient")
                }
                create("Obfuscated Server", Gradle::class.java) {
                    taskNames = setOf("runObfServer")
                }
                create("Vanilla Client", Gradle::class.java) {
                    taskNames = setOf("runVanillaClient")
                }
                create("Vanilla Server", Gradle::class.java) {
                    taskNames = setOf("runVanillaServer")
                }
            }
        }
    }
}

// Set the toolchain version to decouple the Java we run Gradle with from the Java used to compile and run the mod
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
    withSourcesJar() // Generate sources jar when building and publishing
}

tasks.processResources.configure {
    inputs.property("name", project.name)
    inputs.property("version", project.version)
    inputs.property("id", id)

    filesMatching("mcmod.info") {
        expand(mapOf("name" to project.name, "version" to project.version, "id" to id))
    }
}

val at = project.files("src/main/resources/META-INF/${id}_at.cfg")

tasks.deobfuscateMergedJarToSrg.configure {
    accessTransformerFiles.from(at)
}
tasks.srgifyBinpatchedJar.configure {
    accessTransformerFiles.from(at)
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "ModSide" to "CLIENT",
            "FMLAT" to "${id}_at.cfg",
            "FMLCorePlugin" to "${project.group}.${id}.asm.ValkyriePlugin",
            "FMLCorePluginContainsFMLMod" to "true",
            "ForceLoadAsMod" to "true"
        )
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        include("org/joml/**")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.isFork = true
    options.forkOptions.jvmArgs = listOf("-Xmx4G")
}
