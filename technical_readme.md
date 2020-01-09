# The Quest for Success - Technical Description

## Architecture
The game uses [libgdx](https://libgdx.badlogicgames.com/) with Artemis-ODB to facilitate a entity-component-system architecture.
This type of game architecture avoids object-oriented composition within entities--rather, it uses a composition-oriented
approach to add behavior to entities. This avoids illogical composition hierarchies while keeping entity behavior 
separated from entity data.

Entities only contain data such as position or sprites. Systems operate on groups of entities with specific components. For
example, PhysicsSystem only acts on entities with both components 

## Map System
