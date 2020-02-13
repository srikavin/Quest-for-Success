# The Quest for Success - Technical Description

## Architecture
The game uses [libgdx](https://libgdx.badlogicgames.com/) with Artemis-ODB to facilitate a entity-component-system architecture.
This type of game architecture avoids object-oriented composition within entities--rather, it uses a composition-oriented
approach to add behavior to entities. This avoids illogical composition hierarchies while keeping entity behavior 
separated from entity data.

Entities only contain references to components. Components are solely "bags" of data such as position or sprites.
Systems operate on groups of entities with specific components. For example, PhysicsSystem only acts on 
entities with both PhysicsBody and Transform components. 

By using libgdx, this game can be easily ported to other platforms including iOS, Android, and the web--it's only a
matter of reconfiguring the control scheme.

## Map System

Maps are stored in the layered TILE format. The maps and their associated assets are loaded. Tiled Maps may contain any 
of the following layers:
 * Foreground - Images to render in front of the background
 * Background - Images to display in the background with a parallax effect
 * Collision - Contains all collision boxes on the map
 * Trigger - Contains all map triggers and their associated properties; all objects in this layer must have the property
   `type` with a value defined in trigger type. All triggers must be rectangular map objects

## UI System

The user interface of the game utilizes Scene2D. All interfaces are defined in code. Styling (called skins) are loaded 
from the assets folder during start up. Multiple systems have different Stages that they render to, allowing
for decoupled UI.

## Animations

Animations are made within Asesprite, and are exported into a JSON format, which is then parsed to load animations from
a spritesheet. This along with Asesprite automation allows for faster prototyping when developing new features.

## Minigame System

The minigame system was designed to be extensible. New minigames are just a matter of extending the Minigame abstract
class and implementing the required methods.
 
Dialogue minigames are entirely scripted. They were implemented separately of the game before being included. This
was possible due to the use of Kotlin blocking coroutines to create an seamless API surface to use. Dialogue coroutines
are run in a coroutine and communicate through a series of Packets with the main thread using a Channel.