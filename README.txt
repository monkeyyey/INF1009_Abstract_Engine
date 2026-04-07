AbstractEngineProject
=====================

Overview
--------
This project is a LibGDX-based Java game project with two main parts:

- an abstract engine that provides reusable game-management systems
- a game-specific implementation, Math Bomber, built on top of that engine


Portion 1: Abstract Engine
--------------------------
The purpose of the abstract engine is to provide the core functionality of an interactive game or simulation through reusable components that support scalability and flexibility across different applications. In this project, it serves as the reusable framework that the Math Bomber game is built on top of.

The engine follows a class-based architecture with clearly separated management systems, each handling a single concern. These concerns include scene management, entity management, movement, collision, input, audio, lifecycle handling, and animation. This structure keeps the engine understandable, modular, and reusable.

At a high level, the overall system follows a layered architectural pattern:
- the `lwjgl3` launcher acts as the platform layer
- `GameEngine` acts as the application coordination layer
- the `engine` folder contains the reusable abstract engine layer
- the `game` folder contains the game-specific implementation layer
- the `game/mathbomber` folder acts as a domain-logic sublayer for the Math Bomber rules and state

Compared with the earlier phase of the project, the engine was improved for:
- clearer separation of concerns
- looser coupling between components
- stronger alignment with OOP and SOLID principles
- better long-term scalability
- generic animation handling
- improved shared audio control

Key engine improvements include:
- improved engine file structure by grouping classes by concern
- moving collision geometry logic into concrete hitbox implementations
- manager injection through scene constructors to reduce coupling
- separate abstract scene types for static scenes and action scenes
- addition of an animation management system
- addition of a timed lifecycle management system for `iTickable` entities
- shared music and SFX volume support across scenes

The main engine systems are:
- Scene Management System
  - manages scene lifecycle through a common `Scene` abstraction
  - supports both scene stacking and scene cycling
- Entity Management System
  - maintains a shared registry of active entities
  - supports centralized update, rendering, and lifecycle control
- Movement Management System
  - updates all entities implementing `iMovable`
- Collision Management System
  - detects collisions generically through entity-owned hitboxes and `iCollidable`
- Timed Lifecycle Management System
  - updates time-based entities implementing `iTickable`
- Input Management System
  - translates concrete device input into a common `InputState`
- Audio Management System
  - centralizes audio registration, playback, and shared volume handling
- Animation Management System
  - updates generic animation behaviour through `iAnimatable`

The abstract engine is mainly located in:
- `core/src/main/java/com/myGame/engine`

Engine subpackages include:
- `animation/`
- `audio/`
- `collision/`
- `collision/hitboxes/`
- `entity/`
- `input/`
- `lifecycle/`
- `movement/`
- `scene/`


Portion 2: Math Bomber Game
---------------------------
Math Bomber is a game-based learning application designed to support children’s mathematics education. It aims to address the lack of engagement in traditional mathematics learning by turning arithmetic practice into an interactive gameplay experience.

Instead of drilling questions in a repetitive format, the game presents mathematics questions during real-time gameplay. The player must move around the board, avoid threats, and bomb the enemy carrying the correct answer. This combines immediate feedback, time pressure, movement, and decision-making into a single gameplay loop.

The purpose of the game is to increase children’s engagement in mathematics learning by turning arithmetic practice into an interactive and motivating activity. Rather than presenting mathematics as isolated worksheets or drills, the game embeds question solving into movement, collision avoidance, and round-based progression.

The game supports:
- three game modes: Addition, Multiplication, and Mixed
- three difficulty levels: Easy, Medium, and Hard
- multiple input schemes: Arrow Keys, WASD, and Mouse Control
- pause, resume, and end-game menu flows
- game statistics at the end of a run

Difficulty settings vary in map size, target score, number of enemies, enemy speed, question range, and time allowed to answer each question. This allows the same gameplay structure to support both easier entry-level play and more demanding play sessions.

Gameplay flow:
- Start Menu
  - configure difficulty, controls, game mode, and volume
- Main Gameplay
  - answer arithmetic questions by bombing the correct target
- Pause Menu
  - resume or quit while adjusting volume
- Ending Menu
  - shows win/lose result and game statistics

During gameplay, each round presents a set of moving enemies, each labelled with a number. One of these numbers is the correct answer to the current question. The player must navigate the board and use a limited number of bombs to eliminate the correct target. When the correct enemy is hit, the score increases and the next round begins with a new question, new enemies, and refreshed timers.

The game also includes penalties to encourage careful play:
- running out of time decreases the score and resets the round
- getting hit by an explosion decreases score and lives, then resets the round
- colliding with an enemy decreases score and lives, then resets the round
- running out of bombs decreases the score and resets the round

The player wins by reaching the target score for the selected difficulty. The player loses by running out of lives or quitting from the pause menu. In both cases, the ending menu displays the result together with game statistics such as difficulty, total elapsed time, and score achieved.

The game-specific implementation is mainly located in:
- `core/src/main/java/com/myGame/game`

Game subpackages include:
- `animation/`
- `entities/`
- `factory/`
- `factory/base/`
- `factory/concrete/`
- `factory/support/`
- `input/`
- `input/keyboard/`
- `input/mouse/`
- `mathbomber/`
- `mathbomber/board/`
- `mathbomber/configurations/`
- `mathbomber/configurations/enums/`
- `mathbomber/questions/`
- `mathbomber/round/`
- `mathbomber/systems/`
- `scenes/`
- `scenes/gameplay/`
- `scenes/menu/`
- `scenes/renderers/`
- `ui/`

Some notable game features described in the report are:
- support for multiple game modes and difficulty levels
- registry-based generic entity factory for repeated entity spawning
- accessibility adjustment through reduced player hitbox size on easier difficulties

The `game/mathbomber` subpackage contains the main domain logic of the game:
- `board/`
  - tile-grid structure and board-related computations
- `configurations/`
  - difficulty presets and game-level configuration/statistics
- `questions/`
  - arithmetic question generation based on mode and difficulty
- `round/`
  - progression state and rules for the active game session
- `systems/`
  - gameplay systems such as bomb explosions, enemy steering, and tile occupancy rules


Launcher
--------
The desktop launcher is located in:
- `lwjgl3/src/main/java/com/myGame/lwjgl3`

Main launcher files:
- `Lwjgl3Launcher.java`
- `StartupHelper.java`


How To Run The Program
----------------------
Prerequisites
- Java 17 or later
- a terminal opened at the project root:
  - `/Users/duckweed/Documents/School/Y1T2/INF1009/AbstractEngineProject`

Run the desktop game
1. Open a terminal in the project root.
2. Run:

   `./gradlew :lwjgl3:run`

Compile only
- To compile without launching:

  `./gradlew :core:compileJava`

Clean and rebuild
- If packages or folders were renamed and the runtime behaves strangely, run:

  `./gradlew clean :lwjgl3:run`


Basic Usage Guide
-----------------
- Launch the game with the desktop runner.
- In the start menu, choose:
  - a difficulty
  - a control scheme
  - a question mode
  - volume settings
- Click `Start` to begin.
- During gameplay:
  - move using the selected control scheme
  - place bombs to eliminate the enemy with the correct answer
  - avoid enemy collisions and self-damage from explosions
- Use the pause menu to resume or quit.
- View the ending screen for the final result and statistics.
