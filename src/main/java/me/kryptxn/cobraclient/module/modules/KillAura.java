package me.kryptxn.cobraclient.module.modules;

import me.kryptxn.cobraclient.CobraClient;
import me.kryptxn.cobraclient.event.EventTarget;
import me.kryptxn.cobraclient.event.events.EventUpdate;
import me.kryptxn.cobraclient.module.Category;
import me.kryptxn.cobraclient.module.Module;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;


public class KillAura extends Module {
    public KillAura() {
        super("Kill Aura", GLFW.GLFW_KEY_R, Category.COMBAT);
    }

    private LivingEntity target;

    @Override
    public void onEnable() {
        System.out.print(this.getName()+" ENABLED!");
        CobraClient.Instance.eventManager.register(this);
    }

    @Override
    public void onDisable() {
        CobraClient.Instance.eventManager.unregister(this);
        target = null;
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate)
    {
        Entity tempEntity = null;

        if(target == null || mc.player.distanceTo(target)>5) {
            for(Entity entity : mc.world.getEntities()) {
                if(entity.isAlive() && entity instanceof LivingEntity) {
                    if(!entity.equals(mc.player)) {
                        if(mc.player.distanceTo(entity) < 10) {
                            if(tempEntity==null || mc.player.distanceTo(entity) < mc.player.distanceTo(tempEntity)) {
                                tempEntity = entity;
                            }
                        }
                    }
                }
            }
            target = (LivingEntity) tempEntity;
        }


        if(target != null && !target.isDead()) {
            if(mc.player.getAttackCooldownProgress(0) == 1F && mc.player.distanceTo(target) < 5) {
                mc.interactionManager.attackEntity(mc.player,target);
                mc.player.swingHand(Hand.MAIN_HAND);
            } else {
                System.out.println(mc.player.getAttackCooldownProgress(0));
            }
        } else  {
            target = null;
        }
    }

    public Entity getTarget() {
        return target;
    }
}
