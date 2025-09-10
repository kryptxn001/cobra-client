package me.kryptxn.cobraclient.module.modules;


import com.mojang.blaze3d.systems.RenderSystem;
import me.kryptxn.cobraclient.CobraClient;
import me.kryptxn.cobraclient.event.EventTarget;

import me.kryptxn.cobraclient.event.events.EventRender3D;
import me.kryptxn.cobraclient.event.events.EventUpdate;
import me.kryptxn.cobraclient.module.Category;
import me.kryptxn.cobraclient.module.Module;

import me.kryptxn.cobraclient.util.EntityUtils;
import me.kryptxn.cobraclient.util.RegionPos;
import me.kryptxn.cobraclient.util.RenderUtils;
import me.kryptxn.cobraclient.util.RotationUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUsage;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class EntityESP extends Module {
    public EntityESP() {
        super("Test", GLFW.GLFW_KEY_V, Category.RENDER);
    }

    private final ArrayList<LivingEntity> mobs = new ArrayList<>();
    private VertexBuffer mobBox;

    @Override
    public void onEnable() {
        System.out.print(this.getName()+" ENABLED!");
        CobraClient.Instance.eventManager.register(this);


        mobBox = new VertexBuffer(GlUsage.STATIC_WRITE);
        Box bb = new Box(-0.5, 0, -0.5, 0.5, 1, 0.5);
        RenderUtils.drawOutlinedBox(bb, mobBox);
    }

    @Override
    public void onDisable() {
        CobraClient.Instance.eventManager.unregister(this);

        if(mobBox != null)
            mobBox.close();
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate)
    {

        mobs.clear();
        for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
            if (entity.isAlive() && entity instanceof LivingEntity) {
                if(!(entity instanceof PassiveEntity)) {
                    if(!entity.equals(MinecraftClient.getInstance().player)) {
                        mobs.add((LivingEntity) entity);
                    }
                }
            }
        }
    }

    @EventTarget
    public void onRender(EventRender3D eventRender3D) {
        MatrixStack matrixStack = eventRender3D.matrixStack;
        // GL settings
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        matrixStack.push();

        RegionPos region = RenderUtils.getCameraRegion();
        RenderUtils.applyRegionalRenderOffset(matrixStack, region);

        renderBoxes(matrixStack, eventRender3D.tickDelta, region);
        renderTracers(matrixStack, eventRender3D.tickDelta, region);

        matrixStack.pop();

        // GL resets
        RenderSystem.setShaderColor(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void renderBoxes(MatrixStack matrixStack, float partialTicks,
                             RegionPos region)
    {
        float extraSize = 0F;
        RenderSystem.setShader(ShaderProgramKeys.POSITION);

        for(LivingEntity e : mobs)
        {
            matrixStack.push();

            Vec3d lerpedPos = EntityUtils.getLerpedPos(e, partialTicks)
                    .subtract(region.toVec3d());
            matrixStack.translate(lerpedPos.x, lerpedPos.y, lerpedPos.z);

            matrixStack.scale(e.getWidth() + extraSize,
                    e.getHeight() + extraSize, e.getWidth() + extraSize);


            Entity target = null;
            KillAura module = (KillAura) CobraClient.Instance.moduleManager.getModule(KillAura.class);
            target = module.getTarget();

            if(target != null) {
                if(target.equals(e)) {
                    RenderSystem.setShaderColor(1F, 0F, 0F, 1F);
                } else {
                    RenderSystem.setShaderColor(0, 0F, 1F, 1F);
                }
            } else {
                RenderSystem.setShaderColor(0, 0F, 1F, 1F);
            }


            Matrix4f viewMatrix = matrixStack.peek().getPositionMatrix();
            Matrix4f projMatrix = RenderSystem.getProjectionMatrix();
            ShaderProgram shader = RenderSystem.getShader();
            mobBox.bind();
            mobBox.draw(viewMatrix, projMatrix, shader);
            VertexBuffer.unbind();

            matrixStack.pop();
        }
    }

    private void renderTracers(MatrixStack matrixStack, float partialTicks,
                               RegionPos region)
    {
        if(mobs.isEmpty())
            return;

        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        Matrix4f matrix = matrixStack.peek().getPositionMatrix();

        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.begin(
                VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        Vec3d regionVec = region.toVec3d();
        Vec3d start = RotationUtils.getClientLookVec(partialTicks)
                .add(RenderUtils.getCameraPos()).subtract(regionVec);

        for(LivingEntity e : mobs)
        {
            Vec3d end = EntityUtils.getLerpedBox(e, partialTicks).getCenter()
                    .subtract(regionVec);


            bufferBuilder
                    .vertex(matrix, (float)start.x, (float)start.y, (float)start.z)
                    .color(0F, 0F, 1F, 0.1F);

            bufferBuilder
                    .vertex(matrix, (float)end.x, (float)end.y, (float)end.z)
                    .color(0, 0, 1F, 0.1F);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }
}
