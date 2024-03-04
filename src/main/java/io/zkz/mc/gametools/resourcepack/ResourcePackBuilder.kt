package io.zkz.mc.gametools.resourcepack

import com.google.gson.GsonBuilder
import io.zkz.mc.gametools.GameToolsPlugin
import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.get
import io.zkz.mc.gametools.util.ZipFileUtils.zipDirectory
import org.bukkit.NamespacedKey
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path

class ResourcePackBuilder(
    private val buildOutputDirectory: Path,
) : InjectionComponent {
    var dirty = false
        private set
        get() = field || withNegativeSpaceCharacters
    private var withNegativeSpaceCharacters = false

    private var nextCharId: Int = 0
    private val charData = mutableListOf<CustomCharacterData>()
    private val translationKeys = mutableMapOf<String, String>()

    fun item(key: NamespacedKey, stream: InputStream) {
        misc(Path(withPng("assets/${key.namespace}/textures/item/${key.key}")), stream)
    }

    fun block(key: NamespacedKey, stream: InputStream) {
        misc(Path(withPng("assets/${key.namespace}/textures/block/${key.key}")), stream)
    }

    fun character(stream: InputStream, ascent: Int, height: Int): Char {
        val charId = nextCharId++
        val assetPath = withPng("assets/gametools/custom/custom_character_$charId")
        charData.add(
            CustomCharacterData(
                charFromIndex(charId),
                "TODO",
                assetPath,
                ascent,
                height,
            ),
        )

        misc(Path(assetPath), stream)

        return charFromIndex(charId)
    }

    fun translationKey(key: String, value: String) {
        if (translationKeys.containsKey(key)) {
            get<GameToolsPlugin>().logger.warning("Duplicate translation key provided: $key")
        }

        translationKeys[key] = value
    }

    fun misc(path: Path, stream: InputStream) {
        dirty = true

        val finalPath = buildOutputDirectory.resolve(path)
        try {
            if (!Files.exists(finalPath.parent)) {
                Files.createDirectories(finalPath.parent)
            }

            stream.use {
                FileOutputStream(finalPath.toFile()).use {
                    stream.transferTo(it)
                }
            }
        } catch (e: IOException) {
            System.err.println("Could not create resource file at '$path'")
            e.printStackTrace(System.err)
        }
    }

    fun withNegativeSpaceCharacters() {
        withNegativeSpaceCharacters = true
    }

    fun build(outputPath: Path) {
        if (!dirty) {
            return
        }

        // Make GSON
        val gson = GsonBuilder().setPrettyPrinting().create()

        // pack.mcmeta
        val mcmeta = gson.toJson(
            mapOf(
                "pack" to mapOf(
                    "pack_format" to 22,
                    "description" to "Minigame resources",
                ),
            ),
        )
        misc(Path("pack.mcmeta"), mcmeta.byteInputStream(Charsets.UTF_8))

        // Custom font data
        val fontData = FontData()
        fontData.providers.addAll(
            charData.map {
                FontProvider(
                    "bitmap",
                    it.assetPath,
                    it.ascent,
                    it.height,
                    listOf(it.stringRepresentation),
                )
            },
        )

        // Load negative space font
        if (withNegativeSpaceCharacters) {
            // Load font glyphs
            ResourcePackBuilder::class.java.getResourceAsStream("/NegativeSpaceFont/assets/minecraft/font/default.json")!!
                .bufferedReader()
                .use {
                    val spaceFontData = gson.fromJson(it, FontData::class.java)
                    fontData.providers.addAll(spaceFontData.providers)
                }

            // Load translation keys
            ResourcePackBuilder::class.java.getResourceAsStream("/NegativeSpaceFont/assets/minecraft/font/default.json")!!
                .bufferedReader()
                .use {
                    val spaceFontData = gson.fromJson(it, FontData::class.java)
                    fontData.providers.addAll(spaceFontData.providers)
                }

            // Load textures and other supporting files
            misc(
                Path("assets/space/font/default.json"),
                ResourcePackBuilder::class.java.getResourceAsStream("/NegativeSpaceFont/assets/space/font/default.json")!!,
            )
            misc(
                Path("assets/space/lang/en_us.json"),
                ResourcePackBuilder::class.java.getResourceAsStream("/NegativeSpaceFont/assets/space/lang/en_us.json")!!,
            )
            misc(
                Path("assets/space/textures/font/splitter.png"),
                ResourcePackBuilder::class.java.getResourceAsStream("/NegativeSpaceFont/assets/space/textures/font/splitter.png")!!,
            )
        }

        // Create font metadata file
        if (fontData.providers.isNotEmpty()) {
            misc(Path("assets/minecraft/font/default.json"), gson.toJson(fontData).byteInputStream(Charsets.UTF_8))
        }

        // Create translation key file
        if (translationKeys.isNotEmpty()) {
            misc(
                Path("assets/gametools/lang/en_us.json"),
                gson.toJson(translationKeys).byteInputStream(Charsets.US_ASCII),
            )
        }

        // Zip pack
        zipDirectory(buildOutputDirectory.toFile(), outputPath.toFile())

        // Compute hash
    }
}

private fun withPng(str: String): String {
    return if (!str.endsWith(".png")) "$str.png" else str
}

private fun charFromIndex(index: Int): Char {
    return '\uE100' + index
}
