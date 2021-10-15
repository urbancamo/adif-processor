package uk.m0nom.adif3.transform;

public enum CallsignSuffix {
    PORTABLE("Portable"),
    MOBILE("Mobile"),
    MARITIME_MOBILE("Maritime Mobile"),
    PEDESTRIAN_MOBILE("Pedestrian Mobile");

    private final String description;

    CallsignSuffix(String description) {
        this.description = description;
    }
}
