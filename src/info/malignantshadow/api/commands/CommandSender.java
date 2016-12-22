package info.malignantshadow.api.commands;

/**
 * Represents a command sender. By default, messages are sent to {@link System#out} ({@link System#err} for errors)
 * 
 * @author MalignantShadow (Caleb Downs)
 *
 */
public class CommandSender {
	
	/**
	 * Print the object to this command sender.
	 * 
	 * @param o
	 *            The object
	 */
	public void print(Object o) {
		print(o == null ? "null" : o.toString());
	}
	
	/**
	 * Print a message to this command sender.
	 * 
	 * @param message
	 *            The message
	 */
	public void print(String message) {
		System.out.println(message);
	}
	
	/**
	 * Print a message to this command sender. Equivalent to <code>{@link #print(String) print}(String.format(format, args))</code>
	 * 
	 * @param format
	 *            The format to use
	 * @param args
	 *            The arguments
	 * @see String#format(String, Object...)
	 */
	public void print(String format, Object... args) {
		print(String.format(format, args));
	}
	
	/**
	 * Print the object to this command sender as an error.
	 * 
	 * @param o
	 *            The object
	 */
	public void printErr(Object o) {
		printErr(o == null ? "null" : o.toString());
	}
	
	/**
	 * Print an error to this command sender.
	 * 
	 * @param message
	 *            The message
	 */
	public void printErr(String message) {
		System.err.println(message);
	}
	
	/**
	 * Print an error to this command sender. Equivalent to <code>{@link #printErr(String) printErr}(String.format(format, args))</code>
	 * 
	 * @param format
	 *            The format to use
	 * @param args
	 *            The arguments
	 * @see String#format(String, Object...)
	 */
	public void printErr(String format, Object... args) {
		printErr(String.format(format, args));
	}
	
}
