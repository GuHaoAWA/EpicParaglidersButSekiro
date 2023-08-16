package com.guhao.sekiro.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.HurtEvent;

@Mixin(value = HurtEvent.Post.class , remap = false)
public class MixinHurtEvent {
    @Inject(method = "<init>(Lyesman/epicfight/world/capabilities/entitypatch/player/ServerPlayerPatch;Lyesman/epicfight/api/utils/ExtendedDamageSource;F)V", at = @At("RETURN")) // 在构造函数末尾注入代码
    private void onHurt(ServerPlayerPatch playerpatch, ExtendedDamageSource damageSource, float amount, CallbackInfo ci) {
        assert playerpatch != null;
        if (playerpatch.getStamina() <= 0.11F) {
            damageSource.setStunType(ExtendedDamageSource.StunType.KNOCKDOWN);
        }
        if (playerpatch != null && amount > 0.0F) {
            float staminaReduce = amount * 0.5F;
            if (staminaReduce <= 2.5F){
                staminaReduce = 2.5F;
            } else if (staminaReduce >= 5.0F) {
                staminaReduce = 5.0F;
            } else {
                staminaReduce = staminaReduce;
            }
            float newStamina = playerpatch.getStamina() - staminaReduce;
            playerpatch.setStamina(newStamina);
        }
    }
}
