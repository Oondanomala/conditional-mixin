package net.ornithemc.conditionalmixin.conditional;

@SuppressWarnings("serial")
public class InvalidConditionException extends RuntimeException {

	public InvalidConditionException(String message) {
		super(message);
	}

	public InvalidConditionException(String message, Throwable cause) {
		super(message, cause);
	}
}
