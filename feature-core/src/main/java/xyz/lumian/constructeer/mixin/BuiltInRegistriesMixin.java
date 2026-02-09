package xyz.lumian.constructeer.mixin;


import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.lumian.constructeer.registry.CteerRegistryEvents;



//**********************************************************************************************************************
@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin
{
    //******************************************************************************************************************
    @Inject(
        method = "bootStrap",
        at     = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/registries/BuiltInRegistries;freeze()V"
        ))
    private static void bootStrap_before(final CallbackInfo ci)
    {
        CteerRegistryEvents.MC_REGISTRIES_FROZEN_BEFORE.invoker().run();
    }
    
    @Inject(
        method = "bootStrap",
        at     = @At(
            value  = "INVOKE",
            shift  = At.Shift.AFTER,
            target = "Lnet/minecraft/core/registries/BuiltInRegistries;freeze()V"
        ))
    private static void bootStrap_after(final CallbackInfo ci)
    {
        CteerRegistryEvents.MC_REGISTRIES_FROZEN_AFTER.invoker().run();
    }
}
