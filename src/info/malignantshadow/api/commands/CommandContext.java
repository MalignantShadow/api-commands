package info.malignantshadow.api.commands;

import java.util.Arrays;

import info.malignantshadow.api.util.AttachableData;
import info.malignantshadow.api.util.ListUtil;
import info.malignantshadow.api.util.arguments.ParsedArgument;
import info.malignantshadow.api.util.arguments.ParsedArguments;

/**
 * Holds command invocation information, such as what command was run and who/what sent it.
 * 
 * @author MalignantShadow (Caleb Downs)
 *
 */
public class CommandContext extends AttachableData {
	
	private String _prefix;
	private CommandSender _sender;
	private Command _cmd;
	private ParsedArguments _parsedArgs;
	
	/**
	 * Construct a new command context with the given sender and arguments
	 * 
	 * @param prefix
	 *            The command (excluding the arguments) that was sent
	 * @param sender
	 *            Who sent the command
	 * @param cmd
	 *            The command that was sent
	 * @param parsedArgs
	 *            The parsed arguments
	 */
	public CommandContext(String prefix, CommandSender sender, Command cmd, ParsedArguments parsedArgs) {
		super();
		_prefix = prefix;
		_sender = sender;
		_cmd = cmd;
		_parsedArgs = parsedArgs;
	}
	
	/**
	 * Get the command that was sent (excluding the argument names and input)
	 * 
	 * @return The command
	 */
	public String getCommandPrefix() {
		return _prefix;
	}
	
	/**
	 * Get the argument inputs joined by spaces.
	 * 
	 * @return THe joined string.
	 */
	public String getInputJoined() {
		return getInputJoined(" ");
	}
	
	/**
	 * Get the argument inputs joined.
	 * 
	 * @param delimeter
	 *            The delimiter
	 * @return The joined string.
	 */
	public String getInputJoined(String delimiter) {
		return ListUtil.join(Arrays.asList(_parsedArgs.getInput()));
	}
	
	/**
	 * Get the full command that was sent, including the argument inputs and extra arguments.
	 * 
	 * @return The joined string.
	 */
	public String getFullCommandString() {
		return _prefix + " " + getInputJoined() + " " + getExtraJoined();
	}
	
	/**
	 * Get the sender of the command
	 * 
	 * @return The command's sender
	 */
	public CommandSender getSender() {
		return _sender;
	}
	
	/**
	 * Get the command that was sent.
	 * 
	 * @return The command
	 */
	public Command getCommand() {
		return _cmd;
	}
	
	/**
	 * Get the parsed arguments.
	 * 
	 * @return The parsed arguments.
	 */
	public ParsedArguments getParsedArgs() {
		return _parsedArgs;
	}
	
	/**
	 * Get the extra arguments (input that was not mapped to a value).
	 * 
	 * @return The extra arguments.
	 */
	public String[] getExtra() {
		return _parsedArgs.getExtra();
	}
	
	/**
	 * Convenience method for <code>getSender().print(message)</code>
	 * 
	 * @param message
	 *            The message
	 * @see CommandSender#print(String)
	 */
	public void print(String message) {
		if (_sender != null)
			_sender.print(message);
	}
	
	/**
	 * Convenience method for <code>getSender().print(format, args)</code>
	 * 
	 * @param format
	 *            The format of the message
	 * @param args
	 *            The arguments of the message
	 * @see CommandSender#print(String, Object...)
	 */
	public void print(String format, Object... args) {
		print(String.format(format, args));
	}
	
	/**
	 * Convenience method for <code>getSender().print(o)</code>
	 * 
	 * @param o
	 *            The object
	 * @see CommandSender#print(Object)
	 */
	public void print(Object o) {
		if (_sender != null)
			_sender.print(o);
	}
	
	/**
	 * Convenience method for <code>getSender().printErr(format, args)</code>
	 * 
	 * @param format
	 *            The format of the message
	 * @param args
	 *            The arguments of the message
	 */
	public void printErr(String format, Object... args) {
		printErr(String.format(format, args));
	}
	
	/**
	 * Convenience method for <code>getSender().printErr(message)</code>
	 * 
	 * @param message
	 *            The message
	 */
	public void printErr(String message) {
		if (_sender != null)
			_sender.printErr(message);
	}
	
	/**
	 * Convenience method for <code>getSender().printErr(o)</code>
	 * 
	 * @param o
	 *            The object
	 * @see CommandSender#printErr(Object)
	 */
	public void printErr(Object o) {
		if (_sender != null)
			_sender.printErr(o);
	}
	
	/**
	 * Get the value of an argument.
	 * 
	 * @param name
	 *            The name of the argument.
	 * @return The value (may be null)
	 */
	public Object get(String name) {
		return _parsedArgs.get(name);
	}
	
	/**
	 * Get the {@link ParsedArgument} representing the argument with the given name.
	 * 
	 * @param name
	 *            The name of the argument.
	 * @return The {@link ParsedArgument} object
	 */
	public ParsedArgument getParsedArg(String name) {
		return _parsedArgs.getArg(name);
	}
	
	/**
	 * Get the input for the argument with the given name.
	 * 
	 * @param name
	 *            The name of the argument.
	 * @return The input.
	 */
	public String getInputFor(String name) {
		ParsedArgument arg = getParsedArg(name);
		if (arg == null)
			return null;
		
		return arg.getInput();
	}
	
	/**
	 * Was input specified for the argument with the given name?
	 * 
	 * @param name
	 *            The name of the argument
	 * @return {@code true} if and only if the argument exists and the input is not {@code null}.
	 */
	public boolean hasInputFor(String name) {
		return getInputFor(name) != null;
	}
	
	/**
	 * Get the extra arguments joined with a space.
	 * 
	 * @return The joined extra arguments
	 */
	public String getExtraJoined() {
		return getExtraJoined(" ");
	}
	
	/**
	 * Get the extra arguments joined with the given delimiter.
	 * 
	 * @param delimiter
	 *            The delimiter.
	 * @return The joined extra arguments
	 */
	public String getExtraJoined(String delimiter) {
		return ListUtil.join(Arrays.asList(getExtra()), delimiter);
	}
	
	/**
	 * Dispatch the command with this context. This should never be called within the handler code itself, as a {@link StackOverflowError} is likely to occur.
	 * 
	 * @return {@code true} if this context was sent to the handler, or {@code false} if the command has no handler
	 */
	public boolean dispatchSelf() {
		Command.Handler handler = _cmd.getHandler();
		if (handler == null)
			return false;
		
		handler.handleCommand(this);
		return true;
	}
	
	@Override
	public String toString() {
		String format = "CommandContext{name=%s, %s}";
		String args = ListUtil.join(getParsedArgs().getArgs(), (arg) -> arg.getArgument().getName() + "=\"" + arg.getInput() + "\"");
		return String.format(format, _cmd.getName(), args);
	}
	
}
