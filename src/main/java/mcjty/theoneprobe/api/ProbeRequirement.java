package mcjty.theoneprobe.api;

public enum ProbeRequirement {


    PROBE_NOTNEEDED(0),
    PROBE_NEEDED(1),
    PROBE_NEEDEDHARD(2),
    PROBE_NEEDEDFOREXTENDED(3);


    public final int configNumber;


    ProbeRequirement(int configNumber) {
        this.configNumber = configNumber;
    }

    public static ProbeRequirement getFromNumber(int configNumber) {
        for (ProbeRequirement requirement : values()) {
            if (requirement.configNumber == configNumber) {
                return requirement;
            }
        }
        throw new IllegalArgumentException("Unknown ProbeRequirement config number: " + configNumber);
    }
}
