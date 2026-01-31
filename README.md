# INF1009 Abstract Engine Project

This is a libGDX project with a small abstract engine layer and two demo scenes. The code is organized to separate reusable engine concepts (abstract) from game/demo logic.

## Project Structure

- `core/`
  - `src/main/java/com/myGame/engine/` -> Abstract engine layer (reusable)
  - `src/main/java/com/myGame/game/` -> Demo/game-specific logic
  - `src/main/java/com/myGame/GameEngine.java` -> Application entry point and scene switching
- `assets/` -> Textures and assets used by demos

## Abstract Engine Portion

These classes form the generic engine layer. They are not tied to a specific demo.

### Core Interfaces
- `core/src/main/java/com/myGame/engine/core/Collidable.java`
  - Interface for objects that can collide; exposes `getHitbox`, `setHitbox`, `onCollision`.
- `core/src/main/java/com/myGame/engine/core/Movable.java`
  - Interface for objects that can move; exposes velocity and `updatePosition`.
- `core/src/main/java/com/myGame/engine/core/InputSource.java`
  - Interface for input polling.
- `core/src/main/java/com/myGame/engine/core/InputState.java`
  - Data container for input states (up/down/left/right/action).

### Entities and Hitboxes
- `core/src/main/java/com/myGame/engine/entities/Entity.java`
  - Base Abstract entity with position, lifecycle, and drawing interface.
- `core/src/main/java/com/myGame/engine/entities/MovableEntity.java`
  - Abstract entity that is both `Movable` and `Collidable`; owns hitbox and velocity.
- `core/src/main/java/com/myGame/engine/entities/Hitbox.java`
  - Base hitbox type with position.
- `core/src/main/java/com/myGame/engine/entities/RectHitbox.java`
  - Rectangle hitbox; collision logic delegated to `CollisionDetector`.
- `core/src/main/java/com/myGame/engine/entities/CircleHitbox.java`
  - Circle hitbox; collision logic delegated to `CollisionDetector`.

### Managers and Systems
- `core/src/main/java/com/myGame/engine/managers/EntityManager.java`
  - Stores entities, draws them, removes inactive entities, exposes collidables/movables.
- `core/src/main/java/com/myGame/engine/managers/InputManager.java`
  - Stores input sources per player ID and updates `InputState` each frame.
- `core/src/main/java/com/myGame/engine/managers/SceneManager.java`
  - Scene stack (set/push/pop), updates and renders active scene.
- `core/src/main/java/com/myGame/engine/managers/OutputManager.java`
  - Placeholder for audio management.
- `core/src/main/java/com/myGame/engine/physics/MovementManager.java`
  - Updates all `Movable` entities each frame.
- `core/src/main/java/com/myGame/engine/physics/CollisionDetector.java`
  - Static collision checks (circle-circle, rect-rect, circle-rect).
- `core/src/main/java/com/myGame/engine/physics/CollisionManager.java`
  - Detects collisions between collidables and dispatches `onCollision`.
- `core/src/main/java/com/myGame/engine/scenes/Scene.java`
  - Base scene class wiring entity, movement, and collision managers.

## Demo/Game Logic

These classes are specific to the demo scenes.

### Game Entities
- `core/src/main/java/com/myGame/game/entities/MovableTextureObject.java`
  - Textured movable entity (base for bucket, droplet, wind).
- `core/src/main/java/com/myGame/game/entities/Bucket.java`
  - Controlled by arrow keys; moves left/right and catches droplets.
- `core/src/main/java/com/myGame/game/entities/Droplet.java`
  - Falls downward; respawns when caught or when leaving the screen.
- `core/src/main/java/com/myGame/game/entities/Wind.java`
  - Controlled by WASD; pushes droplets sideways or holds them when on top.
- `core/src/main/java/com/myGame/game/entities/MovableCircle.java`
  - Simple circle-based movable entity.
- `core/src/main/java/com/myGame/game/entities/PlayerCircle.java`
  - Player-controlled circle with screen bounds and color.
- `core/src/main/java/com/myGame/game/entities/RectangleWall.java`
  - Static rectangle that changes color on contact in DemoScene2.

### Input Sources
- `core/src/main/java/com/myGame/game/input/KeyboardArrowInputSource.java`
  - Arrow-key input mapping.
- `core/src/main/java/com/myGame/game/input/KeyboardWASDInputSource.java`
  - WASD input mapping.

### Scenes
- `core/src/main/java/com/myGame/game/scenes/DemoScene1.java`
  - Bucket + 10 droplets + wind interaction demo.
- `core/src/main/java/com/myGame/game/scenes/DemoScene2.java`
  - Two player circles + 3 rectangles that change color on contact.
- `core/src/main/java/com/myGame/game/scenes/PauseScene.java`
  - Pause menu (Resume / Quit) shown with `ESC`.

### Application Entry
- `core/src/main/java/com/myGame/GameEngine.java`
  - Initializes managers, handles global input (`TAB` swaps scenes, `ESC` pause), and runs the game loop.

## How to Download and Run in VS Code

### 1) Get the project
Option A: clone with Git
```
git clone <YOUR_REPO_URL>
cd AbstractEngineProject
```

Option B: download ZIP from GitHub and extract it, then open the folder in VS Code.

### 2) Install prerequisites
- Java JDK 17 (or newer)
- VS Code extensions:
  - “Extension Pack for Java” (recommended)

### 3) Open in VS Code
- Open the project folder in VS Code.
- Let VS Code finish indexing and Gradle import.

### 4) Run the desktop app
From the VS Code terminal in the project root:
```
./gradlew lwjgl3:run
```

On Windows PowerShell:
```
./gradlew.bat lwjgl3:run
```

### 5) Controls
- `TAB` -> switch between DemoScene1 and DemoScene2
- `ESC` -> pause menu
- DemoScene1:
  - Bucket: Arrow keys
  - Wind: WASD
- DemoScene2:
  - Green circle: Arrow keys
  - Yellow circle: WASD

## Notes
- Assets live in `assets/` and are loaded by entity constructors.
- If Gradle fails to download dependencies, make sure you have internet access and try re-running the command.
