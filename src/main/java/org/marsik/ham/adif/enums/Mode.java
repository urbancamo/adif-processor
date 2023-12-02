package org.marsik.ham.adif.enums;

import org.marsik.ham.adif.enums.AdifEnumCode;

public enum Mode implements AdifEnumCode {
    AM,
    ARDOP,
    ATV,
    CHIP,
    CLO,
    CONTESTI,
    CW,
    DIGITALVOICE,
    DOMINO,
    DYNAMIC,
    FAX,
    FM,
    FSK441,
    FT8,
    HELL,
    ISCAT,
    JT4,
    JT6M,
    JT9,
    JT44,
    JT65,
    MFSK,
    MSK144,
    MT63,
    OLIVIA,
    OPERA,
    PAC,
    PAX,
    PKT,
    PSK,
    PSK2K,
    Q15,
    QRA64,
    ROS,
    RTTY,
    RTTYM,
    SSB,
    SSTV,
    T10,
    THOR,
    THRB,
    TOR,
    V4,
    VOI,
    WINMOR,
    WSPR,
    AMTORFEC, // Import Only
    ASCI, // Import Only
    C4FM, // Import Only
    CHIP64, // Import Only
    CHIP128, // Import Only
    DOMINOF, // Import Only
    DSTAR, // Import Only
    FMHELL, // Import Only
    FSK31, // Import Only
    GTOR, // Import Only
    HELL80, // Import Only
    HFSK, // Import Only
    JT4A, // Import Only
    JT4B, // Import Only
    JT4C, // Import Only
    JT4D, // Import Only
    JT4E, // Import Only
    JT4F, // Import Only
    JT4G, // Import Only
    JT65A, // Import Only
    JT65B, // Import Only
    JT65C, // Import Only
    MFSK8, // Import Only
    MFSK16, // Import Only
    PAC2, // Import Only
    PAC3, // Import Only
    PAX2, // Import Only
    PCW, // Import Only
    PSK10, // Import Only
    PSK31, // Import Only
    PSK63, // Import Only
    PSK63F, // Import Only
    PSK125, // Import Only
    PSKAM10, // Import Only
    PSKAM31, // Import Only
    PSKAM50, // Import Only
    PSKFEC31, // Import Only
    PSKHELL, // Import Only
    QPSK31, // Import Only
    QPSK63, // Import Only
    QPSK125, // Import Only
    THRBX; // Import Only

    @Override
    public String adifCode() {
        return name();
    }

    public static Mode findByCode(String code) {
        return Mode.valueOf(code.toUpperCase().replace(' ', '_'));
    }
}
