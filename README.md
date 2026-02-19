# INF1009 Abstract Engine Project

A libGDX simulation project organized into a reusable engine layer (`engine`) and simulation-specific layer (`simulation`).

## Project Structure

- `core/src/main/java/com/myGame/engine/`
  Engine abstractions: entities, managers, physics, scenes, and core contracts.
- `core/src/main/java/com/myGame/simulation/`
  Simulation/demo entities, scenes, input sources, and UI entities.
- `core/src/main/java/com/myGame/GameEngine.java`
  Application entry point and global scene/input/audio flow.
- `assets/`
  Textures and audio assets.

## Innovative Enhancements

- Scene cycle orchestration
  - `SceneManager.registerCycleScene(scene)` + `SceneManager.cycleScene()`.
  - Removes hardcoded scene toggling logic from `GameEngine`.
- Layered pause overlay
  - `pushScene(pause)` overlays pause on top of gameplay.
  - Resume returns to the exact prior game state.
- Unified scene update pipeline
  - Base `Scene.update(...)`: movement -> collision -> entity lifecycle cleanup.
- Generic system processing over `Collection<Entity>`
  - `MovementManager` and `CollisionManager` operate on generic entity collections.
- Decoupled hitbox geometry
  - Hitboxes store only shape dimensions/radius.
  - World position comes from `Entity`.
- Input abstraction with pluggable sources
  - `InputSource` implementations feed a unified `InputState`.
  - Includes keyboard, mouse-direction, mouse-click, pause input, and custom key mapping.
- Reusable UI entities
  - `Button` and `VolumeSlider` extend `Entity` and follow the same lifecycle style.
- Audio scene orchestration
  - `AudioManager` handles SFX + background music switching and volume control.
  - Demo scenes request their own background track on `onEnter()`.
- Gameplay objective loop in DemoScene1
  - Score/timer objective (catch 100 droplets), completion timing, and restart flow (`R`).

## OOP Principles in This Engine

### Abstraction

- `Entity` abstracts common position/lifecycle behavior.
- `Scene` abstracts scene lifecycle and update pipeline.
- `Hitbox` abstracts collision geometry type.

### Encapsulation

- Core state is hidden behind methods (for example `Entity` position, active state).
- `EntityManager` encapsulates entity storage and cleanup rules.
- `AudioManager` encapsulates sound/music loading, switching, and volume logic.

### Inheritance

- `MovableTextureObject extends Entity` to reuse textured movable/collidable behavior.
- Game entities specialize behavior by overriding `onCollision`, `draw`, or movement logic.

### Composition

- `Scene` composes managers (`EntityManager`, `MovementManager`, `CollisionManager`).
- `GameEngine` composes scene instances and managers.
- Scenes compose entities and input sources.

### Interfaces and Abstract Classes

- Interfaces: `InputSource`, `iMovable`, `iCollidable`.
- Abstract classes: `Entity`, `Scene`, `Hitbox`, `MovableTextureObject`.

### Polymorphism

- `EntityManager` draws mixed entity types via polymorphic `draw(...)`.
- Physics managers process `Entity` collections and branch by interface contracts (`iMovable`, `iCollidable`).
- `InputManager` updates different `InputSource` implementations through one interface.

## Architecture Map (Inheritance / Implementation)

### Engine Core

- `iCollidable` (interface)
- `iMovable` (interface)
- `InputSource` (interface)
- `InputState` (class)

### Engine Entities

- `Entity` (abstract class)
- `Hitbox` (abstract class)
  - `RectHitbox extends Hitbox`
  - `CircleHitbox extends Hitbox`

### Engine Scene Base

- `Scene` (abstract class)

### Simulation Entities

- `MovableTextureObject extends Entity implements iCollidable, iMovable` (abstract)
  - `Bucket extends MovableTextureObject`
  - `Droplet extends MovableTextureObject`
  - `Wind extends MovableTextureObject`
- `PlayerCircle extends Entity implements iCollidable, iMovable`
- `RectangleWall extends Entity implements iCollidable`
- `StaticTextureEntity extends Entity`

### UI Entities

- `Button extends Entity`
- `VolumeSlider extends Entity`

### Simulation Scenes

- `DemoScene1 extends Scene`
- `DemoScene2 extends Scene`
- `PauseScene extends Scene`

