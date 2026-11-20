# 🔄 Pre-Placement Block Rotation

A highly advanced, strictly client-side Fabric mod that allows you to rotate and flip blocks *before* you place them, bypassing Minecraft's strict vanilla placement rules. 

Tired of having to build temporary dirt scaffolding just to place an Observer or Piston facing the correct direction? **Pre-Placement Block Rotation** gives you 100% control over your block orientations, complete with a perfectly synced visual ghost preview.

## ✨ Features
* **Perfect Sync Ghost Preview:** See exactly how your block will be placed before you click. The preview is produced by the same game code that does the real placement, so it always matches, and it disappears when the block cannot be placed there.
* **SINGLEPLAYER ONLY:** This mod is currently in **Beta**. The current release is **Singleplayer Only**. We are actively working on making the system compatible with **Multiplayer servers**, but due to how Minecraft handles block placement and synchronization, additional work is required. Until then, please treat this version as **experimental**.
* **Universal Support:** Works seamlessly with directional blocks that rely on player look-angles and hit-faces, such as Observers, Pistons, Dispensers, Logs, Pillars, and more.
* **Smart Toggling:** Easily turn the mod on or off on the fly with a single keystroke.

*(Note: Due to hardcoded vanilla server rules, upside-down stairs and slabs placed on flat ground will still snap to their vanilla states on multiplayer servers, but horizontal rotations and all other directional blocks work flawlessly!)*

## 🎮 Controls
You can change all of these in your standard Minecraft Controls menu under the **Rotation Placement** category:
* **`R`** : Toggle Mod ON / OFF
* **`Right Arrow` (Y-Axis)** : Spin the block horizontally (North/East/South/West).
* **`Up Arrow` (X-Axis)** : Flip the block vertically (Up/Down/Opposite).

## ⚙️ Versions & Compatibility
**Version 1.3** supports Minecraft `1.21` through `26.3`. Mojang changed the rendering engine and several game classes along the way, so there is one jar per group of versions. Download the file that matches your game version:

| Minecraft | Jar |
|---|---|
| 1.21 - 1.21.1 | `rotationplacement-1.3.0+1.21.jar` |
| 1.21.2 - 1.21.8 | `rotationplacement-1.3.0+1.21.2.jar` |
| 1.21.9 | `rotationplacement-1.3.0+1.21.9.jar` |
| 1.21.10 - 1.21.11 | `rotationplacement-1.3.0+1.21.10.jar` |
| 26.1.x | `rotationplacement-1.3.0+26.1.jar` |
| 26.2 - 26.3 | `rotationplacement-1.3.0+26.2.jar` |

Minecraft 26.1 and newer need Java 25, older versions Java 21. To build every jar yourself, run `./build-all.ps1` (it needs JDK 25 to run Gradle); the jars end up in `dist/`.

### What changed in 1.3
* Ported to Minecraft 1.21.2 - 26.3.
* Only **your own** placements are rotated now. Before, dispensers and other automatic placers were rotated too.
* The rotated state is checked before placing, so wall-mounted blocks (torches, ladders, buttons) are no longer placed facing a wall they are not attached to.
* The ghost preview is computed once per tick, follows the camera smoothly, uses the exact placement position (replaceable blocks like tall grass), and is hidden where placement is impossible.
* Slabs can be flipped upside down, rotated chests are placed as single chests, and blocks that only allow some orientations no longer crash.
* Rotate keys pressed while the mod is off no longer pile up and fire when you switch it on.
* Corrected the mixin config, the language file (keybind category name) and the mod metadata (client only, Fabric API and version ranges).

## 📥 Installation
1. Install the [Fabric Loader](https://fabricmc.net/use/installer/).
2. Download the [Fabric API](https://modrinth.com/mod/fabric-api) for your specific Minecraft version.
3. Download the correct `Pre-Placement Block Rotation` .jar file for your version.
4. Place both the Fabric API and this mod into your `.minecraft/mods` folder.

## 📝 License & Copyright
**Copyright (c) 2026 jcwd. All Rights Reserved.**

This mod and its source code are the intellectual property of jcwd. You are permitted to download and play with this mod, or include it in modpacks distributed via official platforms (Modrinth/CurseForge) that link back to this project. You may not modify, decompile, copy, or distribute the source code, nor re-upload the compiled `.jar` files to other platforms without explicit permission.