package org.caliog.Rolecraft.Spells.Menu;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Items.Custom.Spellbook;
import org.caliog.Rolecraft.Spells.Spell;
import org.caliog.Rolecraft.XMechanics.Menus.Menu;
import org.caliog.Rolecraft.XMechanics.Menus.MenuInventoryView;
import org.caliog.Rolecraft.XMechanics.Menus.MenuItem;
import org.caliog.Rolecraft.XMechanics.Messages.MessageKey;
import org.caliog.Rolecraft.XMechanics.Messages.Msg;
import org.caliog.Rolecraft.XMechanics.Messages.Translator.Phrase;
import org.caliog.Rolecraft.XMechanics.Utils.Pair;

public class SpellMenu extends Menu {

	private RolecraftPlayer player;

	public SpellMenu(RolecraftPlayer player) {
		super(1, "Spellbook");
		this.player = player;
		setup();
	}

	private void setup() {
		MenuItem item = null;
		ArrayList<String> lore;
		boolean t = player.getSpellPoints() > 0;
		HashMap<String, Pair<Spell, Integer>> map = player.getSpells();
		int c = -1;
		for (String k : map.keySet()) {
			c++;
			if (c >= 8)
				break;
			lore = new ArrayList<String>();
			Pair<Spell, Integer> value = map.get(k);
			lore.add(ChatColor.DARK_GRAY + "Power: " + value.second);
			String e = k.replaceAll("1", ChatColor.RED + Phrase.LEFT.translate() + ChatColor.GRAY + "-").replaceAll("0",
					ChatColor.BLUE + Phrase.RIGHT.translate() + ChatColor.GRAY + "-");
			lore.add(ChatColor.AQUA + e.substring(0, e.length() - 1));
			if (t)
				lore.add(ChatColor.GOLD + Msg.getMessage(MessageKey.SPELL_CLICK_POWER));
			item = new MenuItem(value.first.getName(), Material.BOOK_AND_QUILL, lore);
			{
				final MenuItem final_item = item;
				if (t)
					item.setButtonClickHandler(item.new ButtonClickHandler(this) {

						@Override
						public void onClick(InventoryClickEvent event, Player player) {
							((SpellMenu) getMenu()).player.powerUpSpell(final_item.getName());
							setup();
							((MenuInventoryView) event.getView()).reload();
							Spellbook.refresh(((SpellMenu) getMenu()).player);
						}
					});
			}
			this.setItem(c, item);
		}

		this.setItem(height * 9 - 1, new MenuItem().new ExitButton(this, "Exit"));
	}

}
