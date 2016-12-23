package cookies;

import info.malignantshadow.api.commands.CommandContext;

public class CookieCommand {
	
	public static void eat(CommandContext context) {
		int amount = (Integer) context.get("amount");
		CookieInventory.getInventory().remove((CookieType) context.get("type"), amount);
		context.print("Removed %d cookie%s", amount, amount == 1 ? "" : "s");
	}
	
	public static void bake(CommandContext context) {
		int amount = (Integer) context.get("amount");
		CookieInventory.getInventory().remove((CookieType) context.get("type"), amount);
		context.print("Created %d cookie%s", amount, amount == 1 ? "" : "s");
	}
	
	public static void inventory(CommandContext context) {
		CookieType type = (CookieType) context.get("type");
		if (type == null) {
			for (CookieType t : CookieType.values())
				context.print("%s - %d", t.getName(), CookieInventory.getInventory().getAmount(t));
			return;
		}
		int amount = CookieInventory.getInventory().getAmount(type);
		context.print("%d cooki%s of type %s", amount, amount == 1 ? "" : "s", type.getName());
	}
	
}
