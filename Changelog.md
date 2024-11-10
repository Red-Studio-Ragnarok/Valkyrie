# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project follows to [Ragnarök Versioning Convention](https://shor.cz/ragnarok_versioning_convention).

## Valkyrie Version 0.3 Changelog - 2024-11-10

- Updated to [io.freefair.lombok](https://plugins.gradle.org/plugin/io.freefair.lombok) 8.7.1
- Updated to [org.jetbrains.gradle.plugin.idea-ext](https://github.com/JetBrains/gradle-idea-ext-plugin) 1.1.9
- Updated [MixinBooter](https://www.curseforge.com/minecraft/mc-mods/mixin-booter) dependency to 9.4
- Updated to [gradle-buildconfig-plugin](https://github.com/gmazzo/gradle-buildconfig-plugin) 5.5.0
- Updated [RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle) to version 1.4.1
- Set a minimum Gradle Daemon JVM version requirement

## Valkyrie Version 0.2 Changelog - 2024-04-04

### Added

- Warning screen system to notify of potential frictions between Valkyrie and other mods
- Added warning for using OptiFine
- Built in support for Mantle for the MC-67532 fix
- Built in support for Overloaded Armor Bar for the MC-67532 fix
- When in a deobfuscated environment, "Development Environment" will be added after the name of the window
- Added terrain wireframe debug config
- Added configuration for the MC-67532 fix
- Added configuration for fancy leaves independent of the Minecraft general graphics settings
- Added leaves culling
- Added keybinding to open the config GUI
- Added F3 shortcuts for clouds and terrain wireframe
- Added missing `MCVersion` attribute to the `ValkyriePlugin` 

### Changed

- Changed the upper limit for the `Zoom Multiplier` to 10
- Reduced default cloud render distance
- Updated to Red Core 0.5

### Fixed

- Fixed OptiFine compatibility (Keep in mind that parts of Valkyrie are disabled when paired with OptiFine)
- Fixed Essential compatibility
- Fixed not being able to disable clouds
- Fixed certain model rotations being broken, (For example, the witch while drinking her potion)
- Fixed large entities dissapearing when in empty chunks (For example, the ender dragon in the end)
- Fixed Valkyrie not declaring Red Core as a dependency
- Fixed clouds taking a quarter second to appear when joining a world
- Fixed incompatibility with RenderLib 1.3.3+

### Optimized

- Optimized `RenderGlobal#setupTerrain` which improves rendering speed
- Optimized `RenderGlobal#getRenderChunkOffset` which improves rendering speed
- Optimized `WorldVertexBufferUploader#draw` which improves rendering speed
- Optimized `ViewFrustum` which reduce FPS drops when blocks are updated, improves the speed of loading render chunks and reduce lag when loading renderer (Changing graphics settings, loading into world)
- Optimized `ModelRenderer#render` rotations and translations which are now more than two times faster (Thanks [Nessiesson], [Ven])
- Optimized `ModelRenderer#renderWithRotation` which should improve rendering speed of models (Thanks [Nessiesson], [Ven])
- Optimized `ModelRenderer#postRender` which should improve rendering speed of models (Thanks [Nessiesson], [Ven])

### Internal

- Switched to new groupId
- Fully switched to Red Core
- Switched to [gradle-buildconfig-plugin](https://github.com/gmazzo/gradle-buildconfig-plugin) entirely for project constants
- Switched to Gradle Kotlin DSL
- General project cleanup
- Updated [RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle) to version 1.3.34
- Updated [foojay-resolver](https://github.com/gradle/foojay-toolchains) to version 0.8.0
- Updated [io.freefair.lombok](https://plugins.gradle.org/plugin/io.freefair.lombok) to version 8.6
- Updated [org.jetbrains.gradle.plugin.idea-ext](https://plugins.gradle.org/plugin/org.jetbrains.gradle.plugin.idea-ext) to version 1.1.8

## Valkyrie Version 0.1.3 Changelog 2023-06-16

### Fixed

- Crash at startup, again

### Internal

- Minor clean-up

## Valkyrie Version 0.1.2 Changelog 2023-06-15 [YANKED]

### Fixed

- Crash at startup because I did an oppsie, sorry

## Valkyrie Version 0.1.1 Changelog 2023-06-15

### Fixed

- Fixed issue causing the version checker to incorrectly report newer versions as outdated

### Removed

- Removed duplicated MixinBooter version checking from the F3 menu

### Internal

- Updated `@reason` for overwritten methods
- Use `ModifyArg` instead of `Redirect` to fix MC-67532

[Nessiesson]: https://github.com/Nessiesson 
[Ven]: https://github.com/basdxz
