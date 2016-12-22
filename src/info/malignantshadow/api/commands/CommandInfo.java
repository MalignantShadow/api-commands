package info.malignantshadow.api.commands;

/**
 * Represents information about a command before its arguments are parsed and executed.
 * 
 * <p>
 * This class is immutable (in the sense that it has no 'set' methods) as it is meant to only store information. It can, however, be subclassed.
 * </p>
 * 
 * @author MalignantShadow (Caleb Downs)
 *
 */
public class CommandInfo {
	
	private String _full;
	private Command _cmd;
	private String[] _args;
	
	/**
	 * Construct a new {@link CommandInfo} object.
	 * 
	 * @param full
	 *            The full command prefix (the command without its arguments)
	 * @param command
	 *            The command
	 * @param args
	 *            The arguments to be passed to the command
	 */
	public CommandInfo(String full, Command command, String[] args) {
		_full = full;
		_cmd = command;
		_args = args;
	}
	
	/**
	 * Get the command string the user used.
	 * 
	 * @return The command string
	 */
	public String getFullCommand() {
		return _full;
	}
	
	/**
	 * Get the command that will be executed.
	 * 
	 * @return The command
	 */
	public Command getCommand() {
		return _cmd;
	}
	
	/**
	 * Get the unparsed arguments
	 * 
	 * @return The arguments
	 */
	public String[] getArgs() {
		return _args;
	}
}
