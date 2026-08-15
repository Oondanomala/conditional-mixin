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

# Using the library

The library provides a `@Conditional` annotation that can be used to mark Mixin classes, fields, and methods conditional. The targeted mixin will only be applied if all the conditions defined in the annotation are met.

## `@Conditional`

`@Conditional` supports the following conditions:

- `minecraftVersion`: defines one or more Minecraft version conditions.
- `gameSide`: defines the required game side (`CLIENT`, `SERVER`, or `ANY`).
- `runtimeEnvironment`: defines the required runtime environment (`DEVELOPMENT`, `PRODUCTION`, or `ANY`).
- `modLoaded`: defines one or more `@Mod` conditions for mods that must be loaded.
- `modAbsent`: defines one or more `@Mod` conditions for mods that must be absent.

The `@Conditional` annotation can be used on classes, fields, and methods in the package(s) defined in your `mixins.json`(s). A class annotated with `@Conditional` will be discarded entirely if the annotation's conditions are not met. Any field or method annotated with `@Conditional` will be removed if the annotation's conditions are not met.

#### `minecraftVersion`

The `minecraftVersion` field accepts one or more `@Version` conditions. All of the specified version conditions must be met.

#### `modLoaded`

The `modLoaded` field accepts one or more `@Mod` conditions for mods that are required to be loaded. All of the specified mod conditions must be met.

#### `modAbsent`

The `modAbsent` field accepts one or more `@Mod` conditions for mods that are required to be absent. All of the specified mod conditions must be met.

## `@Mod`

`@Mod` supports the following conditions:

- `value` (required): defines one or more mod IDs.
- `version`: defines one or more mod version conditions - must be left empty if more than one mod ID is specified.
- `gameSide`: defines the required game side (`CLIENT`, `SERVER`, or `ANY`).
- `runtimeEnvironment`: defines the required runtime environment (`DEVELOPMENT`, `PRODUCTION`, or `ANY`).

The `@Mod` annotation can be used for two conditions:

- `modLoaded`: the defined mod must be loaded.
- `modAbsent`: the defined mod must be absent.

#### `value`

The `value` field accepts one or more mod IDs, as a single `String` value or an array of `String` values repectively. Only one of the defined mod IDs must match a mod that is currently loaded..

### `version`

The `version` field accepts one or more `@Version` conditions. All of the specified version conditions must be met.

#### `gameSide`

The `gameSide` field defines the game side (client or server) on which this mod condition must be met. If the game side does not match, this mod condition is effectively ignored.

#### `runtimeEnvironment`

The `runtimeEnvironment` field defines the runtime environment (development or production) in which this mod condition must be met. If the runtime environment does not match, this mod condition is effectively ignored.

## `@Version`

`@Version` supports the following conditions:

- `value` (required): defines one or more version predicates.
- `gameSide`: defines the required game side (`CLIENT`, `SERVER`, or `ANY`).
- `runtimeEnvironment`: defines the required runtime environment (`DEVELOPMENT`, `PRODUCTION`, or `ANY`).


#### `value`

The `value` field accepts one or more version predicates, as a single `String` value or an array of `String` values . Only one of the defined predicates must match the runtime version for the condition to be met.

Version predicates follow the same format as dependency version predicates in your `mod.json`, meaning the version strings must be Semver 2.0.0 compliant. However, if the `@Version` is used as a `@Conditional` `minecraftVersion`, the use of "friendly" version strings is also allowed.

The following examples are all valid usages of `@Version`:

```java
@Version("1.0.0")
@Version(">1.0.0- <1.1")
@Version({ "1.0.x", ">=1.2" })
@Version({ "<1.0.0-alpha.1", ">1.0.0-rc.2" })
```

And the following are all examples of valid usages of `@Version` in `@Conditional` `minecraftVersion`:

```java
@Version("1.7.2")
@Version(">13w41a <1.7")
@Version({ "1.7.x", ">=1.9" })
@Version({ "<b1.9-pre1", ">1.0.0-rc2" })
@Version({ ">=c0.0.11a-launcher <=inf-20100629", ">=a0.1.0 <=a0.2.8" })
```

#### `gameSide`

The `gameSide` field defines the game side (client or server) on which this version condition must be met. If the game side does not match, this version condition is effectively ignored.

#### `runtimeEnvironment`

The `runtimeEnvironment` field defines the runtime environment (development or production) in which this version condition must be met. If the runtime environment does not match, this version condition is effectively ignored.

## Examples

```java
// Mixin class is only applied in Minecraft 13w16a and above.
@Conditional(minecraftVersion = @Version(">=13w16a"))
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
	// - Mod 'yetanotherexample', with version 1.2.1 or above if on the client, or version 1.2.2 or above if on the server, is loaded.
	// - Mod 'breakingexample', with version below 1.2.0 if in a development environment, or any version otherwise, is not loaded.
	@Conditional(
		minecraftVersion = @Version({
			">1.7.6- <=1.7.10",
			">=14w08a"
		}),
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
					@Version(
						value = ">=1.2.1",
						gameSide = GameSide.CLIENT
					),
					@Version(
						value = ">=1.2.2",
						gameSide = GameSide.SERVER
					)
				}
			)
		},
		modAbsent = {
			@Mod(
				value = "breakingexample",
				version = @Version(
					value = "<1.2.0",
					runtimeEnvironment = RuntimeEnvironment.DEVELOPMENT
				)
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
