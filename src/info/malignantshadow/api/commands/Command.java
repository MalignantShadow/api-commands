package info.malignantshadow.api.commands;

import java.util.ArrayList;
import java.util.List;

import info.malignantshadow.api.util.ListUtil;
import info.malignantshadow.api.util.aliases.Aliasable;
import info.malignantshadow.api.util.arguments.Argument;
import info.malignantshadow.api.util.arguments.ArgumentHolder;
import info.malignantshadow.api.util.arguments.ArgumentList;
import info.malignantshadow.api.util.arguments.ParsedArguments;

/**
 * Represents a command and its arguments.
 * 
 * @author MalignantShadow (Caleb Downs)
 *
 */
public class Command implements Aliasable, ArgumentHolder {
	
	private String _name, _desc;
	private List<String> _aliases;
	private Handler _handler;
	private ArgumentList _args;
	private CommandManager _nested;
	private boolean _hidden;
	
	private static final Handler UNKNOWN_SUBCOMMAND = (context) -> {
		String[] extra = context.getExtra();
		if (extra.length == 0)
			return;
		
		String sub = extra[0];
		if (sub == null || sub.isEmpty())
			return;
		
		context.printErr("Unknown sub-command: '%s'", sub);
	};
	
	/**
	 * Construct a new Command. Names and aliases cannot be null, empty, or contain spaces.
	 * 
	 * @param name
	 *            The name of the command
	 * @param desc
	 *            A String description the command
	 * @param aliases
	 *            Aliases for the command (Optional)
	 */
	public Command(String name, String desc, String... aliases) {
		checkAlias(name);
		_name = name;
		_desc = desc;
		_aliases = new ArrayList<String>();
		
		if (aliases != null && aliases.length > 0)
			for (String s : aliases)
				_aliases.add(s);
			
		_args = new ArgumentList();
	}
	
	static void checkAlias(String alias) {
		if (alias == null)
			throw new IllegalArgumentException("alias cannot be null");
		else if (alias.isEmpty())
			throw new IllegalArgumentException("alias cannot be empty");
		else if (alias.contains(" "))
			throw new IllegalArgumentException("alias cannot contain spaces");
	}
	
	/**
	 * Get the name of this command.
	 * 
	 * @return This command's name
	 */
	@Override
	public String getName() {
		return _name;
	}
	
	/**
	 * Get this command's description
	 * 
	 * @return A String describing what this command does when invoked
	 */
	public String getDescription() {
		return _desc;
	}
	
	/**
	 * Get the argument list for this command
	 * 
	 * @return This command's argument list
	 */
	@Override
	public ArgumentList getArguments() {
		return _args;
	}
	
	/**
	 * Set the arguments of this command
	 * 
	 * @param args
	 *            The arguments
	 */
	@Override
	public Command withArgs(ArgumentList args) {
		_args = args;
		return this;
	}
	
	/**
	 * Add an argument to this command's argument list
	 * 
	 * @param arg
	 *            The argument to add
	 * @return this
	 */
	public Command withArg(Argument arg) {
		_args.add(arg);
		return this;
	}
	
	/**
	 * Set the extra argument for this command.
	 * 
	 * @param display
	 *            The display of the argument
	 * @param description
	 *            The description of the argument
	 * @param required
	 *            Whether the argument is required
	 * @return this
	 * @see ArgumentList#setExtraArgument(String, String, boolean)
	 */
	public Command withExtra(String display, String description, boolean required) {
		_args.setExtraArgument(display, description, required);
		return this;
	}
	
	/**
	 * Get the aliases for this command (not including its name).
	 * 
	 * @return The aliases for this command
	 */
	@Override
	public String[] getAliases() {
		return getAliases(false).toArray(new String[0]);
	}
	
	/**
	 * Get the aliases for this command, optionally including the name of this command.
	 * 
	 * @param includeName
	 *            If {@code true}, include this command's name as the first item in the list.
	 * @return The aliases
	 */
	public List<String> getAliases(boolean includeName) {
		List<String> list = new ArrayList<String>();
		if (includeName)
			list.add(_name);
		list.addAll(_aliases);
		return list;
	}
	
	/**
	 * And an alias to this command. The alias should not be null, empty, or contain spaces.
	 * 
	 * @param alias
	 *            The alias to add
	 */
	public Command withAlias(String alias) {
		checkAlias(alias);
		_aliases.add(alias);
		return this;
	}
	
	/**
	 * Does this command have the given alias?
	 * 
	 * @param alias
	 *            The alias to search for
	 * @return true if this command has the given alias
	 */
	public boolean hasAlias(String alias) {
		return ListUtil.contains(getAliases(true), (s) -> s.equalsIgnoreCase(alias));
	}
	
