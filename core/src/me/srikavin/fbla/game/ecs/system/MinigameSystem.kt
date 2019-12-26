package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import me.srikavin.fbla.game.ecs.component.MinigameComponent

@All(MinigameComponent::class)
class MinigameSystem : IteratingSystem() {
    lateinit var minigameMapper: ComponentMapper<MinigameComponent>

    override fun process(entityId: Int) {
        val minigameComponent: MinigameComponent = minigameMapper[entityId]
        val minigame = minigameComponent.minigame ?: return

        if (minigame.isActive()) {
            minigame.process(Gdx.graphics.deltaTime)
        }
    }

}