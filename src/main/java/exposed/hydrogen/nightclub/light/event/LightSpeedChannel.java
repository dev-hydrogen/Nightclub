package exposed.hydrogen.nightclub.light.event;

import lombok.Getter;

public enum LightSpeedChannel {
    DEFAULT(LightChannel.CENTER_LIGHTS), // this should never receive speed events
    LEFT_ROTATING_LASERS(LightChannel.LEFT_ROTATING_LASERS),
    RIGHT_ROTATING_LASERS(LightChannel.RIGHT_ROTATING_LASERS);

    @Getter private final LightChannel channel;

    LightSpeedChannel(LightChannel channel) {
        this.channel = channel;
    }
}
