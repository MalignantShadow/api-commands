package info.malignantshadow.api.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import info.malignantshadow.api.util.ListUtil;
import info.malignantshadow.api.util.arguments.Argument;
import info.malignantshadow.api.util.arguments.ArgumentTypes;
import info.malignantshadow.api.util.arguments.ParsedArgument;

/**
 * Represents a command manager. CommandManagers are in charge of storing, organizing, and dispatching commands.
 * 
 * @author MalignantShadow (Caleb Downs)
 * @see #push(CommandBuilder)
 * @see #dispatch(CommandSender, String, String[])
 *
 */
public class CommandManager {
	
	private List<Command> _commands;
	
	/**
	 * Create a new {@link CommandManager}
	 */
	public CommandManager() {
		_commands = new ArrayList<Command>();
	}
	
	/**
	 * Get all commands that have been added so far.
	 * 
	 * @return The commands
	 */
	public List<Command> getCommands() {
		return _commands;
	}
	
	/**
	 * Get all visible commands for help listings. If a {@link Command} returns <code>true</code> when {@link Command#shouldBeHidden() shouldBeHidden()} is called on it,
	 * then it will be excluded from the returned list.
	 * 
	 * @param sender
	 *            Who is viewing the help listing
	 * @return The commands that <code>sender</code> can see.
	 */
	public List<Command> getVisibleCommands(CommandSender sender) {
		List<Command> visible = new ArrayList<Command>();
		for (Command c : _commands)
			if (!c.shouldBeHidden())
				visible.add(c);
		return visible;
	}
	
	/**
	 * Does this command manager have a command with the given name/alias?
	 * 
	 * @param alias
	 *            The name or alias to search for (case-sensitive)
	 * @return <code>true</code> if a command has specified alias.
	 */
	public boolean hasCommandWithAlias(String alias) {
		for (Command c : _commands)
			if (c.hasAlias(alias))
				return true;
		return false;
	}
	
	/**
	 * Add a command to this manager.
	 * 
	 * @param command
	 *            The command to add
	 * @return this
	 * @throws CommandException
	 *             If this manager already has a command with the given command's name or any of its aliases
	 */
	public CommandManager push(Command command) {
		for (String s : command.getAliases(true))
			if (hasCommandWithAlias(s))
				throw new CommandException(String.format("A command with the alias '%s' already exists", s));
			
		_commands.add(command);
		return this;
	}
	
	private static String[] removeFirst(String[] args) {
		if (args == null || args.length <= 1)
			return new String[0];
		String[] newArgs = new String[args.length - 1];
		System.arraycopy(args, 1, newArgs, 0, newArgs.length);
		return newArgs;
	}
	
	private static String[] removeLast(String[] args) {
		if (args == null || args.length <= 1)
			return new String[0];
		String[] newArgs = new String[args.length - 1];
		System.arraycopy(args, 0, newArgs, 0, newArgs.length);
		return newArgs;
	}
	
	/**
	 * Get the command with given name/alias
	 * 
	 * @param name
	 *            The name/alias of the command
	 * @return The command, or null if none was found.
	 */
	public Command getCommand(String name) {
		return ListUtil.find(_commands, (command) -> command.hasAlias(name));
	}
	
	/**
	 * Called when a command is about to be run. If this method returns false, the command will not run. (By default, it simply returns <code>true</code>)
	 * 
	 * @param cmd
	 *            The command being run
	 * @param context
	 *            The context of the command
	 * @return <code>true</code> if the command should run, <code>false</code> to prevent it from doing so.
	 */
	protected boolean commandWillDispatch(Command cmd, CommandContext context) {
		return true;
	}
	
	/**
	 * Called after a command was run. (By default, this method does nothing)
	 * 
	 * @param cmd
	 *            The command that was run
	 * @param context
	 *            The context of the command
	 */
	protected void commandDidDispatch(Command cmd, CommandContext context) {
		
	}
	
	/**
	 * Called when a command context object was created by the {@link #dispatch(CommandSender, Command, String, String[])} method.
	 * (By default, this method does nothing)
	 * 
	 * @param context
	 *            The context created.
	 */
	protected void contextWasCreated(CommandContext context) {
		
	}
	
	/**
	 * Get information representing a command the user is trying to run. <code>fullCommand</code> will be split by any whitespace,
	 * and passed to {@link #dispatch(CommandSender, String[])}, where the first item in the array is the name of the command to run.
	 * 
	 * @param fullCommand
	 *            The full command, including arguments
	 * @return A {@link CommandInfo} object representing a command the user is trying to run.
	 */
	public CommandInfo getCommandInfo(String fullCommand) {
		return getCommandInfo(fullCommand.split("\\s+"));
	}
	
