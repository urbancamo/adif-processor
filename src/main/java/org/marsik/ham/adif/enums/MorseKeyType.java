package org.marsik.ham.adif.enums;

import lombok.Getter;

/**
 * Enumeration of different types of Morse keys along with their definitions, operational methods, and examples.
 * Introduced in ADIF 3.1.5
 */
@Getter
public enum MorseKeyType {
    SK("Straight Key",	"a single control which actuates a single switch.",	"a human makes the dits and dahs and builds characters",	"Lionel J-38"),
    SS("Sideswiper",	"a single control which actuates a SPDT (single poll, double throw) switch.",	"a human makes the dits and dahs and builds characters",	"W1SFR Green Machine Torsion Bar Cootie"),
    BUG("Mechanical semi-automatic keyer or Bug",	"a control which actuates a switch as well as a control which actuates a spring and pendulum mechanism which actuates a switch. Both switches are wired in parallel.",	"a machine makes the dits and a human makes the dahs and builds characters.",	"Vibroplex Blue Racer Deluxe"),
    FAB("Mechanical fully-automatic keyer or Bug",	"a control which actuates one of two separate spring and pendulum mechanisms at a time, each of which actuates a switch. Both switches are wired in parallel.",	"a machine makes the dits and the dahs and a human builds characters.",	"GHD GN209FA fully-automatic bug"),
    SP("Single Paddle",	"a single control which actuates two independent switches.",	"a machine makes the dits and the dahs and a human builds the characters.",	"American Morse Mini-B"),
    DP("Dual Paddle",	"two controls which actuate independent switches.",	"a machine makes the dits and the dahs and a human builds the characters.",	"Begali Sculpture, VK3IL pressure paddles, M0UKD capacitive touch paddles"),
    CPU("Computer Driven",	"an electronic device performs the actuation of the switch.",	"a machine makes the dits and dahs to build the characters.",	"N1MM+ Logging Software");

    private final String name;
    private final String definition;
    private final String howItWorks;
    private final String example;

    MorseKeyType(String name, String definition, String howItWorks, String example) {
        this.name = name;
        this.definition = definition;
        this.howItWorks = howItWorks;
        this.example = example;
    }
}
