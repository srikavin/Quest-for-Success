package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.managers.TagManager
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import me.srikavin.fbla.game.ecs.component.DisableInput
import me.srikavin.fbla.game.ecs.component.MinigameComponent

@All(MinigameComponent::class)
class MinigameSystem : IteratingSystem() {
    lateinit var minigameMapper: ComponentMapper<MinigameComponent>
    private val disableInput = DisableInput()

    override fun process(entityId: Int) {
        val minigameComponent: MinigameComponent = minigameMapper[entityId]
        val minigame = minigameComponent.minigame ?: return

        val player = world.getSystem(TagManager::class.java).getEntityId("PLAYER")
        if (minigame.active) {
            if (!minigame.allowPlayerMovement()) {
                world.edit(player).add(disableInput)
            }

            minigame.process(Gdx.graphics.deltaTime)
        } else if (minigame.allowPlayerMovement()) {
            // If the minigame is not active make sure that player input is not disabled
            world.edit(player).remove(disableInput)
        }
    }

}