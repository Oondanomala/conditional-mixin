package net.ornithemc.conditionalmixin.testmod;

public enum Test {

	EMPTY_CONDITIONAL,
	FAILED_CONDITIONAL(true),
	MINECRAFT_VERSION,
	MINECRAFT_VERSION_FRIENDLY_STRING,
	ONE_OF_MINECRAFT_VERSIONS,
	GAME_SIDE,
	RUNTIME_ENVIRONMENT,
	MOD_LOADED,
	MOD_ABSENT,
	MOD_VERSION,
	ONE_OF_MOD_VERSIONS,
	MOD_GAME_SIDE,
	MOD_RUNTIME_ENVIRONMENT,
	ONE_OF_MODS,
	ALL_OF_MODS;

	public final boolean defaultSuccess;

	private Test() {
		this(false);
	}

	private Test(boolean defaultSuccess) {
		this.defaultSuccess = defaultSuccess;
	}
}
