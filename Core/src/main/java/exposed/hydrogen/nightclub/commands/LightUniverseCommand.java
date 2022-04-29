package exposed.hydrogen.nightclub.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import exposed.hydrogen.nightclub.Nightclub;
import exposed.hydrogen.nightclub.light.Light;
import exposed.hydrogen.nightclub.light.LightUniverse;
import exposed.hydrogen.nightclub.light.LightUniverseManager;
import exposed.hydrogen.nightclub.light.event.LightChannel;
import exposed.hydrogen.nightclub.util.Util;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


@CommandAlias("lightuniverse|lu")
@CommandPermission("nightclub.lightuniverse")
public class LightUniverseCommand extends BaseCommand {
    private static final LightUniverseManager manager = Nightclub.getLightUniverseManager();
    private static final boolean debugMode = false;

    static List<CommandError> isUnloaded() {
        List<CommandError> errors = new ArrayList<>();
        errors.add(Nightclub.getLightUniverseManager().getLoadedUniverse() == null ? CommandError.LIGHTUNIVERSE_UNLOADED : CommandError.VALID);
        errors.add(BeatmapCommand.getPlayer() != null && BeatmapCommand.getPlayer().isPlaying() ? CommandError.BEATMAP_PLAYING : CommandError.VALID);
        return errors;
    }

    static List<CommandError> isUnloaded(String[] args, int minArgsLength) {
        List<CommandError> errors = isUnloaded();
        errors.add(args.length < minArgsLength ? CommandError.TOO_LITTLE_ARGUMENTS : CommandError.VALID);
        return errors;
    }

    @Subcommand("build")
    @Description("Build a new LightUniverse")
    @CommandPermission("nightclub.lightuniverse")
    public static void onBuild(CommandIssuer sender, String[] args) {
        if (isUnloaded().stream().anyMatch(error -> error != CommandError.VALID && error != CommandError.LIGHTUNIVERSE_UNLOADED)) {
            sender.sendMessage(Util.formatErrors(isUnloaded()));
            return;
        }
        LightUniverse universe = new LightUniverse();
        if (manager.getLoadedUniverse() != null) {
            manager.getLoadedUniverse().unload();
        }
        manager.add(universe);
        manager.setLoadedUniverse(universe);
    }

    @Subcommand("load")
    @Description("Load a LightUniverse from provided argument")
    @CommandCompletion("@universes")
    @CommandPermission("nightclub.lightuniverse")
    public static void onLoad(CommandIssuer sender, String[] args) {
        if (isUnloaded(args, 1).stream().anyMatch(error -> error != CommandError.VALID && error != CommandError.LIGHTUNIVERSE_UNLOADED)) {
            sender.sendMessage(Util.formatErrors(isUnloaded()));
            return;
        }
        LightUniverse lightUniverse = manager.getLoadedUniverse();

        if (lightUniverse != null && lightUniverse.isLoaded()) {
            lightUniverse.unload();
        }

        AtomicReference<LightUniverse> lightUniverseAtomic = new AtomicReference<>();
        Nightclub.getLightUniverseManager().getUniverses().forEach((universe -> {
            if (args[0].equals(universe.getName())) {
                lightUniverseAtomic.set(universe);
            }
        }));
        lightUniverse = lightUniverseAtomic.get();

        lightUniverse.load();
        manager.setLoadedUniverse(lightUniverse);
    }

    @Subcommand("unload")
    @Description("Unload currently loaded LightUniverse")
    @CommandPermission("nightclub.lightuniverse")
    public static void onUnload() {
        if (BeatmapCommand.getPlayer() != null && BeatmapCommand.getPlayer().isPlaying()) return;
        LightUniverse lightUniverse = manager.getLoadedUniverse();
        if (lightUniverse != null && lightUniverse.isLoaded()) {
            lightUniverse.unload();
            manager.setLoadedUniverse(null);
        }
    }

