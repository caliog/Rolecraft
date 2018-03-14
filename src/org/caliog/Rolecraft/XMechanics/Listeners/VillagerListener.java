package org.caliog.Rolecraft.XMechanics.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.Entities.EntityManager;
import org.caliog.Rolecraft.Entities.Player.PlayerManager;
import org.caliog.Rolecraft.Entities.Player.RolecraftPlayer;
import org.caliog.Rolecraft.Guards.GManager;
import org.caliog.Rolecraft.Guards.Guard;
import org.caliog.Rolecraft.Items.Books.QuestBook;
import org.caliog.Rolecraft.Mobs.Mob;
import org.caliog.Rolecraft.Villagers.VManager;
import org.caliog.Rolecraft.Villagers.Chat.ChatManager;
import org.caliog.Rolecraft.Villagers.NPC.Trader;
import org.caliog.Rolecraft.Villagers.NPC.Villager;
import org.caliog.Rolecraft.Villagers.NPC.Villager.VillagerType;
import org.caliog.Rolecraft.Villagers.Quests.QuestKill;

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
	@EventHandler(priority = EventPriority.MONITOR)
	public void interactEvent(PlayerInteractEntityEvent event) {
		// this is called twice by system
		if (event.getRightClicked() instanceof org.bukkit.entity.Villager) {
			org.bukkit.entity.Villager v = (org.bukkit.entity.Villager) event.getRightClicked();
			Villager vil = VManager.getVillager(v.getUniqueId());

			if (vil == null)
				return;
			event.setCancelled(true);
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
		if (m != null && m.getKillerId() != null) {
			RolecraftPlayer p = PlayerManager.getPlayer(m.getKillerId());
			if (p != null) {
				QuestKill.killed(p.getPlayer(), m);
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
		if (event == null || event.getTarget() == null)
			return;
		if (VManager.getVillager(event.getTarget().getUniqueId()) != null) {
			event.setCancelled(true);
			return;
		}
		Mob mob = EntityManager.getMob(event.getEntity().getUniqueId());
		if (mob == null)
			return;
		if (GManager.isGuard(event.getTarget())) {
			event.setCancelled(true);
		}

	}

	/*
	 * @Name: InteractEvent
	 * 
	 * @Listen TO: Player
	 * 
	 * @Cancel: true
	 * 
	 * @Category: Quest Book
	 * 
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void playerInteract(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		QuestBook dummy = new QuestBook(event.getPlayer());
		if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
				&& item.getItemMeta().getDisplayName().equals(dummy.getItemMeta().getDisplayName())) {
			dummy.clicked();
		}
	}

}
