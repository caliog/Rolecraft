package org.caliog.Rolecraft.XMechanics.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.caliog.Rolecraft.Entities.EntityManager;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Guards.GManager;
import org.caliog.Rolecraft.Guards.Guard;
import org.caliog.Rolecraft.Mobs.Mob;
import org.caliog.Rolecraft.Villagers.VManager;
import org.caliog.Rolecraft.Villagers.Chat.ChatManager;
import org.caliog.Rolecraft.Villagers.NPC.Trader;
import org.caliog.Rolecraft.Villagers.NPC.Villager;
import org.caliog.Rolecraft.Villagers.NPC.Villager.VillagerType;
import org.caliog.Rolecraft.Villagers.Quests.QManager;
import org.caliog.Rolecraft.Villagers.Quests.Quest;
import org.caliog.Rolecraft.Villagers.Quests.QuestKill;
import org.caliog.Rolecraft.Villagers.Quests.Utils.QuestInventory;

public class VillagerListener implements Listener {

	/*
	 * @Name: Interact
	 * 
	 * @Listen TO: Trader
	 * 
	 * @Cancel: true
	 * 
	 * @Category: Trader Interaction
	 * 
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void interactEvent(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof org.bukkit.entity.Villager) {
			event.setCancelled(true);
			org.bukkit.entity.Villager v = (org.bukkit.entity.Villager) event.getRightClicked();
			Villager vil = VManager.getVillager(v.getUniqueId());

			if (vil == null)
				return;
			ChatManager.interaction(event.getPlayer(), vil, false);
			if (vil.getType().equals(VillagerType.TRADER)) {
				Trader trader = (Trader) vil;
				trader.openInventory(event.getPlayer());
			}

		}
	}

	/*
	 * @Name: DamageEvent
	 * 
	 * @Listen TO: Villager
	 * 
	 * @Cancel: false
	 * 
	 * @Category: Villager interaction
	 * 
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void leftClick(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof org.bukkit.entity.Villager && event.getDamager() instanceof Player) {
			org.bukkit.entity.Villager v = (org.bukkit.entity.Villager) event.getEntity();
			Villager vil = VManager.getVillager(v.getUniqueId());
			if (vil == null)
				return;
			ChatManager.interaction((Player) event.getDamager(), vil, true);

		}
	}

	/*
	 * @Name: InteractEvent
	 * 
	 * @Listen TO: Villager
	 * 
	 * @Cancel: true
	 * 
	 * @Category: Villager protection
	 * 
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void interactEvent(EntityTargetEvent event) {
		if (event.getTarget() instanceof org.bukkit.entity.Villager)
			event.setCancelled(true);
	}

	/*
	 * @Name: DamageEvent
	 * 
	 * @Listen TO: Villager
	 * 
	 * @Cancel: true
	 * 
	 * @Category: Villager protection
	 * 
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void damageEvent(EntityDamageEvent event) {
		Guard guard = GManager.getGuard(event.getEntity().getUniqueId());
		if (VManager.getVillager(event.getEntity().getUniqueId()) != null || guard != null) {
			event.setCancelled(true);
		}
	}

	/*
	 * @Name: ChunkLoad
	 * 
	 * @Listen TO: Villager
	 * 
	 * @Cancel: false
	 * 
	 * @Category: Villager
	 * 
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void chunkLoad(ChunkLoadEvent event) {
		VManager.load(event.getChunk());
	}

	/*
	 * @Name: DeathEvent
	 * 
	 * @Listen TO: Mob
	 * 
	 * @Cancel: false
	 * 
	 * @Category: Quests
	 * 
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void death(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player)
			return;
		Mob m = EntityManager.getMob(event.getEntity().getUniqueId());
		if (m != null)
			if (m.getKillerId() != null)
				if (m != null) {
					RolecraftPlayer p = PlayerManager.getPlayer(m.getKillerId());
					if (p != null) {
						for (String i : p.getUnCompletedQuests()) {
							Quest q = QManager.getQuest(i);
							if (q != null && q.getMobs() != null && !q.getMobs().isEmpty()) {
								if (q.getMobs().containsKey(m.getName())
										&& q.getMobs().get(m.getName()) > QuestKill.getKilled(p.getPlayer(), m.getName())) {
									QuestKill.killed(p.getPlayer(), m);
									QManager.updateQuestBook(p);
									break;
								}
							}
						}

					}
				}

	}

	/*
	 * @Name: EntityTarget
	 * 
	 * @Listen TO: Player
	 * 
	 * @Cancel: true
	 * 
	 * @Category: Mob restriction
	 * 
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void entityTargetVillager(final EntityTargetLivingEntityEvent event) {
		Mob mob = EntityManager.getMob(event.getEntity().getUniqueId());
		if (mob == null)
			return;
		if (event == null || event.getTarget() == null)
			return;
		if (GManager.isGuard(event.getTarget()) || VManager.getVillager(event.getTarget().getUniqueId()) != null) {
			event.setCancelled(true);
		}

	}

	/*
	 * @Name: InventoryClick
	 * 
	 * @Listen TO: Inventory
	 * 
	 * @Cancel: true
	 * 
	 * @Category: Quest
	 * 
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void inventoryClick(final InventoryClickEvent event) {
		if (event.getView() instanceof QuestInventory && event.getWhoClicked() instanceof Player) {
			boolean cancel = ((QuestInventory) event.getView()).inventoryClick(event);
			if (cancel)
				event.setCancelled(cancel);
		}
	}

	/*
	 * @Name: InventoryClose
	 * 
	 * @Listen TO: Inventory
	 * 
	 * @Cancel: false
	 * 
	 * @Category: Quest
	 * 
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void inventoryClose(final InventoryCloseEvent event) {
		if (event.getView() instanceof QuestInventory) {
			((QuestInventory) event.getView()).closed();
		}
	}
}
