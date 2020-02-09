package me.srikavin.fbla.game.ext

import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align

fun Table.addImageTextButton(text: String?, image: Drawable?, clicked: Runnable, styleName: String): Cell<TextButton> {
    val button = TextButton(text, skin, styleName)
    if (image != null) {
        button.add(Image(image)).size(image.minWidth)
        button.cells.reverse()
    }
    button.clicked(clicked)
    return add(button)
}


fun Table.addImageTextButton(text: String?, image: Drawable?, clicked: Runnable): Cell<TextButton> {
    val button = TextButton(text, skin)
    if (image != null) {
        button.add(Image(image)).size(image.minWidth)
        button.cells.reverse()
    }
    button.clicked(clicked)
    return add(button)
}

fun Table.addCenteredImageTextButton(text: String?, image: Drawable?, imagesize: Float, clicked: Runnable): Cell<TextButton> {
    val button = TextButton(text, skin)
    button.add(Image(image)).size(imagesize)
    button.cells.reverse()
    button.clicked(clicked)
    button.labelCell.padLeft(-imagesize)
    return add(button)
}

fun Table.addCenteredImageTextButton(text: String?, image: Drawable, clicked: Runnable): Cell<TextButton> {
    val button = TextButton(text, skin)
    button.add(Image(image))
    button.cells.reverse()
    button.clicked(clicked)
    button.labelCell.padLeft(-image.minWidth)
    return add(button)
}

fun Table.table(): Cell<Table> {
    return table(null)
}

fun Table.table(background: Drawable?): Cell<Table> {
    val table: Table = Table(skin).background(background)
    return add(table)
}

fun Table.table(callable: (Table) -> Unit): Cell<Table> {
    val table = Table(skin)
    callable(table)
    return add(table)
}

fun Table.table(background: Drawable?, callable: (Table) -> Unit): Cell<Table> {
    return table(background, Align.center, callable)
}

fun Table.table(background: Drawable?, align: Int, callable: (Table) -> Unit): Cell<Table> {
    val table = Table(skin).background(background)
    table.align(align)
    callable(table)
    return add(table)
}