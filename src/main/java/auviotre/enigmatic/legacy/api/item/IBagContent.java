package auviotre.enigmatic.legacy.api.item;

import net.minecraft.network.chat.Component;

import java.util.List;

public interface IBagContent {
    default List<Component> getTooltipInBag(List<Component> list) {
        return list;
    }
}