	/**
	 * Get information representing a command the user is trying to run.
	 * 
	 * @param args
	 *            The command name and arguments
	 * @return A {@link CommandInfo} object representing a command the user is trying to run.
	 */
	public CommandInfo getCommandInfo(String[] args) {
		if (args == null || args.length == 0)
			return null;
		
		String command = args[0];
		args = removeFirst(args);
		return getCommandInfo(command, args);
	}
	
	/**
	 * Get information representing a command the user is trying to run.
	 * 
	 * @param command
	 *            The root command
	 * @param args
	 *            The arguments to the command
	 * @return A {@link CommandInfo} object representing a command the user is trying to run.
	 */
	public CommandInfo getCommandInfo(String command, String[] args) {
		Command cmd = getCommand(command);
		if (cmd == null)
			return null;
		
		CommandManager nested = cmd.getNestedManager();
		String fullCmdPath = command;
		String[] contextArgs = args;
		
		for (int i = 0; i < args.length; i++) {
			if (nested != null) {
				String label = contextArgs[0];
				Command tmpCmd = nested.getCommand(label);
				if (tmpCmd == null) // if a sub-command isn't found, break from the loop and use current arguments for the handler
					break;
				cmd = tmpCmd;
				fullCmdPath += " " + label;
				contextArgs = removeFirst(contextArgs);
				nested = cmd.getNestedManager();
			} else // if there are no sub-commands, break and use current arguments for the handler
				break;
		}
		
		return new CommandInfo(fullCmdPath, cmd, contextArgs);
	}
	
	/**
	 * Dispatch a command. <code>fullCommand</code> will be split by any whitespace, and passed to {@link #dispatch(CommandSender, String[])}, where the first item in the array is the
	 * name of the command to run.
	 * 
	 * @param sender
	 *            Who is sending the command
	 * @param fullCommand
	 *            The command
	 * @return <code>true</code> if the command was dispatched successfully, <code>false</code> otherwise.
	 */
	public boolean dispatch(CommandSender sender, String fullCommand) {
		return dispatch(sender, fullCommand.split("\\s+"));
	}
	
	/**
	 * Dispatch a command. The first item in the specified array is the name of the command to run.
	 * 
	 * @param sender
	 *            Who is sending the command
	 * @param args
	 *            The command name and arguments
	 * @return <code>true</code> if the command was dispatched successfully, <code>false</code> otherwise.
	 */
	public boolean dispatch(CommandSender sender, String[] args) {
		if (args == null || args.length == 0)
			return false;
		
		String command = args[0];
		args = removeFirst(args);
		return dispatch(sender, command, args);
	}
	
	/**
	 * Dispatch a command.
	 * 
	 * @param sender
	 *            Who is sending the command
	 * @param command
	 *            The name/alias of the command to run
	 * @param args
	 *            The unparsed arguments to the command
	 * @return <code>true</code> if the command was dispatched successfully, <code>false</code> otherwise.
	 */
	public boolean dispatch(CommandSender sender, String command, String[] args) {
		CommandInfo info = getCommandInfo(command, args);
		if (info == null) {
			sender.printErr("[CommandErr] <%s> - Not found", command);
			return false;
		}
		
		return dispatch(sender, info.getCommand(), info.getFullCommand(), info.getArgs());
	}
	
	/**
	 * Dispatch a command.
	 * 
	 * @param sender
	 *            Who is sending the command
	 * @param cmd
	 *            The command to dispatch
	 * @param cmdPrefix
	 *            The prefix to the command. This is the full command string up to and including the command's name.
	 * @param args
	 *            The arguments to give the command
	 * @return {@code true} if the command was dispatched successfully, {@code false} otherwise.
	 */
	public boolean dispatch(CommandSender sender, Command cmd, String cmdPrefix, String[] args) {
		CommandContext context = createContext(sender, cmd, cmdPrefix, args);
		contextWasCreated(context);
		if (context == null) {
			sender.printErr("[CommandErr] '%s' - Expected at least %d argument(s), but got %d", cmdPrefix, cmd.getArguments().getMinimum(), args.length);
			return false;
		}
		
		for (ParsedArgument a : context.getParsedArgs()) {
			Argument arg = a.getArgument();
			if (arg.isRequired() && !arg.canBeNull() && a.getValue() == null) {
				sender.printErr("[CommandErr] '%s' - Invalid input for argument '%s': \"%s\"", cmdPrefix, arg.getDisplay(), a.getInput());
				return false;
			}
		}
		
		if (commandWillDispatch(cmd, context)) {
			try {
				if (!context.dispatchSelf())
					return false;
			} catch (Exception e) {
				e.printStackTrace();
				
				//to the programmer, error != exception, however most users do not know that, and may not even know what the word "exception" means in this context
				//therefore, the more user-friendly word error is used.
				context.printErr("An error occurred while running this command");
				return false;
			}
			// moved out of try/catch to prevent confusion of the above error, i.e. if an exception occurred
			// during commandDidDispatch() and not handler.handleCommand()
			commandDidDispatch(cmd, context);
			return true;
		}
		return false;
	}
	
