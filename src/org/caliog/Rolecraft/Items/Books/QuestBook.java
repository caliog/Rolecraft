package org.caliog.Rolecraft.Items.Books;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Items.CustomItem;
import org.caliog.Rolecraft.Items.ItemEffect;
import org.caliog.Rolecraft.Villagers.Quests.QuestManager;
import org.caliog.Rolecraft.Villagers.Quests.Quest;
import org.caliog.Rolecraft.Villagers.Quests.YmlQuest;
import org.caliog.Rolecraft.Villagers.Quests.Menu.QuestInfoMenu;
import org.caliog.Rolecraft.XMechanics.Menus.Menu;
import org.caliog.Rolecraft.XMechanics.Menus.MenuItem;
import org.caliog.Rolecraft.XMechanics.Menus.MenuManager;
import org.caliog.Rolecraft.XMechanics.Utils.VersionControll.Mat;

public class QuestBook extends CustomItem {

	private final Player player;

	public QuestBook(Player player) {
		super(Material.ENCHANTED_BOOK, "Book of Quests", false);
		this.syncItemStack();
		this.player = player;
	}

	@Override
	public List<ItemEffect> getEffects() {
		return new ArrayList<ItemEffect>();
	}

	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public String getClazz() {
		return null;
	}

	public void clicked() {
		RolecraftPlayer rcp = PlayerManager.getPlayer(player.getUniqueId());
		if (rcp == null)
			return;
		List<String> quests = rcp.getUnCompletedQuests();
		int height = quests.size() / 9 + 1;
		MenuManager.openMenu(player, new Menu(height, "Book of Quests") {

			@Override
			public void init() {
				super.init();
				for (int i = 0; i < quests.size(); i++) {
					MenuItem item = new MenuItem(quests.get(i), Mat.BOOK_AND_QUILL.e());
					{
						final int j = i;
						item.setButtonClickHandler(item.new ButtonClickHandler(this) {

							@Override
							public void onClick(InventoryClickEvent event, Player player) {
								// TODO this cannot stay here forever!
								Quest quest = QuestManager.getQuest(quests.get(j));
								if (quest instanceof YmlQuest)
									MenuManager.openMenu(player, new QuestInfoMenu((YmlQuest) quest));
							}
						});
					}
					this.setItem(i, item);
				}
			}
		});
	}

	public String getLore() {
		return null;
	}

}
