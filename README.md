# Tums
A Minecraft Mod that makes Minecraft Modding simpler
---

Tums is a Fabric Minecraft Mod that simplifies Minecraft Mod Development by introducing a simple, user-friendly language based on [Tusan](https://wiki.tutla.net/tusan). It was initially meant to be an extension for [CCS](https://wiki.tutla.net/ccs)

## How to install

1. Download the latest version from [Modrinth](https://modrith.com/mod/tums)
2. Drag and drop the downloaded `.jar` file into your `mods` folder
3. Start Minecraft and enjoy the simplicity of Tums!

## How to write scripts?

Scripting in tums is relatively simple and includes a system to introduce custom syntax via mods. To start writing a script, simply create a new file with a `.tsn` extension in `[YOUR MINECRAFT FOLDER]/tums/scripts/`. Here is a basic example of a script:

```lua
on left_click
    print("Hello, World!")
end
```

**To start learning in detail, read [Scripting](https://wiki.tutla.net/tums/scripting/)**

## For developers

Tums is fully open source and contains API support for creating custom syntax and expanding the language or [creating your own language](https://wiki.tutla.net/tums/developer/creating-your-own).

To get started, follow these steps:

1. Download the mod and put it in `[PROJECT FOLDER]/libs/`
```yaml
├── build
├── build.gradle
├── gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── libs
│   └── tums-0.1.0.jar <-- here!
├── LICENSE
├── README.md
├── settings.gradle
└── src
    ├── client
    └── main
```
2.. Add the mod as a dependancy in `build.gradle` under the `dependencies` section.
```cpp
dependencies {
    minecraft "com.mojang:minecraft:${project.minecraft_version}"

    implementation "net.fabricmc:fabric-loader:${project.loader_version}"

    implementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_api_version}"
    implementation files("libs/[MOD FILENAME].jar") // <---- HERE!
}
```
3. Import `TumsAPI` and start developing!

Read the full developer docs in the [Tums API documentation](https://wiki.tutla.net/tums/developer/).

---

## 💬 Support, Tracking & More

- 💬 Join our [**Discord**](https://discord.tutla.net) for sneak peeks & community support
- ⭐ Star us on [**GitHub**](https://github.com/TutlaMC/manhunt-plus)
- 🌐 Visit our [**Website**](https://tutla.net) for updates
- 💙 Contribute by [**donating**](https://donatr.ee/tutlamc?utm_source=copy&utm_medium=share)

Think we’re missing something? **Contribute on [GitHub](https://github.com/TutlaMC/manhunt-plus)**

---

## 🧪 Other Tutla Studio Projects

- ⚔️ [**PvP+**](https://modrinth.com/plugin/pvp+plugin) – A Comprehensive PvP Plugin for Paper servers
- 🛡️ [**LeaveBind!**](https://modrinth.com/mod/leavebind) – Leave the server with a click of a button
- 🧾 [**Leave This, GoBack!**](https://modrinth.com/mod/leave-this-go-back) – Pressing on a slot twice returns you back to the previous slot. Useful for attribute swapping / quick tool swapping / mace pvp 