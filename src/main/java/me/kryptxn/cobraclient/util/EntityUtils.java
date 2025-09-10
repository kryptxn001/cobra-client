package me.kryptxn.cobraclient.util;

import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import me.kryptxn.cobraclient.CobraClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public enum EntityUtils
{
    ;

    protected static final CobraClient COBRA_CLIENT = CobraClient.Instance;
    protected static final MinecraftClient MC = MinecraftClient.getInstance();

    public static Stream<Entity> getAttackableEntities()
    {
        return StreamSupport.stream(MC.world.getEntities().spliterator(), true)
                .filter(IS_ATTACKABLE);
    }

    public static final Predicate<Entity> IS_ATTACKABLE = e -> e != null
            && !e.isRemoved()
            && (e instanceof LivingEntity && ((LivingEntity)e).getHealth() > 0
            || e instanceof EndCrystalEntity
            || e instanceof ShulkerBulletEntity);

    public static Stream<AnimalEntity> getValidAnimals()
    {
        return StreamSupport.stream(MC.world.getEntities().spliterator(), true)
                .filter(AnimalEntity.class::isInstance).map(e -> (AnimalEntity)e)
                .filter(IS_VALID_ANIMAL);
    }

    public static final Predicate<AnimalEntity> IS_VALID_ANIMAL =
            a -> a != null && !a.isRemoved() && a.getHealth() > 0;

    /**
     * Interpolates (or "lerps") between the entity's position in the previous
     * tick and its position in the current tick to get the exact position where
     * the entity will be rendered in the next frame.
     *
     * <p>
     * This interpolation is important for smooth animations. Using the entity's
     * current tick position directly would cause animations to look choppy
     * because that position is only updated 20 times per second.
     */
    public static Vec3d getLerpedPos(Entity e, float partialTicks)
    {
        // When an entity is removed, it stops moving and its lastRenderX/Y/Z
        // values are no longer updated.
        if(e.isRemoved())
            return e.getPos();

        double x = MathHelper.lerp(partialTicks, e.lastRenderX, e.getX());
        double y = MathHelper.lerp(partialTicks, e.lastRenderY, e.getY());
        double z = MathHelper.lerp(partialTicks, e.lastRenderZ, e.getZ());
        return new Vec3d(x, y, z);
    }

    public static Box getLerpedBox(Entity e, float partialTicks)
    {
        // When an entity is removed, it stops moving and its lastRenderX/Y/Z
        // values are no longer updated.
        if(e.isRemoved())
            return e.getBoundingBox();

        Vec3d offset = getLerpedPos(e, partialTicks).subtract(e.getPos());
        return e.getBoundingBox().offset(offset);
    }
}