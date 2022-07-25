package exposed.hydrogen.nightclub.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import exposed.hydrogen.nightclub.Nightclub;
import exposed.hydrogen.nightclub.light.LightUniverse;
import exposed.hydrogen.nightclub.light.LightUniverseManager;
import exposed.hydrogen.nightclub.light.Ring;
import exposed.hydrogen.nightclub.light.data.LightType;
import exposed.hydrogen.nightclub.light.data.RingData;
import exposed.hydrogen.nightclub.light.data.RingMovementData;
import exposed.hydrogen.nightclub.util.CrossCompatPlayer;
import exposed.hydrogen.nightclub.util.Location;
import exposed.hydrogen.nightclub.util.Util;
import lombok.Getter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@CommandAlias("ring")
@CommandPermission("nightclub.ring")
public class RingCommand extends BaseCommand {
    @Getter private static Ring ring;

    private static List<CommandError> isUnloaded() {
        Nightclub.getLightUniverseManager().save();
        List<CommandError> errors = new ArrayList<>();
        errors.add(ring == null ? CommandError.RING_UNLOADED : CommandError.VALID);
        errors.add(Nightclub.getLightUniverseManager().getLoadedUniverse() == null ? CommandError.LIGHTUNIVERSE_UNLOADED : CommandError.VALID);
        errors.add(BeatmapCommand.getPlayer() != null && BeatmapCommand.getPlayer().isPlaying() ? CommandError.BEATMAP_PLAYING : CommandError.VALID);
        return errors;
    }

    private static List<CommandError> isUnloaded(String[] args, int minArgsLength) {
        List<CommandError> errors = isUnloaded();
        errors.add(args.length < minArgsLength ? CommandError.TOO_LITTLE_ARGUMENTS : CommandError.VALID);
        return errors;
    }

    @Subcommand("build")
    @Description("Build a new Ring!")
    @CommandPermission("nightclub.ring")
    public static void onBuild(CommandIssuer sender) {
        LightUniverseManager manager = Nightclub.getLightUniverseManager();
        CrossCompatPlayer player = Nightclub.getCrossCompatUtil().getPlayer(sender);

        List<CommandError> errors = isUnloaded();
        errors.add(player == null ? CommandError.COMMAND_SENT_FROM_CONSOLE : CommandError.VALID);
        if (errors.stream().anyMatch(error -> error != CommandError.VALID && error != CommandError.RING_UNLOADED)) {
            sender.sendMessage(Util.formatErrors(errors));
            return;
        }
        UUID uuid = UUID.randomUUID();
        RingData ringData = RingData.builder()
                .ringMovementData(new RingMovementData())
                .ringCount(4)
                .ringLightCount(3)
                .ringOffset(0.8)
                .ringRotation(6)
                .ringSize(15)
                .ringSpacing(8)
                .build();

        ring = new Ring(
                uuid,
                uuid.toString(),
                player.getLocation(),
                ringData,
                LightType.GUARDIAN_BEAM
        );
        ring.start();
        manager.getLoadedUniverse().addRing(ring);
        manager.save();
    }

    @Subcommand("remove")
    @Description("Remove currently loaded Ring")
    @CommandPermission("nightclub.ring")
    public static void onRemove(CommandIssuer sender) {
        LightUniverseManager manager = Nightclub.getLightUniverseManager();
        if (isUnloaded().stream().anyMatch(error -> error != CommandError.VALID)) {
            sender.sendMessage(Util.formatErrors(isUnloaded()));
            return;
        }
        ring.stop();
        manager.getLoadedUniverse().removeRing(ring);
        manager.save();
        ring = null;
    }

    @Subcommand("load")
    @Description("Load a Light from currently loaded LightUniverse")
    @CommandCompletion("@rings")
    @CommandPermission("nightclub.ring")
    public static void onLoad(CommandIssuer sender, String[] args) {
        LightUniverseManager manager = Nightclub.getLightUniverseManager();
        LightUniverse universe = manager.getLoadedUniverse();

        List<CommandError> errors = isUnloaded();
        errors.add(args.length < 1 ? CommandError.TOO_LITTLE_ARGUMENTS : CommandError.VALID);
        if (errors.stream().noneMatch(error -> error == CommandError.LIGHTUNIVERSE_UNLOADED)) {
            errors.add(universe.getRing(args[0]) == null ? CommandError.INVALID_ARGUMENT : CommandError.VALID);
        }
        if (errors.stream().anyMatch(error -> error != CommandError.VALID && error != CommandError.RING_UNLOADED)) {
            sender.sendMessage(Util.formatErrors(errors));
            return;
        }

        ring = universe.getRing(args[0]);
        ring.start();
    }

    @Subcommand("clone")
    @Description("Clone a Ring from currently loaded LightUniverse")
    @CommandCompletion("@rings")
    @CommandPermission("nightclub.ring")
    public static void onClone(CommandIssuer sender, String[] args) {
        LightUniverseManager manager = Nightclub.getLightUniverseManager();
        LightUniverse universe = manager.getLoadedUniverse();
        CrossCompatPlayer player = Nightclub.getCrossCompatUtil().getPlayer(sender);

        List<CommandError> errors = isUnloaded();
        errors.add(player == null ? CommandError.COMMAND_SENT_FROM_CONSOLE : CommandError.VALID);
        errors.add(args.length < 1 ? CommandError.TOO_LITTLE_ARGUMENTS : CommandError.VALID);
        if (errors.stream().noneMatch(error -> error == CommandError.LIGHTUNIVERSE_UNLOADED)) {
            errors.add(universe.getLight(args[0]) == null ? CommandError.INVALID_ARGUMENT : CommandError.VALID);
        }
        if (errors.stream().anyMatch(error -> error != CommandError.VALID && error != CommandError.RING_UNLOADED)) {
            sender.sendMessage(Util.formatErrors(errors));
            return;
        }

        Ring r = universe.getRing(args[0]);
        UUID uuid = UUID.randomUUID();
        ring = Ring.builder()
                .uniqueId(uuid)
                .name(uuid.toString())
                .location(player.getLocation().add(0, 1, 0))
                .lightType(r.getLightType())
                .ringData(r.getRingData().clone()).build();
        manager.getLoadedUniverse().addRing(ring);
        manager.save();
        ring.start();
    }

