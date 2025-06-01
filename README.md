
<p align="center">
    <img width="504" height="82" alt="Animated image of the win streak panel, showing numbers increasing." src="https://github.com/billyfletcher5000/streak-the-spire/blob/main/readme-images/score-increase.gif" />
</p>

# Streak The Spire

<img align="right" width="94" height="280" alt="Animated image of a series of Slay the Spire character heads next to a score representing their current win streak" src="https://github.com/billyfletcher5000/streak-the-spire/blob/main/readme-images/side-bar-idle.gif" />
<p>
Streak The Spire is a mod for the popular deckbuilding roguelike, Slay The Spire. It adds automatic (and accurate!) streak calculation based upon your run history, with effects when you increase or lose your streaks, on a per character and rotating basis. It even has animated character icons using the existing animations from the game!
</p>
<p>
It is highly configurable, allowing you to drag and resize the display and put it anywhere you want, with a number of different background options (including no background at all), as well as customise the criteria used to determine if a given run counts to your streaks.
</p>
<p>
It also provides helpful tooltips with more information on your historical play, including your best win streak of all time, the dates your streaks occurred and your win rate, all calculated depending on the criteria you select. It even integrates with Slay The Relics (+ Reborn)!
</p>

## Installation

Installing can be down in one of two ways:

### Steam Workshop
1. Download from the Steam Workshop (Link here when uploaded)
2. Run the game with mods enabled
3. Select "Streak the Spire" from the list in Mod Loader
4. Hit Play!

### Manual Install
1. Download the latest StreakTheSpire.JAR from the Releases page
2. Copy StreakTheSpire.JAR to your Slay the Spire install directory's "mods" folder
3. Run the game with mods enabled
4. Select "BaseMod" from the list in Mod Loader if it isn't already selected
5. Select "Streak the Spire" from the list in Mod Loader
6. Hit Play!

## Configuring Streak the Spire

Configuration of the mod happens in one of two ways:

### Panel Configuration

To move, resize or change the background of the panel simply hold down `ALT`
on your keyboard and your mouse will be able to resize and move the panel.

You will also see a small button next to the panel, which allows you to cycle
though all the available border styles.

<p align="center">
    <img width="480" height="391" alt="Animated image of the win streak panel moving, resizing and changing its border style" src="https://github.com/billyfletcher5000/streak-the-spire/blob/main/readme-images/resize-border-change.gif" />
</p>

### Mods Menu Configuration

To change your streak criteria, as well as what 'layer' the panel sits on:

1. Go to the Main Menu of Slay the Spire
2. Select "Mods"
3. Select "Streak the Spire" from the list on the left
4. Click the "Config" button in the bottom left area of the screen

Each option should have a tooltip that explains what it does in detail, simply hover over an option for more information.

<p align="center">
    <a href="https://github.com/billyfletcher5000/streak-the-spire/blob/main/readme-images/mod-menu-config-button.png">
    <img width="250" height="215" alt="Static image of the general Mods page. The Streak The Spire entry and Config button are marked." src="https://github.com/billyfletcher5000/streak-the-spire/blob/main/readme-images/mod-menu-config-button-thumbnail.png" hspace=10 />
    </a>
    <a href="https://github.com/billyfletcher5000/streak-the-spire/blob/main/readme-images/config-menu.png">
    <img width="415" height="215" alt="Static image of the mod's configuration panel, showing many options." src="https://github.com/billyfletcher5000/streak-the-spire/blob/main/readme-images/config-menu-thumbnail.png" hspace=10 />
    </a>
</p>

## Known Issues

- Panel resize/move handles are slightly misaligned and it's surprisingly frustrating
- Localization only supports english and may have issues with other languages

## Planned Updates

- Full localization support
- Additional, configurable effects for streak increase/resets
- Specific number and first time ever reached effects
- Ability to add in historical, untracked streaks, in case you don't have your full run history
- Static image and text displays for characters
- Custom character support

## Development Instructions

###  Setup
This project was built with a somewhat outdated base (ojb's Map Marks mod), so does not have any of the cool pom.xml related automagical stuff. If anyone wants to update the project structure appropriately I would be very thankful!

Therefore to setup the project properly it requires the following to exist:

- An `STS_INSTALL` environment variable, pointing to your Slay The Spire install directory
- An `STS_MODDING_LIB` environment variable, pointing to a folder containing copies of `BaseMod.jar`, `ModTheSpire.jar` and the game's `desktop-1.0.jar`.

After this it should just be a case of opening the project in your IDE and building either through the supplied IntelliJ IDEA Run Configurations, or using gradle with the `buildJAR` or `buildAndCopyJAR` tasks.

### Submitting Changes

I will happily entertain any changes through GitHub's Pull Request system and if I ever have to abandon the project will happily hand it over to someone willing to maintain it.

### Potential Useful Stuff For Other Mods

This project contains a few systems and tools that may be of use to other people, feel free to steal whatever you like:

- SkeletonModifier - A mini tool for filtering StS' Spine animations to remove elements based on a system of choosing bones to remove and bones to keep. See StreakTheSpire.initialiseCharacterDisplayModels() for examples.
- CursorOverride - A little system for overriding the cursor image depending on context, see PlayerStreakStoreView and UIResizablePanel for an example of use.
- UIElement framework - A little framework for drawing UI with a scene graph approach so elements are positioned/scaled/rotated relative to their parent. Has some limited layouting options that I hope to refine to a more fully featured WPF style two pass approach in the future. There are better options for Java but not that easily integrate into the StS modding flow/LibGDX.
- Utils.Properties - A basic implementation of variables that automatically send changed events (known as the Observer pattern) when you change their values. Seems overkill at first but is incredibly useful for making reactive UI when used in conjunction with a Model View approach. Almost definitely reinventing the wheel but didn't want to have dependencies.
- SDF Fonts w/ shaders - See UISDFTextElement for details, allows text that can scale to any size and still look good.
- Masking shader - Allows you to add a colour on top of an image, masked to the image's alpha appropriately, used for doing things like 'fading to white'.
