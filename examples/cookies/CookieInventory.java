package cookies;

import java.util.HashMap;
import java.util.Map;

public class CookieInventory {
	
	private static CookieInventory INSTANCE;
	
	static {
		INSTANCE = new CookieInventory();
	}
	
	public static CookieInventory getInventory() {
		return INSTANCE;
	}
	
	private Map<CookieType, Integer> _inv;
	
	private CookieInventory() {
		_inv = new HashMap<CookieType, Integer>();
	}
	
	public void add(CookieType type, int amount) {
		Integer a = _inv.get(type);
		if (a == null)
			a = amount;
		else
			a += amount;
		
		_inv.put(type, Math.max(0, a));
	}
	
	public void remove(CookieType type, int amount) {
		add(type, -amount);
	}
	
	public int getAmount(CookieType type) {
		return _inv.get(type);
	}
	
}
