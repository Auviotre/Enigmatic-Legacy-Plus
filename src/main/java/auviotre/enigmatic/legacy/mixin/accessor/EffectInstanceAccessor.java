package auviotre.enigmatic.legacy.mixin.accessor;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.EffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
@Mixin(EffectInstance.class)
public interface EffectInstanceAccessor {
    @Accessor("uniformMap")
    Map<String, Uniform> getUniforms();
}