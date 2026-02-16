# INF1009 Abstract Engine Project

This is a libGDX project with a reusable engine layer (`engine`) and demo/game logic (`game`).

## Project Structure

- `core/src/main/java/com/myGame/engine/`  
  Reusable engine abstractions (entities, managers, physics, scene flow, input interfaces).
- `core/src/main/java/com/myGame/game/`  
  Demo-specific entities, scenes, and input sources.
- `core/src/main/java/com/myGame/GameEngine.java`  
  App entry point, global controls, and scene switching.
- `assets/`  
  Textures used by demo scenes.

## Engine Layer

### Core Interfaces
- `engine/core/Collidable.java`  
  Collision contract (`getHitbox`, `setHitbox`, `onCollision`).
- `engine/core/Movable.java`  
  Movement contract (`updatePosition`, velocity getters/setters).
- `engine/core/InputSource.java`  
  Input polling contract.
- `engine/core/InputState.java`  
  Unified input data (movement/action flags + pointer click fields).

### Entities / Hitboxes
- `engine/entities/Entity.java`  
  Base abstract entity with position, name, active state, and draw overloads.
- `engine/entities/Hitbox.java`  
  Base hitbox type.
- `engine/entities/RectHitbox.java` / `engine/entities/CircleHitbox.java`  
  Shape-only hitboxes (no x/y stored in hitbox).

### Managers
- `engine/managers/EntityManager.java`  
  Entity storage, draw pass helpers, lifecycle removal/dispose.
- `engine/managers/InputManager.java`  
  Input source registry by ID and per-ID `InputState`.
- `engine/managers/SceneManager.java`  
  Active scene stack (`setScene`, `pushScene`, `popScene`), update/render routing.

### Physics
- `engine/physics/MovementManager.java`  
  Updates active `Movable` entities from a collection of `Entity`.
- `engine/physics/CollisionDetector.java`  
  Static geometry checks (`circle-circle`, `rect-rect`, `circle-rect`).
- `engine/physics/CollisionManager.java`  
  Finds colliding `Collidable` entity pairs and dispatches `onCollision`.

### Scene Base
- `engine/scenes/Scene.java`  
  Base scene pipeline: movement -> collision -> entity lifecycle update.

## Game Layer

### Demo Entities
- `game/entities/MovableTextureObject.java`  
  Abstract textured movable+collidable base (used by bucket/droplet/wind).
- `game/entities/Bucket.java`  
  Arrow-key controlled catcher.
- `game/entities/Droplet.java`  
  Falling droplet with wind reaction + catch/reset behavior.
- `game/entities/Wind.java`  
  WASD-controlled wind zone that affects droplets on collision.
- `game/entities/PlayerCircle.java`  
  Circle player with bounds clamp and rectangle collision rollback.
- `game/entities/RectangleWall.java`  
  Static rectangle wall.
- `game/entities/StaticTextureEntity.java`  
  Non-collidable textured entity (used for background).

### UI Entities
- `game/ui/Button.java`  
  Clickable UI button entity with bound click action.
- `game/ui/VolumeSlider.java`  
  Slider entity for pause-screen volume visualization.

### Input Sources
- `game/input/KeyboardArrowInputSource.java`
- `game/input/KeyboardWASDInputSource.java`
- `game/input/MouseInputSource.java` (entity-follow directional input)
- `game/input/PauseInputSource.java` (pause movement/action mapping)
- `game/input/MouseClickInputSource.java` (pointer X/Y + justTouched)

### Scenes
- `game/scenes/DemoScene1.java`  
  Bucket + wind + droplets scene with cloud background.
- `game/scenes/DemoScene2.java`  
  Circle-vs-rectangle scene (arrow player + mouse player).
- `game/scenes/PauseScene.java`  
  Pause overlay with volume slider and Resume/Quit buttons.

## Runtime / Scene Behavior

- `TAB` switches between `DemoScene1` and `DemoScene2`.
- `ESC` pushes `PauseScene`.
- Demo scenes are created once and reused, so scene object state is retained across `TAB` switches.
- Only the active scene updates/renders at runtime.

## Controls

### Global
- `TAB` -> swap DemoScene1/DemoScene2
- `ESC` -> open pause scene (from demo scenes)

### DemoScene1
- Bucket: Arrow keys
- Wind: WASD

### DemoScene2
- Green circle: Arrow keys
- Yellow circle: Mouse-follow directional input

### PauseScene
- `UP` / `RIGHT`: increase volume slider
- `DOWN` / `LEFT`: decrease volume slider
- `ESC` (`action1`): resume
- Mouse click:
  - `RESUME` button -> resume
  - `QUIT` button -> exit app

## Build / Run

### Prerequisites
- Java 17+

### Run desktop app
From project root:

```bash
./gradlew lwjgl3:run
```

On Windows PowerShell:

```powershell
./gradlew.bat lwjgl3:run
```
