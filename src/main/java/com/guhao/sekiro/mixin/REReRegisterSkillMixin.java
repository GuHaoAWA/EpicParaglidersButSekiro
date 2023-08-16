package com.guhao.sekiro.mixin;

import com.google.common.collect.Maps;
import net.cravencraft.epicparagliders.capabilities.UpdatedServerPlayerMovement;
import net.cravencraft.epicparagliders.skills.ReRegisterSkills;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.skill.ActiveGuardSkill;
import yesman.epicfight.skill.EnergizingGuardSkill;
import yesman.epicfight.skill.GuardSkill;
import yesman.epicfight.skill.Skill;

import java.util.Map;



@Mixin(value = ReRegisterSkills.class, remap = false)
public abstract class REReRegisterSkillMixin {

    @Shadow
    private static Map<ResourceLocation, Skill> SKILLS = Maps.newHashMap();

    @Shadow
    private static void registerIfAbsent(Map<ResourceLocation, Skill> map, Skill skill) {
    }

    @Shadow
    public static Skill GUARD;
    @Shadow
    public static Skill ACTIVE_GUARD;
    @Shadow
    public static Skill ENERGIZING_GUARD;

    @Inject(method = "setNewSkills", at = @At("TAIL"))
    private static void injectModifySkills(UpdatedServerPlayerMovement serverPlayerMovement, CallbackInfo ci) {
        modifySkills(serverPlayerMovement, ci);
    }
    private static void modifySkills(UpdatedServerPlayerMovement serverPlayerMovement, CallbackInfo ci) {
        GUARD = registerSkill(new GuardSkill(GuardSkill.createBuilder(new ResourceLocation("epicfight", "guard")).setRequiredXp(5)));
        ACTIVE_GUARD = registerSkill(new ActiveGuardSkill(ActiveGuardSkill.createBuilder(new ResourceLocation("epicfight", "active_guard")).setRequiredXp(8)));
        ENERGIZING_GUARD = registerSkill(new EnergizingGuardSkill(EnergizingGuardSkill.createBuilder(new ResourceLocation("epicfight", "energizing_guard")).setRequiredXp(8)));
    }

    private static Skill registerSkill(Skill skill) {
        registerIfAbsent(SKILLS, skill);
        return skill;
    }
}
