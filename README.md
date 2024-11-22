# The One Smeagle - Minecraft Mod
_More immersive alternative for WAILA, fork of The One Probe_

## Introduction to The One Smeagle (TOS)

The One Smeagle is a fork of The One Probe (or TOP in short) that is meant to be a more immersive version of WAILA/HWYLA.

The purpose of this mod is to show on-screen information about the block you are looking at whenever you hold the probe in your hand (or off-hand). The mod itself will show basic information like the name of the block, the mod the block comes from, and also the tool to use for harvesting the block. In addition, this mod will also show the amount of RF energy that is stored in the block (if the block supports RF), and if you are sneaking it will also give a list of all items that are in the block if it is an inventory (like a chest).

This mod is very configurable so you can disable all the above features if they do not fit your playing style or modpack.

This mod also has a flexible API that other mods can use to add more information. The API can be found [here](https://github.com/McJty/TheOneProbe/tree/master/src/main/java/mcjty/theoneprobe/api).

## Changes from The One Probe
* Features from TOP Extras
    * View the Enchantment power of Bookshelf's
    * View Inventory of Minecarts and Mules
    * View the time left until TNT explodes
    * View how much water is in a Cauldron
    * View what disk is playing in a Jukebox
    * View a paintings name
* Fixes from TopFix
   * Show the fluid's local name when playing on a server
   * Fixed chests not displaying the local names of GregTechCE items when playing on a server               
* Updated gradle version from 2.7 to 4.5
* View more information with the Creative Probe
* A greatly improved config screen
  * View all config categories, not just the client ones.
  * Subcategories to help group things
* New theme presets
   * Jade: based on the [mod](https://www.curseforge.com/minecraft/mc-mods/jade) of the same name
   * Crazy: please don't use this, your eyeballs will thank me.
   * Soft Pastels: WIP
   * Ocean Blue: The colors of the ocean will relax you
* New config options:
   * showDebugUUID (Default: False) If you want to see the UUID of an NPC with the Creative Probe
   * probeNoteStackSize (Default: 1) Change the One Probe Read Me stack size.
   * harvestLevels (Default: Stone-Vibranium) Add/remove lang keys for the harvest levels if say you have a block with a > 8  harvest level
   * blockNameMaxWidth (Default: 0.0) The max length to display in the probe, will be truncated. 0.0 for no limit 
   * showProbeConfigGUI (Default: true) If players can open the Config UI with the Probe/Note
   * showProbeNoteGUI (Default: true) If players can open the Note UI with the Note
   * probeNoteBlock (Default: minecraft:log) The block to use as the example in the probe config UI
* Replaced the hardcoded strings with language-translatable versions
   * Updated ru_ru.lang (By: @bigenergy)
   * New en_ud.lang
   * New uk_ua.lang (By: @SKZGx)
   * New zh_tw.lang (By: @HJ-zhtw)
   * New de_de.lang (By: @TheScienceDemon)
   * New zh_cn.lang and zh_tw.lang (Fully translated as of Dev-14)
   * New ja_jp.lang (Fully translated as of Dev-15)
   * New ko_kr.lang (Fully translated as of 1.10)
* Javadoc's for the random methods lying around to make working easier
* Many Progress Bars for slower computers (Almost impossible to see if you have a mid-teir computer)
* API improvements
  * New ProbeBase to simplify probe creation of new probes 
  * New PresetBuilder to simplify making new presets (And for GroovyScript compact!)
* Fixed isProbeInHand() checking for the wrong NBT tag 
* Fixed isProbeHelmet() always checking NBT even though we know that our helmets are probe helmets

Note: Languages other than English may not be fully translated, feel free to submit a PR

***

Mods that have compatibility for TOP will also have built-in compatibility for TOS

## Licence

#### MIT

This mod is licensed under the MIT license. To see the full terms of the license click [here](https://github.com/McJty/TheOneProbe/blob/1.10/LICENCE).

#### Modpack Permission

You're free to use the mod in your modpack.

***

## Credits

**Copyright © 2016 [McJty](https://twitter.com/McJty)**

**Copyright © 2022 [TechLord22](https://github.com/TechLord22)**

**Copyright © 2022 [vfyjxf](https://github.com/vfyjxf)**

**Copyright © 2024 Strubium**
