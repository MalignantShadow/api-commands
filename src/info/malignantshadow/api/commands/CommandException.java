package info.malignantshadow.api.commands;

/**
 * An {@link Exception} that is thrown when an error in the command system occurs. The definition of "error" is not synonymous to the {@link Error} class.
 * CommandExceptions are only thrown during the creation and addition of commands in a {@link CommandManager}, and are typically synonymous
 * to {@link IllegalArgumentException}, but more tailored to this API.
 * 
 * @author MalignantShadow (Caleb Downs)
 *
 */
public class CommandException extends RuntimeException {
	
	private static final long serialVersionUID = -7102698010018442081L;
	
	/**
	 * @param message
	 *            The message
	 * @see RuntimeException#RuntimeException(String)
	 */
	public CommandException(String message) {
		super(message);
	}
	
}
