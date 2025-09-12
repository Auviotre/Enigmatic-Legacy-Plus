package auviotre.enigmatic.legacy.contents.command;

import auviotre.enigmatic.legacy.contents.attachement.EnigmaticData;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SetCurseTimeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setcursetime")
                .requires(source -> source.hasPermission(4))
                .then(Commands.argument("ticks", LongArgumentType.longArg(0))
                        .executes(context -> setTime(context.getSource(), context.getSource().getPlayerOrException(), LongArgumentType.getLong(context, "ticks")))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> setTime(context.getSource(), EntityArgument.getPlayer(context, "player"), LongArgumentType.getLong(context, "ticks")))
                        )
                )
        );
        dispatcher.register(Commands.literal("setnocursetime")
                .requires(source -> source.hasPermission(4))
                .then(Commands.argument("ticks", LongArgumentType.longArg(0))
                        .executes(context -> setNotTime(context.getSource(), context.getSource().getPlayerOrException(), LongArgumentType.getLong(context, "ticks")))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> setNotTime(context.getSource(), EntityArgument.getPlayer(context, "player"), LongArgumentType.getLong(context, "ticks")))
                        )
                )
        );
    }

    private static int setTime(CommandSourceStack source, ServerPlayer player, long tick) {
        EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
        data.setTimeWithCurses(tick);
        source.sendSuccess(() -> Component.translatable("message.enigmaticlegacy.command.setcursetime",
                player.getDisplayName(),
                data.getTimeWithCurses()
        ), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int setNotTime(CommandSourceStack source, ServerPlayer player, long tick) {
        EnigmaticData data = player.getData(EnigmaticAttachments.ENIGMATIC_DATA);
        data.setTimeWithoutCurses(tick);
        source.sendSuccess(() -> Component.translatable("message.enigmaticlegacy.command.setcursetime",
                player.getDisplayName(),
                data.getTimeWithoutCurses()
        ), true);
        return Command.SINGLE_SUCCESS;
    }

}
