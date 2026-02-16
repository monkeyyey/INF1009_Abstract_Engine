# INF1009 Abstract Engine Project

A libGDX project with a reusable engine layer (`engine`) and game/demo layer (`game`).

## Project Structure

- `core/src/main/java/com/myGame/engine/`
  Engine abstractions (entities, managers, physics, scenes, core interfaces).
- `core/src/main/java/com/myGame/game/`
  Game/demo entities, scenes, input sources, and simple UI elements.
- `core/src/main/java/com/myGame/GameEngine.java`
  Application entry point and global scene/input flow.
- `assets/`
  Textures and media assets used by scenes.

## Innovative Enhancements

- Scene cycle orchestration in `SceneManager`
  - `registerCycleScene(scene)` and `cycleScene()` allow cycling demo scenes without hardcoding toggle state in `GameEngine`.
  - This reduces duplicated state and centralizes scene-switch policy.
- Layered scene stack with pause overlay
  - `pushScene(pause)` overlays pause on top of gameplay scene.
  - `popScene()` returns to previous scene state without rebuilding entities.
- Unified manager pipeline in base `Scene`
  - Default update order: movement -> collision -> lifecycle cleanup.
  - Keeps orchestration reusable across different scenes.
- Unified input abstraction
  - Different input devices map to `InputState` via `InputSource` implementations.
  - Scene logic consumes one state model regardless of device.
- Hitbox geometry decoupled from world position
  - `RectHitbox` stores only width/height, `CircleHitbox` stores only radius.
  - World position comes from `Entity`, keeping geometry data clean.
- Collision ownership split by responsibility
  - `CollisionManager` handles pair detection and dispatch only.
  - Domain responses stay inside each entity’s `onCollision` (for example, wall color changes, circle rollback, droplet/wind interaction).
- Generic entity collections for systems
  - `MovementManager` and `CollisionManager` both consume `Collection<Entity>` rather than scene-maintained specialized lists.
  - This keeps `EntityManager` focused on storage while systems decide who to process.
- Scene state persistence through reuse
  - Demo scenes are created once and reused when cycling.
  - Object state (positions, velocities, spawned entities) is retained across scene switches.
- Input layering on top of shared manager
  - Scenes register/unregister their own input sources in lifecycle hooks.
  - Pause flow adds temporary pause-specific sources without replacing engine-level input architecture.
- Reusable UI as first-class entities
  - `Button` and `VolumeSlider` extend `Entity`, so UI follows the same lifecycle/render model as gameplay objects.
  - This creates a consistent object model across gameplay and menu systems.

## Architecture Map (Inheritance / Implementation)

### Engine Core

- `Collidable` (interface)
- `Movable` (interface)
- `InputSource` (interface)
- `InputState` (class)

### Engine Entities

- `Entity` (abstract class)
- `Hitbox` (abstract class)
  - `RectHitbox extends Hitbox`
  - `CircleHitbox extends Hitbox`

### Engine Scene Base

- `Scene` (abstract class)

### Game Entities

- `MovableTextureObject extends Entity implements Collidable, Movable` (abstract class)
  - `Bucket extends MovableTextureObject`
  - `Droplet extends MovableTextureObject`
  - `Wind extends MovableTextureObject`
- `PlayerCircle extends Entity implements Collidable, Movable`
- `RectangleWall extends Entity implements Collidable`
- `StaticTextureEntity extends Entity`

### UI Entities

- `Button extends Entity`
- `VolumeSlider extends Entity`

### Game Scenes

- `DemoScene1 extends Scene`
- `DemoScene2 extends Scene`
- `PauseScene extends Scene`

### Input Sources

- `KeyboardInputSource implements InputSource`
- `KeyboardArrowInputSource implements InputSource`
- `KeyboardWASDInputSource implements InputSource`
- `MouseInputSource implements InputSource`
- `MouseClickInputSource implements InputSource`
- `PauseInputSource implements InputSource`

## Engine Layer

### Core Interfaces and State

- `engine/core/Collidable.java`
  Collision contract: `getHitbox`, `setHitbox`, `onCollision`.
- `engine/core/Movable.java`
  Movement contract: `updatePosition` and velocity getters/setters.
- `engine/core/InputSource.java`
  Contract for populating an `InputState` each frame.
- `engine/core/InputState.java`
  Unified input data:
  - movement flags (`up/down/left/right`)
  - action flags (`action1/action2/pause`)
  - pointer fields (`pointerX/pointerY/justTouched`)

### Entities and Hitboxes

- `engine/entities/Entity.java`
  Abstract base entity with:
  - position (`x`, `y`)
  - logical identity (`name`)
  - active flag + `destroy()`
  - two draw hooks (`draw(SpriteBatch)`, `draw(ShapeRenderer)`) as no-op defaults
  - abstract `dispose()`
- `engine/entities/Hitbox.java`
  Abstract hitbox base type.
