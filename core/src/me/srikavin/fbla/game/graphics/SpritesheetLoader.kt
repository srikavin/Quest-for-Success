package me.srikavin.fbla.game.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import ktx.log.info
import com.badlogic.gdx.utils.Array as GdxArray

class SpritesheetLoader {
    private val jsonReader = JsonReader()

    fun loadAsespriteSheet(imgPath: String, propertiesPath: String, animationName: String): Animation<TextureRegion> {
        return loadAsespriteSheet(Texture(Gdx.files.internal(imgPath)), jsonReader.parse(Gdx.files.internal(propertiesPath)), animationName)
    }

    fun loadAsespriteSheet(img: Texture, properties: JsonValue, animationName: String): Animation<TextureRegion> {
        val animation = properties.get("meta")?.get("frameTags")?.find { e -> animationName == e["name"]?.asString() }
                ?: throw RuntimeException("Animation $animationName does not exist!")


        img.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)

        val from = animation["from"].asInt()
        val to = animation["to"].asInt()

        val regions = GdxArray<TextureRegion>((to - from) + 1)

        for (i in from..to) {
            val pos = properties["frames"][i]["frame"]
            val x = pos["x"].asInt();
            val y = pos["y"].asInt();
            val w = pos["w"].asInt();
            val h = pos["h"].asInt();
            regions.add(TextureRegion(img, x, y, w, h))
        }

        info { "Loaded animation ($animationName) with ${regions.size} frames" }

        return Animation(.100f, regions);
    }


}