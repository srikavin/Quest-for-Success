package me.srikavin.fbla.game.ecs.system

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * Responsible for updating the camera's projection matrix and setting up the screen to render the frame
 */
class RenderSystem : BaseSystem() {
    @Wire
    private lateinit var camera: OrthographicCamera
    @Wire
    private lateinit var batch: SpriteBatch

    override fun processSystem() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(
                GL20.GL_COLOR_BUFFER_BIT
                        or GL20.GL_DEPTH_BUFFER_BIT
                        or (if (Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0)
        )
        camera.update()
        batch.projectionMatrix = camera.combined
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
    }
}
