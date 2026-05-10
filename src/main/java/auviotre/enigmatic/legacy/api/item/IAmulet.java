package auviotre.enigmatic.legacy.api.item;

import auviotre.enigmatic.legacy.contents.item.amulets.EnigmaticAmulet;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.concurrent.atomic.AtomicReference;

public interface IAmulet {

    static Color getAmulet(LivingEntity entity) {
        AtomicReference<Color> ret = new AtomicReference<>(Color.NULL);
        CuriosApi.getCuriosInventory(entity).ifPresent(handler -> {
            IItemHandlerModifiable curios = handler.getEquippedCurios();
            for (int id = 0; id < curios.getSlots(); id++) {
                ItemStack stackInSlot = curios.getStackInSlot(id);
                if (stackInSlot.isEmpty()) continue;
                if (stackInSlot.getItem() instanceof IAmulet amulet) {
                    ret.set(amulet.getColor());
                    break;
                }
            }
        });
        return ret.get();
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
