package auviotre.enigmatic.legacy.contents.command;

import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CurseTimeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cursetime")
                .requires(source -> source.hasPermission(4))
                .then(Commands.literal("get")
                        .executes(context -> getTime(context.getSource(), context.getSource().getPlayerOrException(), false))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> getTime(context.getSource(), EntityArgument.getPlayer(context, "player"), false))
                                .then(Commands.argument("reverse", BoolArgumentType.bool())
                                        .executes(context -> getTime(context.getSource(), EntityArgument.getPlayer(context, "player"), BoolArgumentType.getBool(context, "reverse")))
                                )
                        )
                ).then(Commands.literal("set")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("ticks", LongArgumentType.longArg(0))
                                        .executes(context -> setTime(context.getSource(), EntityArgument.getPlayer(context, "player"), LongArgumentType.getLong(context, "ticks"), false))
                                        .then(Commands.argument("reverse", BoolArgumentType.bool())
                                                .executes(context -> setTime(context.getSource(), EntityArgument.getPlayer(context, "player"), LongArgumentType.getLong(context, "ticks"), BoolArgumentType.getBool(context, "reverse")))
                                        )
                                )
                        )
                )
        );
    }

    private static int getTime(CommandSourceStack source, ServerPlayer player, boolean reverse) {
        EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
        if (reverse) source.sendSuccess(() -> Component.translatable("message.enigmaticlegacy.command.getnocursetime",
                player.getDisplayName(),
                data.getTimeWithoutCurses(),
                EnigmaticHandler.getNoSufferingTime(player)
        ), true);
        else source.sendSuccess(() -> Component.translatable("message.enigmaticlegacy.command.getcursetime",
                player.getDisplayName(),
                data.getTimeWithCurses(),
                EnigmaticHandler.getSufferingTime(player)
        ), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int setTime(CommandSourceStack source, ServerPlayer player, long tick, boolean reverse) {
        EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
        if (reverse) {
            data.setTimeWithoutCurses(tick);
            source.sendSuccess(() -> Component.translatable("message.enigmaticlegacy.command.setnocursetime",
                    player.getDisplayName(),
                    data.getTimeWithoutCurses()
            ), true);
        } else {
            data.setTimeWithCurses(tick);
            source.sendSuccess(() -> Component.translatable("message.enigmaticlegacy.command.setcursetime",
                    player.getDisplayName(),
                    data.getTimeWithCurses()
            ), true);
        }
        return Command.SINGLE_SUCCESS;
    }
}
