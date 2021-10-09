package com.ilm9001.nightclub.light;

import com.ilm9001.nightclub.laser.Laser;
import lombok.Getter;

public enum LightType {
    GUARDIAN_BEAM(Laser.LaserType.GUARDIAN), END_CRYSTAL_BEAM(Laser.LaserType.ENDER_CRYSTAL);
    
    @Getter
    private final Laser.LaserType type;
    
    LightType(Laser.LaserType actualType) {
        type = actualType;
    }
}
