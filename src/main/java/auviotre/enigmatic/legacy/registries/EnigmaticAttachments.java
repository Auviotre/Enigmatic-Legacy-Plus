package auviotre.enigmatic.legacy.registries;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class EnigmaticAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, EnigmaticLegacy.MODID);

    public static final Supplier<AttachmentType<EnigmaticData>> ENIGMATIC_DATA = ATTACHMENT_TYPES.register("enigmatic_data", () -> AttachmentType.serializable(EnigmaticData::new).copyOnDeath().build());
}