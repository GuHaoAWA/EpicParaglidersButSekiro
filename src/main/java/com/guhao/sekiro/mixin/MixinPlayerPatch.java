package com.guhao.sekiro.mixin;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

@Mixin(value = PlayerPatch.class, remap = false)

public abstract class MixinPlayerPatch<T extends Player> extends LivingEntityPatch<T> {

    @Inject(method = "initAttributes", at = @At("HEAD"), remap = false)
    public void modifyInitAttributes(CallbackInfo ci) {
        ((Player)this.original).getAttribute((Attribute)EpicFightAttributes.MAX_STAMINA.get()).setBaseValue(9.0);
        }
    }