### Input Sources

- `KeyboardInputSource implements InputSource`
- `KeyboardArrowInputSource implements InputSource`
- `KeyboardWASDInputSource implements InputSource`
- `KeyboardCustomInputSource implements InputSource`
- `MouseInputSource implements InputSource`
- `MouseClickInputSource implements InputSource`
- `PauseInputSource implements InputSource`

## Engine Layer

### Core Contracts

- `engine/core/iCollidable.java`
  - `getHitbox`, `setHitbox`, `onCollision`
- `engine/core/iMovable.java`
  - `updatePosition(float dt)`
- `engine/core/InputSource.java`
  - `updateState(InputState state)`
- `engine/core/InputState.java`
  - movement/action/pause flags + pointer position/click state

### Managers

- `engine/managers/EntityManager.java`
  - entity storage by name, draw passes, inactive cleanup
- `engine/managers/InputManager.java`
  - input source registration and per-id state updates
- `engine/managers/SceneManager.java`
  - stack-based scene control + cycle-scene support
- `engine/managers/AudioManager.java`
  - SFX/music loading, playback, track switching, and volume control

### Physics

- `engine/physics/MovementManager.java`
  - `update(dt, Collection<Entity>)`
- `engine/physics/CollisionDetector.java`
  - shape overlap checks (`circle-circle`, `rect-rect`, `circle-rect`)
- `engine/physics/CollisionManager.java`
  - `update(Collection<Entity>)` and bidirectional collision dispatch

### Scene Base

- `engine/scenes/Scene.java`
  - owns managers and shared update pipeline
  - lifecycle hooks: `onEnter`, `onExit`, `dispose`

## Simulation Layer

### DemoScene1 Highlights

- Background + bucket + wind + droplets.
- Objective gameplay loop:
  - catch 100 droplets
  - timer runs until completion
  - completion time shown
  - droplets are removed on completion
  - press `R` to restart
- Droplet catch SFX via `AudioManager.playSound("water_droplet")`.

### DemoScene2 Highlights

- Two player circles + rectangle walls.
- Arrow-controlled green player and mouse-driven yellow player.
- Intense background music on scene entry.

### PauseScene Highlights

- Overlay menu with:
  - `RESUME` and `QUIT` buttons
  - volume slider
- Adjusts live music volume (keyboard or click-on-slider).
- Keeps current music track active while paused.

## Runtime Flow

- `GameEngine.create()` initializes managers, loads audio assets, creates scenes.
- Initial scene: `DemoScene1`.
- `TAB`: cycles registered demo scenes.
- `ESC` (outside pause): pushes `PauseScene`.
- Only top scene updates/renders.
- Scene objects are reused, so in-memory state persists across switches.

## Controls

### Global

- `TAB` -> cycle demo scenes
- `ESC` -> open pause scene

### DemoScene1

- Bucket: Arrow keys
- Wind: WASD
- Restart run: `R` (mapped to `action2`)

### DemoScene2

- Green circle: Arrow keys
- Yellow circle: mouse-direction input

### PauseScene

- `UP` / `RIGHT`: increase volume
- `DOWN` / `LEFT`: decrease volume
- `ESC` (`action1`): resume
- Mouse click:
  - `RESUME` button -> resume
  - `QUIT` button -> exit
  - slider track -> set volume by click position

## Current Limitations (Design Tradeoffs)

- Scene classes still carry multiple responsibilities
  - Scene setup, per-frame game rules, input wiring, and HUD rendering are in the same classes.
- Collision extensibility
  - `CollisionDetector` uses explicit type checks; adding new shape types requires editing detector logic.
- `iCollidable` breadth
  - `setHitbox(...)` is required for all collidables even if some should be immutable.
- Input manager keying
  - `InputManager` uses integer IDs; this works but can become fragile as projects scale.
- Audio manager scope
  - `AudioManager` currently handles both SFX and music (acceptable here, but split managers may scale better).
- Engine lacks dedicated service boundaries for HUD/game rules
  - No separate gameplay systems layer yet (for example score service, round controller, HUD renderer).

## Build and Run

### Prerequisites

- Java 17+

### Run desktop app

From project root:

```bash
./gradlew lwjgl3:run
```

Windows PowerShell:

```powershell
./gradlew.bat lwjgl3:run
```
