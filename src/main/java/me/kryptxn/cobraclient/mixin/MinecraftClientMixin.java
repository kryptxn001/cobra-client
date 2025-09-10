package me.kryptxn.cobraclient.mixin;

import me.kryptxn.cobraclient.CobraClient;
import me.kryptxn.cobraclient.CobraClientMod;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = {"updateWindowTitle"}, at = @At("RETURN"))
    public void updateWindowTitle(CallbackInfo callbackInfo) {
        MinecraftClient.getInstance().getWindow().setTitle(CobraClient.NAME + " v" + CobraClient.VERSION + " by " + CobraClient.AUTHOR);
    }

    @Inject(at = @At("HEAD"), method = "run")
    private void run(CallbackInfo info) {
        System.out.println("Cobra Client Hooked!");
        CobraClient.Instance.init();
    }
}