    @Subcommand("setname")
    @Description("Set the currently loaded LightUniverses name")
    @CommandPermission("nightclub.lightuniverse")
    public static void onSetName(CommandIssuer sender, String[] args) {
        List<CommandError> errors = isUnloaded(args, 1);
        errors.add(Nightclub.getLightUniverseManager().getUniverses().stream().anyMatch(universe -> Objects.equals(universe.getName(), args[0]))
                ? CommandError.NAME_ALREADY_EXISTS : CommandError.VALID);
        if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
            sender.sendMessage(Util.formatErrors(errors));
            return;
        }
        manager.getLoadedUniverse().setName(args[0]);
    }

    @Subcommand("getid")
    @Description("Get ID of loaded LightUniverse")
    @CommandPermission("nightclub.lightuniverse")
    public static void onGetID(CommandIssuer sender) {
        if (isUnloaded().stream().anyMatch(error -> error != CommandError.VALID)) {
            sender.sendMessage(Util.formatErrors(isUnloaded()));
            return;
        }
        sender.sendMessage("Currently loaded ID: " + manager.getLoadedUniverse().getId());
    }

    @Subcommand("getname")
    @Description("Get name of loaded LightUniverse")
    @CommandPermission("nightclub.lightuniverse")
    public static void onGetName(CommandIssuer sender) {
        if (isUnloaded().stream().anyMatch(error -> error != CommandError.VALID)) {
            sender.sendMessage(Util.formatErrors(isUnloaded()));
            return;
        }
        sender.sendMessage("Currently loaded ID: " + manager.getLoadedUniverse().getName());
    }

    @Subcommand("debug")
    @Description("Debug channel")
    @CommandPermission("nightclub.lightuniverse")
    public static void onToggleDebug(CommandIssuer sender) {
        List<CommandError> errors = isUnloaded();
        //toggle
        Arrays.stream(LightChannel.values()).forEach(channel -> channel.debug(!channel.isDebugOn()));
        if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
            sender.sendMessage(Util.formatErrors(errors));
        }
    }

    @Subcommand("channel")
    @CommandAlias("c")
    @Description("Control a channel, for example, turn it on or off")
    @CommandPermission("nightclub.lightuniverse")
    public class LightControlCommand extends BaseCommand {
        @Subcommand("on")
        @Description("Turn channel on")
        @CommandPermission("nightclub.lightuniverse")
        @CommandCompletion("@channels")
        public static void onTurnOn(CommandIssuer sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                LightChannel.valueOf(args[0]).on(new Color(0x0066ff), null);
            } catch (IllegalArgumentException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            } catch (ArrayIndexOutOfBoundsException e) {
                errors.add(CommandError.TOO_LITTLE_ARGUMENTS);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
            }
        }

        @Subcommand("off")
        @Description("Turn channel off")
        @CommandPermission("nightclub.lightuniverse")
        @CommandCompletion("@channels")
        public static void onTurnOff(CommandIssuer sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                LightChannel.valueOf(args[0]).off(new Color(0x0066ff), null);
            } catch (IllegalArgumentException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            } catch (ArrayIndexOutOfBoundsException e) {
                errors.add(CommandError.TOO_LITTLE_ARGUMENTS);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
            }
        }

        @Subcommand("flash")
        @Description("Flash channel")
        @CommandPermission("nightclub.lightuniverse")
        @CommandCompletion("@channels")
        public static void onFlash(CommandIssuer sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                LightChannel.valueOf(args[0]).flash(new Color(0x0066ff), null);
            } catch (IllegalArgumentException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            } catch (ArrayIndexOutOfBoundsException e) {
                errors.add(CommandError.TOO_LITTLE_ARGUMENTS);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
            }
        }

        @Subcommand("flashoff")
        @Description("Flash off channel")
        @CommandPermission("nightclub.lightuniverse")
        @CommandCompletion("@channels")
        public static void onFlashOff(CommandIssuer sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                LightChannel.valueOf(args[0]).flashOff(new Color(0x0066ff), null);
            } catch (IllegalArgumentException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            } catch (ArrayIndexOutOfBoundsException e) {
                errors.add(CommandError.TOO_LITTLE_ARGUMENTS);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
            }
        }

        @Subcommand("ringzoom")
        @Description("rz")
        @CommandPermission("nightclub.lightuniverse")
        public static void onRingZoom(CommandIssuer sender) {
            List<CommandError> errors = isUnloaded();
            try {
                Nightclub.getLightUniverseManager().getLoadedUniverse().getLights().forEach(Light::ringZoom);
            } catch (IllegalArgumentException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            } catch (ArrayIndexOutOfBoundsException e) {
                errors.add(CommandError.TOO_LITTLE_ARGUMENTS);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
            }
        }


    }
}
