package net.ornithemc.conditionalmixin.testmod;

import java.util.EnumSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;

public final class TestMod {

	public static final Logger LOGGER = LogManager.getLogger("Conditional Mixin|Test Mod");

	private static final EnumSet<Test> SUCCESSES = EnumSet.noneOf(Test.class);
	private static final EnumSet<Test> FAILURES = EnumSet.noneOf(Test.class);

	public static void success(Test test) {
		if (SUCCESSES.add(test)) {
			LOGGER.info("Conditional Mixin test {} succeeded!", test.name().toLowerCase());
		}
	}

	public static void failure(Test test) {
		if (FAILURES.add(test)) {
			LOGGER.error("Conditional Mixin test {} failed!", test.name().toLowerCase());
		}
	}

	public static void init() {
		// some tests can be partially successful
		SUCCESSES.removeAll(FAILURES);

		EnumSet<Test> failuresToLog = EnumSet.complementOf(SUCCESSES);
		failuresToLog.removeAll(FAILURES);
		failuresToLog.forEach(test -> {
			if (test.defaultSuccess)
				success(test);
			else
				failure(test);
		});

		if (FAILURES.isEmpty()) {
			LOGGER.info("Conditional Mixin tests finished (all {} succeeded)", SUCCESSES.size());
		} else {
			LOGGER.warn("Conditional Mixin tests finished ({} succeeded/{} failed)", SUCCESSES.size(), FAILURES.size());
		}

		if (FAILURES.isEmpty()) {
			Minecraft.getInstance().stop();
		} else {
			throw new RuntimeException("Conditional Mixin tests failed! See the logs for more info.");
		}
	}
}
