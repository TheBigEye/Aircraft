 <!-- Logo -->
<img 
     align="right"
     width="32%"
     src="https://github.com/TheBigEye/Aircraft/assets/63316583/67eda91f-fbec-4595-bd11-ab3090ed6e5f"
     title="Aircraft"
/>

**Aircraft** is a modified version of [**Minicraft Plus**](https://github.com/chrisj42/minicraft-plus-revived), a mod based on the original [**Minicraft**](https://minicraft.fandom.com/wiki/Minicraft) game made by Markus persson "Notch" for the Lodum dare 22 competition. 

The development of the mod is originally based on version 2.0.6 and 2.0.7 of Minicraft Plus, with the aim of adding more content and features to the game and at the same time improving and expanding the entire look and design.

> **Note**: It is currently not recommended to load version after version of old worlds, as the save system is not ready or adapted and a rewrite is planned.

## Main Features
### Graphics 🖌️
+ More detailed and renewed graphics and textures.
+ Added more particles effects.
+ Improved the rendering of various menus and GUI elements.
+ The game window and interface is wider.

### Gameplay 👾
* Added peaceful difficulty.
* Added more durability in tools.
* Added some more diversity of peaceful and hostile mobs:
    * Penguins, Chickens and Goats.
    * Iron golems and Rust Iron Golems.
    * NPCs, like villagers, that you can trade with.
* Added Maps, and more items.
* Added the Keeper, the guardian of the underground.
* Added the Eye queen, the final boss.
* Improved the Air Wizard with three phases.

### Level Generation 🗺️
- Added villages, replacing the wooden ruins in the forests.
- Improved the Heaven generation:
    - Added a Sky dungeon.
    - Added a central island, with trees, grass and rock.
    - Added one more cloud variety, Ferrosite, which temporarily increases player speed.
    - Added Aether-inspired crops and mobs.
* Improved mountains to have two rock layers instead of one.
* More vegetation (Lawn and flowers).
* Added the tundra biome.
* More types of trees were added with their respective types of wood and their variants in doors, walls and floors.
* Better terrain shape generation.

### Technical side 🛠️
* Improved the sound system:
    * Added background music.
    * Some sounds are attenuated depending on the distance.
* Added Texture packs support.
* Added an more detailed Crash screen in information.
* Improved precision and information in the F3 menu (debug screen).

## How to Run? 🚀
- First, make sure you have [**Java**](https://java.com/en/download/) installed on your system.
- Next, download the .jar file of the game from our [**releases page**](https://github.com/TheBigEye/Aircraft/releases).
- **Windows Users:** Simply `double click` the downloaded .jar file to launch the game. Alternatively, you can `right-click` on the file and choose `open with > Java platform SE binary`.
- **Linux Users:** Open your terminal, navigate to the location where the .jar file was downloaded, and enter the following command:
   ```sh
    java -jar Aircraft.jar
   ```
- If you encounter any issues, try running the same command using CMD, PowerShell (Windows), or Terminal (Linux) to resolve any potential Java-related problems

## How to Build or Run from Source? 🛠️
Currently the project is built with Gradle, a tool that makes it easy to build or run the project from source code.

- Download the source code by clicking the green "Code" button and selecting "Download ZIP".
- Extract the contents of the downloaded ZIP folder.
- Open your command prompt and use the `cd` command to navigate to the extracted folder, bringing it up in the command prompt.
- To build the project, enter the following command:
> **Note**: in CMD the `./` is not necessary.
   ```sh
    ./gradlew build
   ```
   Alternatively, to run the project, use:
   ```sh
    ./gradlew run
   ```

- If you built the project, find the generated .jar file in the `build/libs` directory.
- In case you encounter an error related to missing Java, ensure that your JAVA_HOME environment variable is correctly set up, or download the [**JDK**](https://www.oracle.com/java/technologies/downloads/) if you haven't already.

<!-- -------------------------------------------------------------------------- Credits ------------------------------------------------------------------------------>
<!-- Header and footer svgs --- kyechan99/capsule-render -->
<!-- Views counter --- antonkomarev/github-profile-views-counter -->
<!-- ---------------------------------------------------------------------------- END -------------------------------------------------------------------------------->
