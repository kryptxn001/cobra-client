package me.kryptxn.cobraclient.module.modules;

import com.mojang.blaze3d.systems.RenderSystem;
import me.kryptxn.cobraclient.CobraClient;
import me.kryptxn.cobraclient.event.EventTarget;
import me.kryptxn.cobraclient.event.events.EventRender3D;
import me.kryptxn.cobraclient.event.events.EventUpdate;
import me.kryptxn.cobraclient.module.Category;
import me.kryptxn.cobraclient.module.Module;
import me.kryptxn.cobraclient.util.ChunkUtils;
import me.kryptxn.cobraclient.util.RegionPos;
import me.kryptxn.cobraclient.util.RenderUtils;
import me.kryptxn.cobraclient.util.RotationUtils;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUsage;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;


public class ChestESP extends Module {
    public ChestESP() {
        super("ChestESP", GLFW.GLFW_KEY_C, Category.RENDER);
    }

    ArrayList<BlockEntity> blockEntities = new ArrayList<>();
    private static VertexBuffer solidBox;
    private static VertexBuffer outlinedBox;

    private RegionPos region;
    private Vec3d start;

    @Override
    public void onEnable() {
        System.out.print(this.getName()+" ENABLED!");
        CobraClient.Instance.eventManager.register(this);
        prepareBuffers();

    }

    @Override
    public void onDisable() {
        CobraClient.Instance.eventManager.unregister(this);
        closeBuffers();
        blockEntities.clear();

    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate)
    {
        blockEntities.clear();
        ChunkUtils.getLoadedBlockEntities().forEach(blockEntity -> {
            if(blockEntity instanceof ChestBlockEntity) {
                blockEntities.add(blockEntity);
            }
        });

    }

    @EventTarget
    public void onRender(EventRender3D eventRender3D) {
        MatrixStack matrixStack = eventRender3D.matrixStack;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        matrixStack.push();
        RenderUtils.applyRegionalRenderOffset(matrixStack);

        //RENDERER SHIT
        region = RenderUtils.getCameraRegion();
        start = RotationUtils.getClientLookVec(eventRender3D.tickDelta)
                .add(RenderUtils.getCameraPos()).subtract(region.toVec3d());

        RenderSystem.setShader(ShaderProgramKeys.POSITION);
        renderBoxes(blockEntities,matrixStack);

        matrixStack.pop();

        // GL resets
        RenderSystem.setShaderColor(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    public void renderBoxes(ArrayList<BlockEntity> blockEntities, MatrixStack matrixStack)
    {
        for(BlockEntity blockEntity : blockEntities) {
            int x = blockEntity.getPos().getX();
            int y = blockEntity.getPos().getY();
            int z = blockEntity.getPos().getZ();

            Box box = new Box(x+0.0625, y, z+0.0625, x + (1-0.0625), y + (1-0.0625*2), z + (1-0.0625));

            matrixStack.push();

            matrixStack.translate(box.minX - region.x(), box.minY,
                    box.minZ - region.z());

            matrixStack.scale((float) (box.maxX - box.minX),
                    (float) (box.maxY - box.minY), (float) (box.maxZ - box.minZ));

            Matrix4f viewMatrix = matrixStack.peek().getPositionMatrix();
            Matrix4f projMatrix = RenderSystem.getProjectionMatrix();
            ShaderProgram shader = RenderSystem.getShader();

            RenderSystem.setShaderColor(1F, 1F, 0F, 0.1F);
            solidBox.bind();
            solidBox.draw(viewMatrix, projMatrix, shader);
            VertexBuffer.unbind();

            RenderSystem.setShaderColor(1F, 1F, 0F, 0.5F);
            outlinedBox.bind();
            outlinedBox.draw(viewMatrix, projMatrix, shader);
            VertexBuffer.unbind();

            matrixStack.pop();
        }
    }

    public static void prepareBuffers()
    {
        closeBuffers();
        solidBox = new VertexBuffer(GlUsage.STATIC_WRITE);
        outlinedBox = new VertexBuffer(GlUsage.STATIC_WRITE);

        Box box = new Box(BlockPos.ORIGIN);
        RenderUtils.drawSolidBox(box, solidBox);
        RenderUtils.drawOutlinedBox(box, outlinedBox);
    }

    public static void closeBuffers()
    {
        Stream.of(solidBox, outlinedBox).filter(Objects::nonNull)
                .forEach(VertexBuffer::close);
        solidBox = null;
        outlinedBox = null;
    }
}
