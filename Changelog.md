# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project follows to [Ragnarök Versioning Convention](https://shor.cz/ragnarok_versioning_convention).

## [Unreleased] Valkyrie Version 0.2 Changelog

### Added

- Warning screen system to notify of potential frictions between Valkyrie and other mods
- Added warning for using OptiFine
- Built in support for Mantle for the MC-67532 fix
- When in a deobfuscated environment, "Development Environment" will be added after the name of the window
- Added terrain wireframe debug config

### Changed

- Changed the upper limit for the `Zoom Multiplier` to 10
- Reduced default cloud render distance

### Fixed

- Fixed OptiFine compatibility (Keep in mind that parts of Valkyrie are disabled when paired with OptiFine)
- Fixed not being able to disable clouds
- Fixed certain model rotations being broken, for example, the witch while drinking her potion

### Optimized

- Optimized `RenderGlobal#setupTerrain` which improves rendering speed
- Optimized `RenderGlobal#getRenderChunkOffset` which improves rendering speed
- Optimized `ViewFrustum` which reduce FPS drops when blocks are updated, improves the speed of loading render chunks and reduce lag when loading renderer (Changing graphics settings, loading into world)

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