	/**
	 * Does this command have any aliases (not including its name)?
	 * 
	 * @return true if this command has any aliases set
	 */
	public boolean hasAliases() {
		return !_aliases.isEmpty();
	}
	
	/**
	 * Create a command context to be given to a {@link CommandHandler}.
	 *
	 * @param sender
	 *            Who is sending the command
	 * @param cmdPrefix
	 *            The prefix to the command. This is the full command string up to and including the command's name.
	 * @param args
	 *            The arguments to give the command
	 * @return A new command context, or {@code null} if not enough arguments were given.
	 */
	public CommandContext createContext(CommandSender sender, String cmdPrefix, String[] args) {
		if (args == null)
			args = new String[0];
		
		// no need to parse arguments if there aren't enough
		ArgumentList arguments = getArguments();
		int min = arguments.getMinimum();
		if (args.length < min)
			return null;
		
		ParsedArguments parsed = new ParsedArguments(arguments, args);
		return new CommandContext(cmdPrefix, sender, this, parsed);
	}
	
	/**
	 * Set the {@link Handler} for this command. When this command is run, {@link Handler#handleCommand(CommandContext) handleCommand(CommandContext)}
	 * in the given handler will be invoked (if the handler is not null).
	 * 
	 * @param handler
	 *            The handler
	 * @return this
	 */
	public Command withHandler(Handler handler) {
		_handler = handler;
		return this;
	}
	
	/**
	 * Set the {@link Handler} for this command to send an "unknown sub-command" error to the sender. This useful for commands
	 * that accept sub-commands, but not the command by itself. Equivalent to: {@code withHandler(Command.UNKNOWN_SUBCOMMAND)}
	 * 
	 * 
	 * @return this
	 */
	public Command withUnknownSubCommandHandler() {
		return withHandler(UNKNOWN_SUBCOMMAND);
	}
	
	/**
	 * Get the {@link CommandHandler} for this command.
	 * 
	 * @return the handler
	 */
	public Handler getHandler() {
		return _handler;
	}
	
	/**
	 * Set the sub-commands for this command.
	 * 
	 * @param manager
	 *            the nested command manager.
	 * @return this
	 */
	public Command withSubCommands(CommandManager manager) {
		_nested = manager;
		return this;
	}
	
	/**
	 * Does this command have a nested command manager?
	 * 
	 * @return true if this command has a nested command manager (i.e. not null)
	 */
	public boolean isNested() {
		return _nested != null;
	}
	
	/**
	 * Does this command have sub-commands?
	 * 
	 * @return true if {@link #isNested()} returns {@code true} and the nested command manager has commands
	 *         ({@link CommandManager#getCommands() getCommands()} is not empty)
	 */
	public boolean hasSubCommands() {
		return isNested() && !_nested.getCommands().isEmpty();
	}
	
	/**
	 * Get this command's nested {@link CommandManager} (if any).
	 * 
	 * @return this command's nested {@link CommandManager} (may be null)
	 */
	public CommandManager getNestedManager() {
		return _nested;
	}
	
	/**
	 * Set that this command should be hidden from help listings.
	 * 
	 * <p>
	 * Note: this is a <i>HINT</i>. Implementations of {@link info.malignantshadow.api.commands.HelpListing HelpListing} may ignore this when showing visible commands.
	 * All implementations of {@link info.malignantshadow.api.commands.HelpListing HelpListing} provided by this API respect this field.
	 * </p>
	 * 
	 * @return this
	 */
	public Command thatIsHidden() {
		return thatMayBeHidden(true);
	}
	
	/**
	 * Set this to true if the command should be hidden from help listings.
	 * 
	 * <p>
	 * Note: this is a <i>HINT</i>. Implementations of {@link info.malignantshadow.api.commands.HelpListing HelpListing} may ignore this when showing visible commands.
	 * All implementations of {@link info.malignantshadow.api.commands.HelpListing HelpListing} provided by this API respect this field.
	 * </p>
	 * 
	 * @param hidden
	 *            Whether this command should be hidden from help listings.
	 * @return this
	 */
	public Command thatMayBeHidden(boolean hidden) {
		_hidden = hidden;
		return this;
	}
	
	/**
	 * Should this command be hidden from help listings?
	 * 
	 * @return true if this command would like to hide.
	 */
	public boolean shouldBeHidden() {
		return _hidden;
	}
	
	/**
	 * 
	 * A method that is called when a command is invoked.
	 * 
	 * @author MalignantShadow (Caleb Downs)
	 *
	 */
	@FunctionalInterface
	public static interface Handler {
		
		/**
		 * Handle the command.
		 * 
		 * @param context
		 *            The context of the command invocation.
		 */
		public void handleCommand(CommandContext context);
		
	}
	
}