    @Subcommand("data")
    @Description("Modify a Ring's data")
    @CommandPermission("nightclub.ring")
    public class RingDataCommand extends BaseCommand {
        @Subcommand("name")
        @Description("Alter Ring Name")
        @CommandPermission("nightclub.ring")
        public static void onNameChange(CommandIssuer sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            errors.add(Nightclub.getLightUniverseManager().getLoadedUniverse().getLights().stream().anyMatch(l -> Objects.equals(l.getName(), args[0]))
                    ? CommandError.NAME_ALREADY_EXISTS : CommandError.VALID);

            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
                return;
            }
            ring.setName(args[0]);
        }
        @Subcommand("type")
        @Description("Alter the Rings type")
        @CommandCompletion("@types")
        @CommandPermission("nightclub.ring")
        public static void onModifyType(CommandIssuer sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                ring.setLightType(LightType.valueOf(args[0]));
            } catch (IllegalArgumentException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
                return;
            }
            ring.buildLasers();
            ring.start();
        }

        @Subcommand("setlocation")
        @Description("Set the Rings location to your location, including pitch and yaw")
        @CommandPermission("nightclub.ring")
        public static void onSetLocation(CommandIssuer sender, String[] args) {
            List<CommandError> errors = isUnloaded();
            CrossCompatPlayer player = Nightclub.getCrossCompatUtil().getPlayer(sender);
            errors.add(player == null ? CommandError.COMMAND_SENT_FROM_CONSOLE : CommandError.VALID);
            errors.add(args.length > 1 && args.length < 5 ? CommandError.TOO_LITTLE_ARGUMENTS : CommandError.VALID);
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
                return;
            }
            if (args.length >= 5) {
                try {
                    Location loc = new Location(Util.parseNumber(args[0]), Util.parseNumber(args[1]), Util.parseNumber(args[2]), // x y z
                            Util.parseNumber(args[3]), Util.parseNumber(args[4]));
                    ring.setLocation(loc); // pitch and yaw
                    ring.getRingData().getRingMovementData().setPitchYaw(loc);
                } catch (ParseException e) {
                    errors.add(CommandError.INVALID_ARGUMENT);
                    sender.sendMessage(Util.formatErrors(errors));
                    return;
                }
            } else {
                ring.setLocation(player.getLocation().add(0, 1, 0));
                ring.getRingData().getRingMovementData().setPitchYaw(player.getLocation());
            }
            ring.buildLasers();
            ring.start();
        }

        @Subcommand("count")
        @Description("Set ring count")
        @CommandPermission("nightclub.ring")
        public static void onRingCount(CommandIssuer sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                ring.getRingData().setRingCount(Util.parseNumber(args[0]).intValue());
            } catch (ParseException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
            }
            ring.buildLasers();
            ring.start();
        }

        @Subcommand("lightcount")
        @Description("Set ring count")
        @CommandPermission("nightclub.ring")
        public static void onRingLightCount(CommandIssuer sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                ring.getRingData().setRingLightCount(Util.parseNumber(args[0]).intValue());
            } catch (ParseException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
            }
            ring.buildLasers();
            ring.start();
        }

        @Subcommand("size")
        @Description("Set ring size")
        @CommandPermission("nightclub.ring")
        public static void onRingSize(CommandIssuer sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                ring.getRingData().setRingSize(Util.parseNumber(args[0]).intValue());
            } catch (ParseException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
            }
            ring.start();
        }

        @Subcommand("offset")
        @Description("Set ring offset from linked ring")
        @CommandPermission("nightclub.ring")
        public static void onOffset(CommandIssuer sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                ring.getRingData().setRingOffset(Util.parseNumber(args[0]).doubleValue());
            } catch (ParseException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
            }
            ring.start();
        }
        @Subcommand("spacing")
        @Description("Set ring spacing")
        @CommandPermission("nightclub.ring")
        public static void onSpacing(CommandIssuer sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                ring.getRingData().setRingSpacing(Util.parseNumber(args[0]).doubleValue());
            } catch (ParseException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
            }
            ring.start();
        }
        @Subcommand("rotation")
        @Description("Set ring rotation per event")
        @CommandPermission("nightclub.ring")
        public static void onRotation(CommandIssuer sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                ring.getRingData().setRingRotation(Util.parseNumber(args[0]).doubleValue());
            } catch (ParseException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
            }
            ring.start();
        }

        @Subcommand("zoomdistance")
        @Description("Alter zooming distance")
        @CommandPermission("nightclub.ring")
        public static void onModifyDistance(CommandIssuer sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                ring.getRingData().getRingMovementData().setDistance(Util.parseNumber(args[0]).doubleValue());
            } catch (ParseException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
                return;
            }
            ring.start();
        }

        @Subcommand("zoomtime")
        @Description("Alter time to move")
        @CommandPermission("nightclub.ring")
        public static void onModifyTime(CommandIssuer sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                ring.getRingData().getRingMovementData().setDuration(Util.parseNumber(args[0]).doubleValue());
            } catch (ParseException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(Util.formatErrors(errors));
                return;
            }
            ring.start();
        }
    }
}
