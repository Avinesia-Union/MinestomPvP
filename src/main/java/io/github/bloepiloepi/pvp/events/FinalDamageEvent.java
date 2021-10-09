package io.github.bloepiloepi.pvp.events;

import io.github.bloepiloepi.pvp.damage.CustomDamageType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The damage event containing the final damage, calculation of armor and effects included.
 * This event should be used, unless you want to detect how much damage was originally dealt.
 */
public class FinalDamageEvent implements EntityEvent, CancellableEvent {
	
	private final LivingEntity entity;
	private final CustomDamageType damageType;
	private float damage;
	private int invulnerabilityTicks = 10;
	
	private boolean cancelled;
	
	public FinalDamageEvent(@NotNull LivingEntity entity, @NotNull CustomDamageType damageType, float damage) {
		this.entity = entity;
		this.damageType = damageType;
		this.damage = damage;
	}
	
	@NotNull
	@Override
	public LivingEntity getEntity() {
		return entity;
	}
	
	/**
	 * Gets the damage type.
	 *
	 * @return the damage type
	 */
	@NotNull
	public CustomDamageType getDamageType() {
		return damageType;
	}
	
	/**
	 * Gets the damage amount.
	 *
	 * @return the damage amount
	 */
	public float getDamage() {
		return damage;
	}
	
	/**
	 * Changes the damage amount.
	 *
	 * @param damage the new damage amount
	 */
	public void setDamage(float damage) {
		this.damage = damage;
	}
	
	/**
	 * Gets the amount of ticks the entity is invulnerable after the damage has been applied.
	 * By default it is 10 (half a second).
	 *
	 * @return the amount of ticks the entity is invulnerable
	 */
	public int getInvulnerabilityTicks() {
		return invulnerabilityTicks;
	}
	
	/**
	 * Sets the amount of ticks the entity is invulnerable after the damage has been applied.
	 * By default it is 10 (half a second).
	 *
	 * @param invulnerabilityTicks the amount of ticks the entity is invulnerable
	 */
	public void setInvulnerabilityTicks(int invulnerabilityTicks) {
		this.invulnerabilityTicks = invulnerabilityTicks;
	}
	
	/**
	 * Checks if the damage will kill the entity.
	 * <br><br>
	 * This requires some computing, caching the result may
	 * be better if used frequently.
	 *
	 * @return {@code true} if the entity will be killed, {@code false} otherwise
	 */
	public boolean doesKillEntity() {
		float remainingDamage = damage;
		
		// Additional hearts support
		if (entity instanceof Player) {
			final Player player = (Player) entity;
			final float additionalHearts = player.getAdditionalHearts();
			if (additionalHearts > 0) {
				if (remainingDamage > additionalHearts) {
					remainingDamage -= additionalHearts;
					player.setAdditionalHearts(0);
				} else {
					player.setAdditionalHearts(additionalHearts - remainingDamage);
					remainingDamage = 0;
				}
			}
		}
		
		float finalHealth = entity.getHealth() - remainingDamage;
		return finalHealth <= 0;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
