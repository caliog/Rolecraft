package org.caliog.Rolecraft.Villagers.Quests;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Items.CustomItem;
import org.caliog.Rolecraft.Items.ItemEffect;
import org.caliog.Rolecraft.Items.Books.Book;
import org.caliog.Rolecraft.XMechanics.Utils.Utils;

public class QuestBook extends Book {

	public QuestBook(RolecraftPlayer clazz) {
		super("Quest Book", clazz);
		syncBookMeta();
	}

	public BookMeta cloneBookMeta() {
		return ((BookMeta) this.getItemMeta()).clone();
	}

	private void syncBookMeta() {
		if (!this.hasItemMeta())
			return;
		if (this.getItemMeta() instanceof BookMeta) {
			BookMeta meta = (BookMeta) this.getItemMeta();
			meta.setTitle("Quest Book");
			meta.setAuthor(player.getName());

			List<String> pages = new ArrayList<String>();

			String frontPage = "&0Quest Book\n";
			frontPage += "&8" + player.getCompletedQuests().size() + " of &8" + QManager.getQuests().size() + " completed!\n\n";
			int counter = 1;
			for (String id : player.getUnCompletedQuests()) {
				counter++;
				frontPage += "&8" + id + "&0 ..... page: " + counter + "\n";
			}

			pages.add(ChatColor.translateAlternateColorCodes('&', frontPage));

			for (String id : player.getUnCompletedQuests()) {
				Quest q = QManager.getQuest(id);
				String page = q.getName() + "\n\n";
				page += ChatColor.translateAlternateColorCodes('&', "&8" + q.getDescription()) + "\n";
				if (q.getMobs() != null && !q.getMobs().isEmpty())
					for (String m : q.getMobs().keySet()) {
						page += "&8" + QuestKill.getKilled(player.getPlayer(), m) + " &0/ " + q.getMobs().get(m) + " "
								+ m.replaceAll(" ", "") + "\n";
					}
				page += "\n";
				if (q.getCollects() != null && !q.getCollects().isEmpty())
					for (ItemStack stack : q.getCollects()) {
						String name;
						if (stack instanceof CustomItem)
							name = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
						else
							name = Utils.readable(stack.getType());
						page += "&8" + name + ": &0" + stack.getAmount() + "\n";
					}

				pages.add(ChatColor.translateAlternateColorCodes('&', page));
			}

			meta.setPages(pages);
			this.setItemMeta(meta);
		}

	}

	public static boolean isQuestBook(ItemStack stack) {
		if (!stack.hasItemMeta())
			return false;
		if (stack.getItemMeta() instanceof BookMeta) {
			BookMeta meta = (BookMeta) stack.getItemMeta();
			if (meta.getTitle().equals("Quest Book"))
				return true;
		}
		return false;
	}

	@Override
	public List<ItemEffect> getEffects() {
		return effects;
	}

	@Override
	public int getMinLevel() {
		return -1;
	}

	@Override
	public String getClazz() {
		return null;
	}

}