- `engine/entities/RectHitbox.java`
  Rectangle hitbox with width/height only.
- `engine/entities/CircleHitbox.java`
  Circle hitbox with radius only.

### Managers

- `engine/managers/EntityManager.java`
  Owns entity storage (`Map<String, Entity>`), draw passes, and inactive-entity cleanup.
- `engine/managers/InputManager.java`
  Owns input source/state maps keyed by `int` ID:
  - `addInputSource(id, source)`
  - `removeInputSource(id)`
  - `update()`
  - `getState(id)`
- `engine/managers/SceneManager.java`
  Stack-based scene orchestration:
  - `setScene`, `pushScene`, `popScene`, `getActiveScene`
  - `update`, `render`, `dispose`
  - cycle support: `registerCycleScene(scene)` and `cycleScene()`
- `engine/managers/OutputManager.java`
  Sound map holder and playback/dispose helper (sound loading is currently a stub).

### Physics

- `engine/physics/MovementManager.java`
  `update(dt, Collection<Entity>)` and updates active entities implementing `Movable`.
- `engine/physics/CollisionDetector.java`
  Geometry overlap checks:
  - `overlaps(...)`
  - `checkCircleCircle(...)`
  - `checkRectRect(...)`
  - `checkCircleRect(...)`
- `engine/physics/CollisionManager.java`
  `update(dt, Collection<Entity>)`:
  - filters active `Collidable` entities
  - checks hitbox overlap using `CollisionDetector`
  - dispatches `onCollision` to both entities

### Scene Base

- `engine/scenes/Scene.java`
  Abstract scene base with:
  - `EntityManager`, `MovementManager`, `CollisionManager`
  - lifecycle hooks `onEnter`, `onExit`, `dispose`
  - default update pipeline: movement -> collision -> entity cleanup

## Game Layer

### Game Entities

- `game/entities/MovableTextureObject.java`
  Abstract textured entity implementing both `Collidable` and `Movable`.
- `game/entities/Bucket.java`
  Horizontal movable catcher with X clamp.
- `game/entities/Wind.java`
  2D movable wind area with bounds clamp; affects droplets on collision.
- `game/entities/Droplet.java`
  Falling droplet with wind interaction and catch/reset state.
- `game/entities/PlayerCircle.java`
  Shape-rendered circle player with bounds clamp and rollback-on-wall-collision behavior.
- `game/entities/RectangleWall.java`
  Static collidable rectangle; changes color on player collision.
- `game/entities/StaticTextureEntity.java`
  Non-collidable textured entity (used for background).

### UI Entities

- `game/ui/Button.java`
  Shape-rendered clickable button with label and `Runnable` callback.
- `game/ui/VolumeSlider.java`
  Shape-rendered slider bar with clamped value in `[0, 1]`.

### Input Sources

- `game/input/KeyboardInputSource.java`
  Combined arrows + WASD directional input.
- `game/input/KeyboardArrowInputSource.java`
  Arrow-only directional input (+ space as action1).
- `game/input/KeyboardWASDInputSource.java`
  WASD directional input.
- `game/input/MouseInputSource.java`
  Converts pointer-to-entity direction into movement flags.
- `game/input/MouseClickInputSource.java`
  Updates pointer X/Y and `justTouched`.
- `game/input/PauseInputSource.java`
  Pause menu directional input + ESC as `action1`.

### Scenes

- `game/scenes/DemoScene1.java`
  Bucket + wind + droplets scene with cloud background.
- `game/scenes/DemoScene2.java`
  Two circle players (arrow + mouse) and rectangle walls.
- `game/scenes/PauseScene.java`
  Pause overlay with volume slider and Resume/Quit buttons.

## Runtime Flow

- `GameEngine.create()` builds shared managers/scenes and sets initial scene to `DemoScene1`.
- `TAB` triggers `sceneManager.cycleScene()` across registered demo scenes.
- `ESC` (when not paused) pushes `PauseScene` on top of current scene.
- Only active top scene updates/renders.
- Demo scenes are reused (not recreated), so their in-memory object state persists across switches.

## Controls

### Global

- `TAB` -> cycle demo scenes
- `ESC` -> push pause scene (from non-pause scenes)

### DemoScene1

- Bucket: arrow keys
- Wind: WASD

### DemoScene2

- Green circle: arrow keys
- Yellow circle: mouse-direction input (`MouseInputSource`)

### PauseScene

- `UP` / `RIGHT`: increase volume slider
- `DOWN` / `LEFT`: decrease volume slider
- `ESC` (`action1`): resume
- Mouse click:
  - `RESUME` button: resume
  - `QUIT` button: exit app

## Build and Run

### Prerequisites

- Java 17+

### Run (desktop)

From project root:

```bash
./gradlew lwjgl3:run
```

Windows PowerShell:

```powershell
./gradlew.bat lwjgl3:run
```
