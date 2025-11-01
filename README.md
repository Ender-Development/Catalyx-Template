# Catalyx-Template

Template repo for creating a mod based on [Catalyx](https://github.com/Ender-Development/Catalyx/).

<a href="https://www.akliz.net/enderman"><img src="https://raw.githubusercontent.com/Ender-Development/Catalyx-Template/refs/heads/master/assets/ender_development/banner.png" align="center"/></a>

## References

This template uses:
- [RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle)

This template is loosely based on:
- [CleanroomMC - ForgeDevEnv](https://github.com/CleanroomMC/ForgeDevEnv)
- [CleanroomMC - TemplateDevEnvKt](https://github.com/CleanroomMC/TemplateDevEnvKt)
- [GregTechCEu - Buildscripts](https://github.com/GregTechCEu/Buildscripts)
- [GTNewHorizons - ExampleMod1.7.10](https://github.com/GTNewHorizons/ExampleMod1.7.10)

## Dev environment

- default maven repositories
- default mods for assisting with development
- everything written in Kotlin
- easy to configure / update
- gradle options for version management, GroovyScript options, creating a Reference/Tags class (with stuff like MOD_ID/similar)
- built-in mixin, coremod and access transformer support
- credentials are managed locally instead of using environment variables
- comes with a few handy set-up scripts

## Spotless

This template uses [Spotless](https://github.com/diffplug/spotless/tree/main/plugin-gradle#readme) to format code.
To auto-format code, run the `Apply Spotless` gradle task or execute the `spotlessInstallGitPrePushHook` task to install a git pre-push hook that will format code before each push.
The formatting rules aren't finalized yet as I still need to talk to roz on what the best rules are for our projects. We also recommend using [IntelliJ IDEA](https://www.jetbrains.com/idea/) as IDE as it has the best Kotlin support,
along with the [Ktlint](https://plugins.jetbrains.com/plugin/15057-ktlint) plugin to highlight formatting issues in the IDE.

## Getting Started

To get started with this template, follow these steps:
- Click on the "Use this template" button above to create a new repository based on this template.
- Clone the newly created repository to your local machine, either by using the command line or the integrated Git support in IntelliJ IDEA.
- Open the project in IntelliJ IDEA.
- Run the `setupDevEnvironment` gradle task to set up the development environment.
  - **Note:** You might need to refresh the gradle project in IntelliJ IDEA if the initial task fails for whatever reason.
  - *Optional:* Run the `spotlessApplyGitPrePushHook` gradle task to install a git pre-push hook that will format code before each push.
- Tweak the `gradle.properties` file to your liking (e.g., set the mod name, mod id, version, etc.).
  - **Note:** Don't forget to update the gradle project after changing the `gradle.properties` file.
- Start coding your mod!
  - **Note:** If you want to take full advantage of Catalyx, make sure to implement the `ICatalyxMod` interface in your main mod class.

## Contributing

Please make sure to read our [contributing guidelines](.github/CONTRIBUTING.md) first.
Furthermore, you have to agree to our [code of conduct](.github/CODE_OF_CONDUCT.md) if you want to contribute.

## Partnership with Akliz

> It's a pleasure to be partnered with Akliz. Besides being a fantastic server provider, which makes it incredibly easy
> to set up a server of your choice, they help me to push myself and the quality of my projects to the next level.
> Furthermore, you can click on the banner below to get a discount. :')

<a href="https://www.akliz.net/enderman"><img src="https://raw.githubusercontent.com/Ender-Development/Catalyx-Template/refs/heads/master/assets/ender_development/partnership.png" align="center"/></a>

If you aren't located in the [US](https://www.akliz.net/enderman), Akliz now offers servers in:

- [Europe](https://www.akliz.net/enderman-eu)
- [Oceania](https://www.akliz.net/enderman-oce)

