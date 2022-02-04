package uk.m0nom.adif3.print;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This is how these parameters relate to a printed page:
 * startCommand is sent to the printer before the start of a print job
 * endCommand is sent to the printer at the end of a print job
 *
 *     ---------------------- width ---------------------------------
 *     +------------------------------------------------------------+
 *  ^  |                                                            |  ^
 *  |  |                                                            |  V - topMargin
 *  |  + HEADER LINE TEXT                                           | - headerLine
 *  |  |                                                            | ^
 *  |  |                                                            | | - header margin
 *  |  |                                                            | v
 *  |  | QSO LINE 1                                                 |
 *  |  | QSO LINE 2                                                 |
 *  |  | ..........                                                 |
 *  |  | QSO LINE n                                                 |
 *  |  |                                                            | ^
 *  |  |                                                            | | - bottomMargin
 *  v  |                                                            | v
 *     +------------------------------------------------------------+
 *  ^ pageHeight
 */
@Getter
@Setter
@NoArgsConstructor
public class PageConfig {
    String pageEnd;
    String lineEnd;
    String headerLine;
    int pageHeight;
    int pageWidth;
    int topMargin;
    int headerMargin;
    int bottomMargin;
    int leftMargin;
    int rightMargin;
    String columnSeparator;
    String headerSeparator;
    LineConfig line;
}
