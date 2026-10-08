package com.example.orangeclient.module.render;

import com.example.orangeclient.friend.Friends;
import com.example.orangeclient.module.Category;
import com.example.orangeclient.module.Module;
import com.example.orangeclient.setting.BoolSetting;
import com.example.orangeclient.setting.ColorSetting;
import com.example.orangeclient.setting.NumberSetting;
import com.example.orangeclient.util.RenderUtil;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class ESP extends Module {
    private final BoolSetting players = add(new BoolSetting("Players", true));
    private final BoolSetting mobs = add(new BoolSetting("Mobs", false));
    private final NumberSetting range = add(new NumberSetting("Range", 64, 8, 128, 4));
    private final ColorSetting color = add(new ColorSetting("Color", 0xFF5555, true));
    private final ColorSetting friendColor = add(new ColorSetting("Friend Color", 0x55FFFF, true));

    public ESP() {
        super("ESP", Category.RENDER);
    }

    @Override
    public void onWorldRender(WorldRenderContext ctx, MinecraftClient mc) {
        MatrixStack ms = ctx.matrixStack();
        if (ms == null) return;
        Matrix4f m = ms.peek().getPositionMatrix();
        Vec3d cam = ctx.camera().getPos();
        float td = ctx.tickCounter().getTickDelta(false);

        BufferBuilder b = RenderUtil.begin();
        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player || !e.isAlive()) continue;
            if (mc.player.distanceTo(e) > range.get()) continue;

            int c;
            if (Friends.isFriend(e)) c = friendColor.argb();
            else if (players.get() && e instanceof PlayerEntity) c = color.argb();
            else if (mobs.get() && e instanceof MobEntity) c = color.argb();
            else continue;

            Vec3d lerped = e.getLerpedPos(td);
            Box box = e.getBoundingBox().offset(lerped.subtract(e.getPos()));
            RenderUtil.box(b, m, cam, box, c);
        }
        RenderUtil.end(b);
    }
}
