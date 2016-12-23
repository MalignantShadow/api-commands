package cookies;

import info.malignantshadow.api.util.StringUtil;
import info.malignantshadow.api.util.aliases.Nameable;

public enum CookieType implements Nameable {
	
	CHOCOLATE_CHIP,
	M_AND_M,
	OATMEAL,
	OATMEAL_RAISIN,
	OREO,
	SUGAR;
	
	@Override
	public String getName() {
		return StringUtil.toProperCase(name().replaceAll("_", " "));
	}
	
}