	/**
	 * Create a command context to be given to a {@link CommandHandler}.
	 * 
	 * @param sender
	 *            Who is sending the command
	 * @param command
	 *            The name of the command being dispatched
	 * @param args
	 *            The arguments to the command
	 * @return A new command context, or {@code null} if a command with the given name cannot be found.
	 */
	public CommandContext createContext(CommandSender sender, String command, String[] args) {
		Command cmd = getCommand(command);
		if (cmd == null)
			return null;
		
		return createContext(sender, cmd, command, args);
	}
	
	/**
	 * Create a command context to be given to a {@link CommandHandler}.
	 *
	 * @param sender
	 *            Who is sending the command
	 * @param cmd
	 *            The command to dispatch
	 * @param cmdPrefix
	 *            The prefix to the command. This is the full command string up to and including the command's name.
	 * @param args
	 *            The arguments to give the command
	 * @return A new command context, or {@code null} if not enough arguments were given.
	 */
	public CommandContext createContext(CommandSender sender, Command cmd, String cmdPrefix, String[] args) {
		return cmd.createContext(sender, cmdPrefix, args);
	}
	
	/**
	 * Add a help command to this command manager. The command will have the name "help" and an alias of "?".
	 * 
	 * @return this
	 * @see #withHelpCommand(String, String...)
	 */
	public CommandManager withHelpCommand() {
		return withHelpCommand("help", "?");
	}
	
	/**
	 * Add a help command to this command manager.
	 * 
	 * @param name
	 *            The name of the help command
	 * @param aliases
	 *            The aliases for the help command
	 * @return this
	 */
	public CommandManager withHelpCommand(String name, String... aliases) {
		return push(new Command(name, "View help", aliases)
			.withArg(new Argument("arg", "page | command", "The page to view or a command to get help for", false)
				.withAcceptedTypes(ArgumentTypes.NUMBER, ArgumentTypes.STRING))
			.withHandler((context) -> {
				String fullCmdPath = context.getCommandPrefix();
				String[] split = fullCmdPath.split("\\s+");
				fullCmdPath = split.length == 0 ? "" : ListUtil.join(Arrays.asList(removeLast(split)));
				int page = 1;
				HelpListing helpList = getHelpListing(fullCmdPath, context.getSender());
				Object argParsed = context.get("arg");
				if (argParsed != null) {
					if (argParsed instanceof Number)
						page = ((Number) argParsed).intValue();
					else {
						String command = (String) argParsed;
						Command helpCommand = getCommand(command);
						
						if (helpCommand == null) {
							context.printErr("Sub-command with name/alias '%s' does not exist", command);
							return;
						}
						
						context.print((helpList.formatFullCommand(fullCmdPath) + " ").trim() + helpList.formatSimpleCommand(helpCommand));
						
						for (Argument c : helpCommand.getArguments()) {
							String argString = helpList.formatArg(c.getDisplay(), c.isRequired());
							argString += " " + helpList.formatDescription(c.getDescription());
							context.print("  " + argString.trim());
						}
					}
					return;
				}
				
				List<String> shownHelp = helpList.getHelp(page);
				if (shownHelp == null) {
					context.printErr("Page " + page + " does not exist");
					return;
				}
				
				for (String s : shownHelp)
					context.print(s);
				
			}));
	}
	
	/**
	 * Get a help listing object with all visible commands from this manager.
	 * 
	 * @param fullCommand
	 *            The full command (minus the sub-command and arguments) to be displayed
	 * @param sender
	 *            Who might send a command (used to determine the visible commands)
	 * @return The help listing
	 */
	public HelpListing getHelpListing(String fullCommand, CommandSender sender) {
		return new HelpListing(fullCommand, getVisibleCommands(sender));
	}
	
	/**
	 * Sort the command list by the names of the commands.
	 * 
	 * @return this
	 */
	public CommandManager sortSelf() {
		return sortSelf((Command a, Command b) -> a.getName().compareTo(b.getName()));
	}
	
	/**
	 * Sort the command list. The provided {@link Comparator} is used to determine how the commands are sorted.
	 * 
	 * @param sortFunc
	 *            A {@link Comparator} used to determine how the commands are sorted.
	 * @return this
	 */
	public CommandManager sortSelf(Comparator<Command> sortFunc) {
		Collections.sort(_commands, sortFunc);
		return this;
	}
	
}
