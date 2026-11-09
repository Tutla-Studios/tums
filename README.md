# Tums
A Minecraft mod and plugin that makes Minecraft development simpler
---

Tums is a Minecraft project that simplifies Minecraft development by introducing a simple, user-friendly language based on [Tusan](https://wiki.tutla.net/tusan). It runs on **Fabric**, **NeoForge** and **Spigot/Paper**, so the same scripts work in your client, on a modded server or on a plugin server. It was initially meant to be an extension for [CCS](https://wiki.tutla.net/ccs)

## How to install

1. Download the jar for your loader from [Modrinth](https://modrinth.com/mod/tums)
2. Put it in the right folder:

| Loader       | Jar                         | Put it in  | Scripts go in                       |
|--------------|-----------------------------|------------|-------------------------------------|
| Fabric       | `tums-fabric-<version>.jar`   | `mods/`    | `[YOUR MINECRAFT FOLDER]/tums/scripts/` |
| NeoForge     | `tums-neoforge-<version>.jar` | `mods/`    | `[YOUR MINECRAFT FOLDER]/tums/scripts/` |
| Spigot/Paper | `tums-paper-<version>.jar`    | `plugins/` | `[YOUR SERVER FOLDER]/plugins/Tums/scripts/` |

3. Start Minecraft and enjoy the simplicity of Tums!

## How to write scripts?

Scripting in tums is relatively simple and includes a system to introduce custom syntax via mods and plugins. To start writing a script, simply create a new file with a `.tsn` extension in your loader's scripts folder (see the table above). Here is a basic example of a script:

```lua
on left_click
    print("Hello, World!")
end
```

**To start learning in detail, read [Scripting](https://wiki.tutla.net/tums/scripting/)**

## For developers

Tums is fully open source and contains API support for creating custom syntax and expanding the language or [creating your own language](https://wiki.tutla.net/tums/developer/creating-your-own). The API is the same on every loader: register your syntax through `TumsAPI`.

### Fabric / NeoForge mods

1. Download the jar for your loader and put it in `[PROJECT FOLDER]/libs/`
```yaml
├── build.gradle
├── libs
│   └── tums-fabric-1.0.0.jar <-- here!
├── settings.gradle
└── src
```
2. Add it as a dependency in `build.gradle` under the `dependencies` section.
```cpp
dependencies {
    // Fabric
    modImplementation files("libs/tums-fabric-1.0.0.jar") // <---- HERE!

    // NeoForge
    implementation files("libs/tums-neoforge-1.0.0.jar") // <---- HERE!
}
```
3. Import `TumsAPI` and start developing!

### Spigot / Paper plugins

1. Add the plugin jar as a `compileOnly` dependency and put it in your server's `plugins` folder.
```cpp
dependencies {
    compileOnly files("libs/tums-paper-1.0.0.jar")
}
```
2. Depend on it in your `plugin.yml`:
```yaml
depend: [Tums]
```
3. Register custom syntax in your `onEnable` using `TumsAPI.getGlobalTusan()` (or `TumsPaper.getTusan()`). Scripts run on the first server tick, after every plugin is enabled.

### Project layout

| Module         | Contents                                                                      |
|----------------|-------------------------------------------------------------------------------|
| `src/core`     | Pure-Java Tusan lexer/interpreter and the Tums language (no Minecraft deps)  |
| `src/common`   | Architectury common code (Minecraft-dependent, loader-agnostic)               |
| `src/fabric`   | Fabric entrypoints, client events and mixins                                  |
| `src/neoforge` | NeoForge entrypoint                                                           |
| `src/paper`    | Spigot/Paper plugin entrypoint                                                |

Build everything with `./gradlew build`; jars end up in `src/<module>/build/libs/`.

Read the full developer docs in the [Tums API documentation](https://wiki.tutla.net/tums/developer/).

---

## 💬 Support, Tracking & More

- 💬 Join our [**Discord**](https://discord.tutla.net) for sneak peeks & community support
- ⭐ Star us on [**GitHub**](https://github.com/TutlaMC/tums)
- 🌐 Visit our [**Website**](https://tutla.net) for updates
- 💙 Contribute by [**donating**](https://donatr.ee/tutlamc)

Think we’re missing something? **Contribute on [GitHub](https://github.com/TutlaMC/tums)**

---

## 🧪 Other Tutla Studio Projects

- ⚔️ [**PvP+**](https://modrinth.com/plugin/pvp+plugin) – A Comprehensive PvP Plugin for Paper servers
- 🛡️ [**LeaveBind!**](https://modrinth.com/mod/leavebind) – Leave the server with a click of a button
- 🧾 [**Leave This, GoBack!**](https://modrinth.com/mod/leave-this-go-back) – Pressing on a slot twice returns you back to the previous slot. Useful for attribute swapping / quick tool swapping / mace pvp 