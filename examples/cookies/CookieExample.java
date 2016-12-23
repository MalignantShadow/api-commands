package cookies;

import info.malignantshadow.api.commands.Command;
import info.malignantshadow.api.commands.CommandManager;
import info.malignantshadow.api.commands.CommandSender;
import info.malignantshadow.api.util.arguments.Argument;
import info.malignantshadow.api.util.arguments.ArgumentTypes;

public class CookieExample {
	
	public static void main(String[] args) {
		CommandSender me = new CommandSender();
		CommandManager commands = new CommandManager()
			.push(new Command("cookie", "Commands related to cookies")
				.withSubCommands(new CommandManager()
					.push(new Command("eat", "Eat a cookie")
						.withArg(new Argument("type", "The type of cookie to eat", true)
							.withAcceptedTypes(ArgumentTypes.enumValue(CookieType.values())))
						.withArg(new Argument("amount", "The amount of cookies to eat. (Default: 1)", false)
							.withAcceptedTypes(ArgumentTypes.INT)
							.withDefault(1))
						.withHandler(CookieCommand::eat))
					.push(new Command("bake", "Bake some cookies")
						.withArg(new Argument("type", "The type of cookie to eat", true)
							.withAcceptedTypes(ArgumentTypes.enumValue(CookieType.values())))
						.withArg(new Argument("amount", "The amount of cookies to eat. (Default: 1)", false)
							.withAcceptedTypes(ArgumentTypes.INT)
							.withDefault(1))
						.withHandler(CookieCommand::bake))
					.push(new Command("inventory", "Check the inventory", "inv")
						.withArg(new Argument("type", "The type of cookie to check (Default: all types)", false)
							.withAcceptedTypes(ArgumentTypes.enumValue(CookieType.values()))
							.thatMayBeNull())
						.withHandler(CookieCommand::inventory))
					.withHelpCommand()))
			.withHelpCommand();
		
		commands.dispatch(me, "cookie help");
		me.print("");
		commands.dispatch(me, "cookie ? eat");
	}
	
}
