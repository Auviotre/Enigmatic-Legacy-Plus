package auviotre.enigmatic.legacy.api.item;

import auviotre.enigmatic.legacy.contents.item.amulets.EnigmaticAmulet;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public interface IAmulet {

    static Color getAmulet(LivingEntity entity) {
        Optional<ICuriosItemHandler> curios = CuriosApi.getCuriosInventory(entity);
        if (curios.isPresent()) {
            Optional<SlotResult> firstCurio = curios.get().findFirstCurio(stack -> !stack.isEmpty() && stack.getItem() instanceof IAmulet, "enigmaticlegacy:amulet");
            if (firstCurio.isPresent()) return ((IAmulet) firstCurio.get().stack().getItem()).getColor();
        }
        return Color.NULL;
    }

    static Color getAmulet(ItemStack stack) {
        if (!(stack.getItem() instanceof IAmulet amulet)) return Color.NULL;
        return amulet.getColor();
    }

    static boolean hasAmulet(LivingEntity entity, Color color) {
        Color amulet = getAmulet(entity);
        return amulet == Color.ALL || amulet == color;
    }

    static String getAttributeVar(Color color) {
        return switch (color) {
            case RED -> String.format("%.1f", EnigmaticAmulet.attackDamage.get());
            case AQUA -> EnigmaticAmulet.sprintingSpeed.get() + "%";
            case VIOLET -> EnigmaticAmulet.projectileDeflect.get() + "%";
            case MAGENTA -> EnigmaticAmulet.gravity.get() + "%";
            case GREEN -> String.format("%.1f", EnigmaticAmulet.miningEfficiency.get());
            case BLACK -> EnigmaticAmulet.lifesteal.get() + "%";
            case BLUE -> EnigmaticAmulet.swimSpeed.get() + "%";
            default -> "0";
        };
    }

    Color getColor();

    enum Color implements StringRepresentable {
        ALL("all"),
        NULL("null"),
        RED("red"),
        AQUA("aqua"),
        VIOLET("violet"),
        MAGENTA("magenta"),
        GREEN("green"),
        BLACK("black"),
        BLUE("blue");

        private final String id;

        Color(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public String getSerializedName() {
            return getId();
        }
    }
}
