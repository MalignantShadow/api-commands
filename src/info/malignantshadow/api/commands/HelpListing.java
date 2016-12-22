package info.malignantshadow.api.commands;

import java.util.ArrayList;
import java.util.List;

import info.malignantshadow.api.util.ListUtil;
import info.malignantshadow.api.util.arguments.Argument;
import info.malignantshadow.api.util.arguments.ArgumentList;

/**
 * Utility class for creating help listings. HelpListings can be used to format help messages to {@link CommandSender}s.
 * 
 * @author MalignantShadow (Caleb Downs)
 *
 */
public class HelpListing {
	
	private List<Command> _commands;
	private String _fullCmd;
	
	/**
	 * Construct a new HelpListing.
	 * 
	 * @param fullCmd
	 *            The full command String use when activate the help message
	 * @param commands
	 *            A possible commands to show
	 */
	public HelpListing(String fullCmd, List<Command> commands) {
		_commands = commands;
		_fullCmd = fullCmd;
	}
	
	/**
	 * Format the full command.
	 * 
	 * <p>
	 * This method can be overridden to provide extra information if needed. For instance,
	 * {@link info.malignantshadow.api.bukkit.commands.BukkitHelpListing BukkitHelpListing} adds colors.
	 * </p>
	 * 
	 * @return The formatted full command
	 */
	public String formatFullCommand() {
		return formatFullCommand(_fullCmd);
	}
	
	/**
	 * Format a command string.
	 * 
	 * <p>
	 * This method can be overridden to provide extra information if needed. For instance,
	 * {@link info.malignantshadow.api.bukkit.commands.BukkitHelpListing BukkitHelpListing} adds colors.
	 * </p>
	 * 
	 * @param fullCmd
	 *            The full command string
	 * @return The formatted full command.
	 */
	public String formatFullCommand(String fullCmd) {
		return fullCmd;
	}
	
	/**
	 * Format an argument. By default, this method simply wraps the argument in &lt;&gt; if it is required or [] it is not.
	 * 
	 * <p>
	 * This method can be overridden to provide extra information if needed. For instance,
	 * {@link info.malignantshadow.api.bukkit.commands.BukkitHelpListing BukkitHelpListing} adds colors.
	 * </p>
	 * 
	 * @param arg
	 *            The argument display
	 * @param required
	 *            <code>true</code> if the argument is required
	 * @return The formatted argument
	 */
	public String formatArg(String arg, boolean required) {
		return String.format(required ? "<%s>" : "[%s]", arg);
		
	}
	
	/**
	 * Format multiple arguments. <code>minArgCount</code> can be used to determine which arguments are required. This method will iterate over the
	 * arguments, and call {@link #formatArg(String, boolean)} for each one. The resulting string is all formatted arguments with a space in between.
	 * 
	 * <p>
	 * This method can be overridden to provide extra information if needed. For instance,
	 * {@link info.malignantshadow.api.bukkit.commands.BukkitHelpListing BukkitHelpListing} adds colors.
	 * </p>
	 * 
	 * @param args
	 *            The arguments to format
	 * @param minArgCount
	 *            The amount of arguments required, starting at the beginning
	 * @return The formatted arguments.
	 */
	public String formatArgs(ArgumentList args) {
		String argHelp = "";
		for (Argument a : args)
			argHelp += formatArg(a.getDisplay(), a.isRequired()) + " ";
		return argHelp.trim();
	}
	
	/**
	 * Format aliases. By default, this method simply joins all aliases with a forward slash (<code>/</code>)
	 * 
	 * <p>
	 * This method can be overridden to provide extra information if needed. For instance,
	 * {@link info.malignantshadow.api.bukkit.commands.BukkitHelpListing BukkitHelpListing} adds colors.
	 * </p>
	 * 
	 * @param aliases
	 *            The aliases to format
	 * @return The formatted aliases.
	 */
	public String formatAliases(List<String> aliases) {
		return ListUtil.join(aliases, "/");
	}
	
	/**
	 * Format a description. By default, this method simply prepends <code>"- "</code> to the description, as long as it isn't null or empty.
	 * Otherwise, this method returns an empty string.
	 * 
	 * <p>
	 * This method can be overridden to provide extra information if needed. For instance,
	 * {@link info.malignantshadow.api.bukkit.commands.BukkitHelpListing BukkitHelpListing} adds colors.
	 * </p>
	 * 
	 * @param desc
	 *            The description to format
	 * @return The formatted description.
	 */
	public String formatDescription(String desc) {
		if (desc == null || desc.isEmpty())
			return "";
		
		return "- " + desc;
	}
	
	/**
	 * Format a command.
	 * 
	 * <p>
	 * This method can be overridden to provide extra information if needed. For instance,
	 * {@link info.malignantshadow.api.bukkit.commands.BukkitHelpListing BukkitHelpListing} adds colors.
	 * </p>
	 * 
	 * @param command
	 *            The command to format
	 * @return A String describing the syntax of the command, in the form of "{COMMAND_NAME} {COMMAND_ARGUMENTS} {COMMAND_DESCRIPTION}"
	 */
	public String formatSimpleCommand(Command command) {
		ArgumentList args = command.getArguments();
		Argument extra = args.getExtraArgument();
		if (extra != null) {
			args = new ArgumentList(args);
			args.add(extra);
		}
		
		String listing = formatAliases(command.getAliases(true)) + " ";
		listing += formatArgs(args);
		return listing.trim() + " " + formatDescription(command.getDescription());
	}
	
	/**
	 * Format a command.
	 * 
	 * <p>
	 * This method can be overridden to provide extra information if needed. For instance,
	 * {@link info.malignantshadow.api.bukkit.commands.BukkitHelpListing BukkitHelpListing} adds colors.
	 * </p>
	 * 
	 * @param command
	 *            The command to format
	 * @return A String describing the command's syntax - "{COMMAND_NAME} &lt;command&gt;"
	 */
	public String formatCommandNested(Command command) {
		ArgumentList dummyArgs = new ArgumentList();
		dummyArgs.add(new Argument("command", "The sub-command to run", true));
		return (formatAliases(command.getAliases(true)) + " " + formatArgs(dummyArgs)).trim() + " " + formatDescription(command.getDescription());
	}
	
	/**
	 * Get the help string for a command.
	 * 
	 * @param cmd
	 *            The command to get help for
	 * @return A string describing the command's syntax.
	 * @see #formatSimpleCommand(Command)
	 * @see #formatCommandNested(Command)
	 */
	public String getCommandHelp(Command cmd) {
		if (cmd.isNested())
			return formatCommandNested(cmd);
		return formatSimpleCommand(cmd);
	}
	
	/**
	 * Get the list of commands this {@link HelpListing} is using.
	 * 
	 * @return The command list.
	 */
	public List<Command> getCommands() {
		return _commands;
	}
	
	/**
	 * Get the full command String this {@link HelpListing} was constructed with.
	 * 
	 * @return The full command String
	 */
	public String getFullCmd() {
		return _fullCmd;
	}
	
	/**
	 * Get the first help page.
	 * 
	 * @return The first help page.
	 */
	public List<String> getHelp() {
		return getHelp(1);
	}
	
	/**
	 * Get a certain page of help in the listing. Note that by default <code>page</code> does nothing.
	 * 
	 * @param page
	 *            The page to display
	 * @return The help page.
	 */
	public List<String> getHelp(int page) {
		List<String> help = new ArrayList<String>();
		help.add("Usage: " + _fullCmd + " <command>");
		help.add("");
		
		help.add("Commands:");
		for (Command c : getCommands())
			help.add("  " + getCommandHelp(c));
		
		return help;
	}
	
}
