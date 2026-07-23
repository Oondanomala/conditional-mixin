This project uses code from Moulberry's MixinConstraints, as of 23 July 2026, v1.1.0. The source code for Moulberry's MixinConstraints can be found at [https://github.com/Moulberry/MixinConstraints](https://github.com/Moulberry/MixinConstraints). It is licensed under MIT, and a copy of that license can be found in the `Moulberry-MixinConstraints-LICENSE` file.

# Conditional Mixin

An extension to Mixin that allows disabling Mixin classes, fields, and methods.

# Installing

Conditional Mixin is available through Ornithe's Maven.

__Gradle__

```groovy
repositories {
	maven { url = "https://maven.ornithemc.net" }
}

dependencies {
	include(implementation("net.ornithemc:conditional-mixin:1.0.0"))
}
```

To bootstrap Conditional Mixin, register the provided Mixin config plugin in your `mixins.json`:

```json
{
	"plugin": "net.ornithemc.conditionalmixin.mixin.ConditionalMixinPlugin",
}
```

If your project requires its own Mixin config plugin, you can extend the `net.ornithemc.conditionalmixin.mixin.ConditionalMixinPlugin` class to bootstrap Conditional Mixin.

Do note that only one instance of the plugin needs to be registered to take care of bootstrapping. If one of your project's dependencies already registers the plugin, your project does not need to do so.
# Using the library

The library provides a `@Conditional` annotation that can be used to mark Mixin classes, fields, and methods conditional. The targeted mixin will only be applied if all the conditions defined in the annotation are met.

## `@Conditional`

`@Conditional` supports the following conditions:

- `minecraftVersion`: defines one or more Minecraft version predicates.
- `gameSide`: defines the required game side (`CLIENT`, `SERVER`, or `ANY`).
- `runtimeEnvironment`: defines the required runtime environment (`DEVELOPMENT`, `PRODUCTION`, or `ANY`).
- `modLoaded`: defines one or more `@Mod` conditions for mods that must be loaded.
- `modAbsent`: defines one or more `@Mod` conditions for mods that must be absent.

The `@Conditional` annotation can be used on classes, fields, and methods in the package(s) defined in your `mixins.json`(s). A class annotated with `@Conditional` will be discarded entirely if the annotation's conditions are not met. Any field or method annotated with `@Conditional` will be removed if the annotation's conditions are not met.

#### `minecraftVersion`

The `minecraftVersion` field accepts one or more Minecraft version predicates, as a single `String` value or an array of `String` values respectively. Only one of the defined predicates must match the runtime Minecraft version for the condition to be met.

Minecraft version predicates follow the same format as dependency version predicates in your `mod.json`, but also allow for the use of "friendly" version strings as opposed to semantic version strings. The following examples are all valid usages of `minecraftVersion` in `@Conditional`:

```java
@Conditional(minecraftVersion = "1.7.2")
@Conditional(minecraftVersion = ">13w41a <1.7")
@Conditional(minecraftVersion = { "1.7.x", ">=1.9" })
@Conditional(minecraftVersion = { "<b1.9-pre1", ">1.0.0-rc2" })
@Conditional(minecraftVersion = { ">=c0.0.11a-launcher <=inf-20100629", ">=a0.1.0 <=a0.2.8" })
```

#### `modLoaded`

The `modLoaded` field accepts one or more `@Mod` conditions for mods that are required to be loaded. All of the specified mod conditions must be met.

#### `modAbsent`

The `modAbsent` field accepts one or more `@Mod` conditions for mods that are required to be absent. All of the specified mod conditions must be met.

## `@Mod`

`@Mod` supports the following conditions:

- `value` (required): defines one or more mod IDs.
- `version`: defines one or more mod version predicates - must be left empty if more than one mod ID is specified.
- `gameSide`: defines the required game side (`CLIENT`, `SERVER`, or `ANY`).
- `runtimeEnvironment`: defines the required runtime environment (`DEVELOPMENT`, `PRODUCTION`, or `ANY`).

The `@Mod` annotation can be used for two conditions:

- `modLoaded`: the defined mod must be loaded.
- `modAbsent`: the defined mod must be absent.

#### `value`

The `value` field accepts one or more mod IDs, as a single `String` value or an array of `String` values repectively.

#### `gameSide`

The `gameSide` field defines the game side (client or server) on which this mod condition must be met. If the game side does not match, this mod condition is effectively ignored.

#### `runtimeEnvironment`

The `runtimeEnvironment` field defines the runtime environment (development or production) in which this mod condition must be met. If the runtime environment does not match, this mod condition is effectively ignored.

## Examples

```java
// Mixin class is only applied in Minecraft 13w16a and above.
@Conditional(minecraftVersion = ">=13w16a")
@Mixin(Minecraft.class)
public class MinecraftMixin {

	// Injector is only applied on the client.
	@Conditional(gameSide = GameSide.CLIENT)
	@Inject(method = "<clinit>", at = @At("HEAD")
	private static void example1(CallbackInfo ci) {
	}

	// Injector is only applied in development environments.
	@Conditional(runtimeEnvironment = RuntimeEnvironment.DEVELOPMENT)
	@Inject(method = "<clinit>", at = @At("HEAD")
	private static void example2(CallbackInfo ci) {
	}

	// Injector is only applied if mod 'example' is loaded.
	@Conditional(modLoaded = @Mod("example"))
	@Inject(method = "<clinit>", at = @At("HEAD")
	private static void example3(CallbackInfo ci) {
	}

	// Injector is only applied if mod 'example' OR mod 'anotherexample' is loaded.
	@Conditional(modLoaded = @Mod({ "example", "anotherexample" }))
	@Inject(method = "<clinit>", at = @At("HEAD")
	private static void example4(CallbackInfo ci) {
	}

	// Injector is only applied if mod 'example' AND mod 'anotherexample' is loaded.
	@Conditional(modLoaded = { @Mod("example"), @Mod("anotherexample") })
	@Inject(method = "<clinit>", at = @At("HEAD")
	private static void example5(CallbackInfo ci) {
	}

	// Injector is only applied if all the following conditions are met:
	// - The Minecraft version is between 1.7.6 (pre) and 1.7.10 OR at least 14w08a.
	// - The game side that is running is the client.
	// - The current runtime environment is a development environment.
	// - Either the runtime environment is not a development environment, or mod 'example' or mod 'anotherexample' is loaded.
	// - Mod 'yetanotherexample', with a version below 1.2.1 or above 1.2.2, is loaded.
	// - Mod 'breakingexample', with a version beginning with 1.7 or version 2.0 or above, is not loaded.
	@Conditional(
		minecraftVersion = {
			">1.7.6- <=1.7.10",
			">=14w08a"
		},
		gameSide = GameSide.CLIENT,
		modLoaded = {
			@Mod(
				value = {
					"example",
					"anotherexample"
				},
				runtimeEnvironment = RuntimeEnvironment.DEVELOPMENT
			),
			@Mod(
				value = "yetanotherexample",
				version = {
					"<1.2.1",
					">1.2.2"
				}
			)
		},
		modAbsent = {
			@Mod(
				value = "breakingexample",
				version = {
					"1.7.x",
					">=2.0"
				}
			)
		}
	)
	@Inject(method = "<clinit>", at = @At("HEAD")
	private static void example6(CallbackInfo ci) {
	}
}
```

# License

The library is available under the Apache 2.0 license.
