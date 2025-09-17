package auviotre.enigmatic.legacy;

import auviotre.enigmatic.legacy.contents.item.SoulCrystal;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ELConfig {
    public static final ModConfigSpec SPEC;
    public static final ELConfig CONFIG;

    static {
        final Pair<ELConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(ELConfig::new);
        SPEC = pair.getRight();
        CONFIG = pair.getLeft();
    }

    public final SevenCurses SEVEN_CURSES;
    public final Spellstones SPELLSTONES;
    public final CursedItems CURSED_ITEMS;
    public final Else ELSE;

    ELConfig(ModConfigSpec.Builder builder) {
        SEVEN_CURSES = new SevenCurses(builder);
        SPELLSTONES = new Spellstones(builder);
        CURSED_ITEMS = new CursedItems(builder);
        ELSE = new Else(builder);
    }

    public static class SevenCurses {
        public final ModConfigSpec.IntValue painMultiplier;
        public final ModConfigSpec.DoubleValue neutralAngerRange;
        public final ModConfigSpec.IntValue armorDebuff;
        public final ModConfigSpec.IntValue monsterDamageDebuff;
        public final ModConfigSpec.EnumValue<SoulCrystal.LossMode> soulCrystalsMode;
        public final ModConfigSpec.BooleanValue enableInsomnia;
        public final ModConfigSpec.IntValue lootingBonus;
        public final ModConfigSpec.IntValue fortuneBonus;
        public final ModConfigSpec.IntValue experienceBonus;
        public final ModConfigSpec.IntValue enchantingBonus;
        public final ModConfigSpec.BooleanValue enableSpecialDrops;
        public final ModConfigSpec.BooleanValue autoEquip;
        public final ModConfigSpec.BooleanValue ultraHardcore;
        ;
        public final ModConfigSpec.IntValue maxSoulCrystalLoss;

        SevenCurses(ModConfigSpec.Builder builder) {
            builder.push("sevenCurses");
            painMultiplier = builder.defineInRange("painMultiplier", 200, 100, 500);
            neutralAngerRange = builder.defineInRange("neutralAngerRange", 24.0, 4.0, 100.0);
            armorDebuff = builder.defineInRange("armorDebuff", 30, 0, 100);
            monsterDamageDebuff = builder.defineInRange("monsterDamageDebuff", 40, 0, 100);
            soulCrystalsMode = builder.defineEnum("soulCrystalsMode", SoulCrystal.LossMode.NEED_CURSE_RING);
            enableInsomnia = builder.define("enableInsomnia", true);
            lootingBonus = builder.defineInRange("lootingBonus", 1, 0, 10);
            fortuneBonus = builder.defineInRange("fortuneBonus", 1, 0, 10);
            experienceBonus = builder.defineInRange("experienceBonus", 300, 0, 1000);
            enchantingBonus = builder.defineInRange("enchantingBonus", 10, 0, 30);
            enableSpecialDrops = builder.define("enableSpecialDrops", true);
            autoEquip = builder.define("autoEquip", false);
            ultraHardcore = builder.define("ultraHardcore", false);
            maxSoulCrystalLoss = builder.defineInRange("maxSoulCrystalLoss", 9, 0, 10);
            builder.pop();
        }
    }

    public static class Spellstones {
        // GolemHeart
        public final ModConfigSpec.DoubleValue defaultArmorBonus;
        public final ModConfigSpec.DoubleValue superArmorBonus;
        public final ModConfigSpec.DoubleValue superArmorToughnessBonus;
        public final ModConfigSpec.DoubleValue knockbackResistance;
        public final ModConfigSpec.IntValue meleeResistance;
        public final ModConfigSpec.IntValue explosionResistance;
        public final ModConfigSpec.DoubleValue GHVulnerabilityModifier;
        // BlazingCore
        public final ModConfigSpec.DoubleValue damageFeedback;
        public final ModConfigSpec.IntValue ignitionFeedback;
        public final ModConfigSpec.DoubleValue BCVulnerabilityModifier;
        // OceanStone
        public final ModConfigSpec.IntValue underwaterCreaturesResistance;
        public final ModConfigSpec.DoubleValue xpCostModifier;
        public final ModConfigSpec.BooleanValue preventOxygenBarRender;
        public final ModConfigSpec.DoubleValue OSVulnerabilityModifier;
        // AngelBlessing
        public final ModConfigSpec.IntValue deflectChance;
        public final ModConfigSpec.DoubleValue ABVulnerabilityModifier;
        // EyeOfNebula
        public final ModConfigSpec.IntValue magicBoost;
        public final ModConfigSpec.IntValue magicResistance;
        ;
        public final ModConfigSpec.IntValue dodgeProbability;
        public final ModConfigSpec.IntValue attackEmpower;
        public final ModConfigSpec.DoubleValue EONVulnerabilityModifier;
        // VoidPearl
        public final ModConfigSpec.DoubleValue shadowRange;
        public final ModConfigSpec.IntValue darknessDamage;
        public final ModConfigSpec.IntValue witheringLevel;
        public final ModConfigSpec.DoubleValue witheringTime;
        public final ModConfigSpec.IntValue undeadProbability;

        Spellstones(ModConfigSpec.Builder builder) {
            builder.push("spellstone");

            builder.push("golemHeart");
            defaultArmorBonus = builder.defineInRange("defaultArmorBonus", 4.0, 0, 20);
            superArmorBonus = builder.defineInRange("superArmorBonus", 16.0, 0, 100);
            superArmorToughnessBonus = builder.defineInRange("superArmorToughnessBonus", 4.0, 0.0, 20.0);
            knockbackResistance = builder.defineInRange("knockbackResistance", 1.0, 0.0, 1.0);
            meleeResistance = builder.defineInRange("meleeResistance", 25, 0, 100);
            explosionResistance = builder.defineInRange("explosionResistance", 40, 0, 100);
            GHVulnerabilityModifier = builder.defineInRange("vulnerabilityModifier", 2.0, 1.0, 20.0);
            builder.pop();
            builder.push("blazingCore");
            damageFeedback = builder.defineInRange("damageFeedback", 4.0, 0.0, 64.0);
            ignitionFeedback = builder.defineInRange("ignitionFeedback", 4, 0, 32);
            BCVulnerabilityModifier = builder.defineInRange("vulnerabilityModifier", 2.0, 1.0, 20.0);
            builder.pop();

            builder.push("oceanStone");
            underwaterCreaturesResistance = builder.defineInRange("underwaterCreaturesResistance", 40, 0, 100);
            xpCostModifier = builder.defineInRange("xpCostModifier", 1.0, 0, 10.0);
            preventOxygenBarRender = builder.define("preventOxygenBarRender", true);
            OSVulnerabilityModifier = builder.defineInRange("vulnerabilityModifier", 2.0, 1.0, 20.0);
            builder.pop();

            builder.push("angelBlessing");
            deflectChance = builder.defineInRange("deflectChance", 40, 0, 100);
            ABVulnerabilityModifier = builder.defineInRange("vulnerabilityModifier", 2.0, 1.0, 20.0);
            builder.pop();

            builder.push("eyeOfNebula");
            magicBoost = builder.defineInRange("magicBoost", 40, 0, 100);
            magicResistance = builder.defineInRange("magicResistance", 65, 0, 100);
            dodgeProbability = builder.defineInRange("dodgeProbability", 15, 0, 100);
            attackEmpower = builder.defineInRange("attackEmpower", 150, 0, 1000);
            EONVulnerabilityModifier = builder.defineInRange("vulnerabilityModifier", 2.0, 1.0, 20.0);
            builder.pop();

            builder.push("voidPearl");
            shadowRange = builder.defineInRange("shadowRange", 16.0, 0.0, 128.0);
            darknessDamage = builder.defineInRange("darknessDamage", 4, 0, 100);
            witheringLevel = builder.defineInRange("witheringLevel", 2, 0, 10);
            witheringTime = builder.defineInRange("witheringTime", 5.0, 0, 120.0);
            undeadProbability = builder.defineInRange("undeadProbability", 30, 0, 80);
            builder.pop();

            builder.pop();
        }
    }

    public static class CursedItems {
        // TheTwist
        public final ModConfigSpec.IntValue specialDamageBoost;
        public final ModConfigSpec.IntValue knockbackModifier;
        // BerserkEmblem
        public final ModConfigSpec.DoubleValue BEAttackDamage;
        public final ModConfigSpec.DoubleValue BEAttackSpeed;
        public final ModConfigSpec.DoubleValue BEMovementSpeed;
        public final ModConfigSpec.DoubleValue BEDamageResistance;

        CursedItems(ModConfigSpec.Builder builder) {
            builder.push("cursedItems");
            builder.push("TheTwist");
            specialDamageBoost = builder.defineInRange("specialDamageBoost", 300, 0, 1000);
            knockbackModifier = builder.defineInRange("knockbackModifier", 300, 0, 1000);
            builder.pop();
            builder.push("BerserkEmblem");
            BEAttackDamage = builder.defineInRange("attackDamage", 1.0, 0, 10.0);
            BEAttackSpeed = builder.defineInRange("attackSpeed", 1.0, 0, 10.0);
            BEMovementSpeed = builder.defineInRange("movementSpeed", 0.5, 0, 10.0);
            BEDamageResistance = builder.defineInRange("damageResistance", 0.5, 0, 1.0);
            builder.pop();
            builder.pop();
        }
    }

    public static class Else {
        public final ModConfigSpec.DoubleValue magnetRingRange;
        public final ModConfigSpec.IntValue magnetButtonOffsetX;
        public final ModConfigSpec.IntValue magnetButtonOffsetY;
        public final ModConfigSpec.IntValue magnetButtonOffsetXCreative;
        public final ModConfigSpec.IntValue magnetButtonOffsetYCreative;
        public final ModConfigSpec.IntValue enderButtonOffsetX;
        public final ModConfigSpec.IntValue enderButtonOffsetY;
        public final ModConfigSpec.IntValue enderButtonOffsetXCreative;
        public final ModConfigSpec.IntValue enderButtonOffsetYCreative;

        Else(ModConfigSpec.Builder builder) {
            builder.push("else");
            magnetRingRange = builder.defineInRange("magnetRingRange", 8.0, 1.0, 256.0);
            builder.push("magnetButton");
            magnetButtonOffsetX = builder.defineInRange("OffsetX", 0, -1024, 1024);
            magnetButtonOffsetY = builder.defineInRange("OffsetY", 0, -1024, 1024);
            magnetButtonOffsetXCreative = builder.defineInRange("OffsetXCreative", 0, -1024, 1024);
            magnetButtonOffsetYCreative = builder.defineInRange("OffsetYCreative", 0, -1024, 1024);
            builder.pop();
            builder.push("enderButton");
            enderButtonOffsetX = builder.defineInRange("OffsetX", 0, -1024, 1024);
            enderButtonOffsetY = builder.defineInRange("OffsetY", 0, -1024, 1024);
            enderButtonOffsetXCreative = builder.defineInRange("OffsetXCreative", 0, -1024, 1024);
            enderButtonOffsetYCreative = builder.defineInRange("OffsetYCreative", 0, -1024, 1024);
            builder.pop();
            builder.pop();
        }
    }
}