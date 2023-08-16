/*
package com.guhao.sekiro.mixin;

import nameless.yamatomoveset.gameasset.YamatoAnimations;
import nameless.yamatomoveset.gameasset.YamatoSounds;
import nameless.yamatomoveset.skill.YamatoArt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.capabilities.PlayerMovement;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import static com.jvn.epicaddon.skills.SAO.DualBladeSkill.EVENT_UUID;
import static yesman.epicfight.skill.Skill.setConsumptionSynchronize;

@Mixin(value = YamatoArt.class , remap = false)
public class MixinYamatoArt  {

    @Final
    @Shadow private static SkillDataManager.SkillDataKey<Boolean> JUST_GUARD;

    @Final
    @Shadow private static SkillDataManager.SkillDataKey<Float> SLASH_COUNTER;

    @Shadow private void stackCost(ServerPlayerPatch player, int cost) {
    }
    private SkillCategory category;
    private PlayerMovement playerMovement;
    public void setConsumptionSynchronizeWithCategory(ServerPlayerPatch executer, float amount) {
        setConsumptionSynchronize(executer, category,amount);
    }

    @Inject(method = "onInitiate" , at = @At("RETURN"))
    private void modifyOnInitiate(SkillContainer container, CallbackInfo ci) {
        category = container.getSkill().getCategory();
        container.getDataManager().registerData(JUST_GUARD);
        container.getDataManager().registerData(SLASH_COUNTER);
        PlayerEventListener listener = container.getExecuter().getEventListener();
        listener.addEventListener(PlayerEventListener.EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {

            ServerPlayerPatch executer = (ServerPlayerPatch)event.getPlayerPatch();
            playerMovement = getPlayerMovement(executer);
            float newstamina = playerMovement.getStamina();
            float newmaxstamina = playerMovement.getMaxStamina();
            int id = event.getAnimationId();
            if (id == YamatoAnimations.YAMATO_P0.getId()) {
                if ((Boolean)container.getDataManager().getDataValue(JUST_GUARD) && (double)newstamina >= 0.2 * (double)newmaxstamina) {
                    ((ServerPlayerPatch)event.getPlayerPatch()).playSound(YamatoSounds.FORESIGHT, 0.5F, 0.0F, 0.1F);
                    ((ServerPlayerPatch)event.getPlayerPatch()).reserveAnimation(YamatoAnimations.YAMATO_P0_2);
                    playerMovement.setStamina((int)(newstamina - 0.2F * newmaxstamina));
                    container.getDataManager().setData(JUST_GUARD, false);
                    this.stackCost(executer, -2);
                } else {
                    ((ServerPlayerPatch)event.getPlayerPatch()).reserveAnimation(YamatoAnimations.YAMATO_P0_1);
                }
            } else if (id != YamatoAnimations.YAMATO_P3.getId() && id != YamatoAnimations.YAMATO_P3_REPEAT.getId()) {
                if (id == YamatoAnimations.YAMATO_COUNTER_1.getId()) {
                    ((ServerPlayerPatch)event.getPlayerPatch()).reserveAnimation(YamatoAnimations.YAMATO_COUNTER_2);
                } else if (id == YamatoAnimations.YAMATO_P3_FINISH.getId() || id == YamatoAnimations.YAMATO_POWER_DASH.getId()) {
                    container.getDataManager().setData(SLASH_COUNTER, 0.0F);
                }
            } else {
                ((ServerPlayerPatch)event.getPlayerPatch()).reserveAnimation(YamatoAnimations.YAMATO_P3_FINISH);
            }
        });
        listener.addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
            ServerPlayerPatch executer = (ServerPlayerPatch)event.getPlayerPatch();
            DamageSource damageSource = (DamageSource)event.getDamageSource();
            DynamicAnimation animation = executer.getAnimator().getPlayerFor((DynamicAnimation)null).getAnimation();
            if (animation == YamatoAnimations.YAMATO_P0 && executer.getEntityState().invulnerableTo(damageSource) && !(Boolean)container.getDataManager().getDataValue(JUST_GUARD)) {
                container.getDataManager().setData(JUST_GUARD, true);
            }

        });
        listener.addEventListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
            StaticAnimation animation = event.getAnimation();
            if (animation != YamatoAnimations.YAMATO_P3 && animation != YamatoAnimations.YAMATO_P3_REPEAT && animation != YamatoAnimations.YAMATO_P3_FINISH && animation != YamatoAnimations.YAMATO_POWER_DASH && container.getDataManager().getDataValue(SLASH_COUNTER) > 0.0F) {
                container.getDataManager().setData(SLASH_COUNTER, 0.0F);
            }

        });
        listener.addEventListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
            ServerPlayerPatch executer = (ServerPlayerPatch)event.getPlayerPatch();
            int id = event.getDamageSource().getAnimationId();
            playerMovement = getPlayerMovement(executer);
            float newstamina = playerMovement.getStamina();
            float newmaxstamina = playerMovement.getMaxStamina();
            if (id == YamatoAnimations.YAMATO_P1.getId()) {
                if (!container.isFull() && container.hasSkill(executer.getHoldingItemCapability(InteractionHand.MAIN_HAND).getSpecialAttack(executer))) {
                    float value = container.getResource() + 0.5F * event.getAttackDamage();
                    if (value > 0.0F) {
                        this.setConsumptionSynchronizeWithCategory(executer, value);
                    }

                    if (newstamina < newmaxstamina) {
                        executer.setStamina(newmaxstamina + 0.3F * newmaxstamina);
                    }
                }
            } else if (id == YamatoAnimations.YAMATO_COUNTER_2.getId()) {
                if (newstamina < newmaxstamina) {
                    executer.setStamina(newstamina + 0.2F * newmaxstamina);
                }
            } else if (id != YamatoAnimations.YAMATO_P3.getId() && id != YamatoAnimations.YAMATO_P3_REPEAT.getId()) {
                if (id == YamatoAnimations.YAMATO_COUNTER_1.getId()) {
                    this.stackCost(executer, -1);
                }
            } else {
                container.getDataManager().setData(SLASH_COUNTER, (Float)container.getDataManager().getDataValue(SLASH_COUNTER) + 1.0F);
            }

        });
        listener.addEventListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID, (event) -> {
            if (event.getDamageSource() != null) {
                float attackDamage = event.getAttackDamage();
                int id = event.getDamageSource().getAnimationId();
                float k = (Float)container.getDataManager().getDataValue(SLASH_COUNTER);
                if (id == YamatoAnimations.YAMATO_P3_FINISH.getId() || id == YamatoAnimations.YAMATO_POWER_DASH.getId()) {
                    event.setAttackDamage(attackDamage * (1.0F + k / 3.0F));
                }
            }

        });
    }
    private PlayerMovement getPlayerMovement(ServerPlayerPatch executer) {
        return playerMovement;
    }
}
*/