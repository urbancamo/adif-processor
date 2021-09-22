package uk.m0nom.adif3.transform;

public enum CallsignSuffix {
    PORTABLE("/P", "Portable"),
    MOBILE("/M", "Mobile"),
    MARITIME_MOBILE("/MM", "Maritime Mobile"),
    PEDESTRIAN_MOBILE("/PM", "Pedestrian Mobile");

    private final String name;
    private final String description;

    CallsignSuffix(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
