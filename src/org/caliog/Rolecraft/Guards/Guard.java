package org.caliog.Rolecraft.Guards;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.caliog.Rolecraft.XMechanics.Utils.IO.DataSaver;
import org.caliog.Rolecraft.XMechanics.npclib.NPC;

public class Guard extends GNPC {
	private boolean attackMonster;
	private boolean attackAnimal;
	private LivingEntity currentTargetEntity;
	private long lastShot;
	private int attackCount = 0;

	public Guard(String name, Location loc, int id, String eq) {
		super(name, loc, id);
		this.setIsLooking(true);
		this.setAttackMonster(true);
		equipment(eq);
	}

	private void equipment(String eq) {
		if (eq == null)
			return;
		String split[] = eq.split("#");
		if (split.length != 6)
			return;
		ItemStack stack[] = new ItemStack[4];
		for (int i = 2; i < 6; i++)
			stack[i - 2] = DataSaver.getItem(split[i]);
		this.setEquipment(DataSaver.getItem(split[0]), DataSaver.getItem(split[1]), stack);

	}

	public double getDamage() {
		return 50;
	}

	public boolean isAttackMonster() {
		return this.attackMonster;
	}

	public void setAttackMonster(boolean attackMob) {
		this.attackMonster = attackMob;
	}

	public boolean isAttackAnimal() {
		return this.attackAnimal;
	}

	public void setAttackAnimal(boolean attackMob) {
		this.attackAnimal = attackMob;
	}

	public String getAttackings() {
		if ((this.attackMonster) && (this.attackAnimal))
			return "Animals and Monsters!";
		if ((this.attackMonster) && (!this.attackAnimal))
			return "Monsters!";
		if ((!this.attackMonster) && (this.attackAnimal)) {
			return "Animals!";
		}
		return "Nobody!";
	}

	public void setAttackings(String s) {
		s = s.toLowerCase();
		setAll(false);
		if (s.contains("all"))
			setAll(true);
		else if (s.contains("no") || s.contains("nobody") || s.contains("none") || s.contains("nothing"))
			setAll(false);
		if (s.contains("monster"))
			this.attackMonster = true;
		if (s.contains("animal"))
			this.attackAnimal = true;

	}

	private void setAll(boolean b) {
		this.attackMonster = b;
		this.attackAnimal = b;
	}

	public boolean isDamageable() {
		return false;
	}

	public void setAttacking(LivingEntity e) {
		this.attackCount = 0;
		this.currentTargetEntity = e;
	}

	public boolean isAttacking() {
		return currentTargetEntity != null;
	}

	public LivingEntity getAttacking() {
		return currentTargetEntity;
	}

	public void attack() {
		if (attackCount > 20)
			setAttacking(null);
		LivingEntity target = currentTargetEntity;
		if (target != null) {
			if (((!(target instanceof Player)) || (target instanceof Player && !((Player) target).isFlying()))) {
				if (((Player) npc.getBukkitEntity()).getInventory().getItemInMainHand().getType().equals(Material.BOW)) {
					Location loc = target.getEyeLocation();
					if (loc != null)
						if (lastShot + 20 < target.getWorld().getTime()
								&& loc.distanceSquared(npc.getBukkitEntity().getLocation()) < 1098) {

							final double c = 0.55;

							double f = c * (loc.distance(npc.getBukkitEntity().getLocation()) - 10);
							if (f < 0)
								f = 0;
							loc.setY(loc.getY() + f);

							npc.lookAtPoint(loc);

							((LivingEntity) npc.getBukkitEntity()).launchProjectile(Arrow.class);

							this.attackCount++;

							this.lastShot = target.getWorld().getTime();
						}
				} else {
					setRunning(true);
					npc.walkTo(target.getLocation());
					((NPC) npc).lookAtPoint(target.getEyeLocation());
					if (npc.getBukkitEntity().getLocation().distanceSquared(target.getLocation()) <= 1.44) {
						npc.animateArmSwing();
						target.damage(this.getDamage());
						this.attackCount++;
					}
				}
			}
		}
	}

	public UUID getUniqueId() {
		return getNpc().getBukkitEntity().getUniqueId();
	}

	@Override
	public int getRadius() {
		return 20;
	}

	public void setEquipment(ItemStack stack, ItemStack shield, ItemStack[] armor) {
		this.getNpc().getInventory().setItemInMainHand(stack);
		this.getNpc().getInventory().setItemInOffHand(shield);
		this.getNpc().getInventory().setArmorContents(armor);
		this.getNpc().updateEquipment();

	}

	public String getEquipmentString() {
		return DataSaver.save(this.getNpc().getInventory().getItemInMainHand()) + "#"
				+ DataSaver.save(this.getNpc().getInventory().getItemInOffHand()) + "#"
				+ DataSaver.save(this.getNpc().getInventory().getBoots()) + "#" + DataSaver.save(this.getNpc().getInventory().getLeggings())
				+ "#" + DataSaver.save(this.getNpc().getInventory().getChestplate()) + "#"
				+ DataSaver.save(this.getNpc().getInventory().getHelmet()) + "#";

	}

	@Override
	public void walkTo(Location loc, int m) {
		getNpc().walkTo(loc, m);

	}

	@Override
	public void moveTo(Location loc) {
		getNpc().moveTo(loc);
	}
}