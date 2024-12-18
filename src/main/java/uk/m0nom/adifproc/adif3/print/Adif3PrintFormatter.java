package uk.m0nom.adifproc.adif3.print;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.types.Sota;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.contacts.Qsos;
import uk.m0nom.adifproc.geodesic.GeodesicUtils;
import uk.m0nom.adifproc.maidenheadlocator.MaidenheadLocatorConversion;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

/**
 * This class can 'print' QSOs into either a fixed-column width table or a markdown table based on the
 * supplied print configuration. Not all ADIF fields are currently supported, this is on my TODO list.
 */
@Service
public class Adif3PrintFormatter {
    private static final Logger logger = Logger.getLogger(Adif3PrintFormatter.class.getName());

    private final static List<String> FIRSTNAME_SKIP = new ArrayList<>(Arrays.asList("Op.", "Mr", "Mr.", "Mrs", "Mrs.")) ;

    static class PrintState {
        StringBuilder sb;
        int currentPage;
        int currentLine;
        int currentColumn;
        int currentRecord;
    }

    public Adif3PrintFormatter() {
        printJobConfig = new PrintJobConfig();
    }

    @Getter
    private final PrintJobConfig printJobConfig;

    private PrintState state;


    public StringBuilder format(Qsos qsos) {
        resetPrintState();

        for (Qso qso : qsos.getQsos()) {
            if (qso.getFrom() != null && qso.getTo() != null) {
                if (atPageBreak()) {
                    handlePageBreak();
                }
                printRecord(qso);
            }
        }
        return state.sb;
    }

    private void resetPrintState() {
        state = new PrintState();
        state.sb = new StringBuilder();
        state.currentLine = 1;
        state.currentPage = 1;
        state.currentRecord = 1;
        state.currentColumn = 1;
    }

    private boolean atPageBreak() {
        // Check for page ending
        return (state.currentLine == 1 || state.currentLine % printJobConfig.pageConfig.getPageHeight() == 0);
    }

    private void handlePageBreak() {
        if (state.currentLine != 1) {
            // Need to end the previous page
            // Add a Control-L
            printPageEnd();
        }
        for (int i = 0; i < printJobConfig.pageConfig.getTopMargin(); i++) {
            printLineEnd();
        }
        printHeader();
        for (int i = 0; i < printJobConfig.pageConfig.getHeaderMargin(); i++) {
            printLineEnd();
        }
    }

    private void printPageEnd() {
        for (int i = 0; i < printJobConfig.pageConfig.getBottomMargin(); i++) {
            printLineEnd();
        }
        state.sb.append(printJobConfig.pageConfig.getPageEnd());
        state.currentPage++;
    }

    private void printHeader() {
        if (!printJobConfig.pageConfig.getHeaderLine().isEmpty()) {
            if ("COLUMN_NAMES".equals(printJobConfig.pageConfig.getHeaderLine())) {
                printColumnHeaders();
            } else {
                state.sb.append("TODO");
            }
        }
    }

    private void printColumnHeaders() {
        StringBuilder line = new StringBuilder();
        StringBuilder separator = new StringBuilder();
        boolean printSeparator = StringUtils.length(printJobConfig.pageConfig.getHeaderSeparator()) > 0;
        LineConfig lineConfig = printJobConfig.pageConfig.getLine();
        List<ColumnConfig> columnConfigs = lineConfig.getColumns();
        for (ColumnConfig columnConfig : columnConfigs) {
            printColumnHeader(columnConfig, line);
        }
        printLine(line.toString());

        if (printSeparator) {
            for (ColumnConfig columnConfig : columnConfigs) {
                printColumnHeaderUnderline(columnConfig, separator);
            }
            printLine(separator.toString());
        }
    }

    private void printColumnHeader(ColumnConfig column, StringBuilder line) {
        String header = column.getHeader();
        printValueToColumn(column, header, line);
    }

    private void printColumnHeaderUnderline(ColumnConfig column, StringBuilder line) {
        String separator = StringUtils.repeat(printJobConfig.pageConfig.getHeaderSeparator().charAt(0), column.getLength());
        printValueToColumn(column, separator, line);
    }

    private void printValueToColumn(ColumnConfig column, String value, StringBuilder line) {
        int position = state.currentColumn;
        if (position > 1) {
            position++;
        }

        int width = column.getLength();
        String align = column.getAlign();
        if (align == null) {
            align = "left";
        }
        String formatString = "";
        switch (align) {
            case "left":
                formatString = "%-" + String.format("%d", width) + "s";
                break;
            case "right":
                formatString = "%" + String.format("%d", width) + "s";
                break;
        }
        String content = String.format(formatString, value == null ? "" : value);

        if (state.currentColumn != position) {
            advanceToColumn(line, position);
        }

        // Now truncate to the maximum length
        content = content.substring(0, width);
        line.append(content);
        state.currentColumn += width;
    }

    private void advanceToColumn(StringBuilder line, int position) {
        boolean first= true;
        for (int i = state.currentColumn; i < position; i++) {
            if (first) {
                line.append(printJobConfig.pageConfig.getColumnSeparator());
                first = false;
            } else {
                line.append(' ');
            }
            state.currentColumn++;
        }
    }

    private void printLine(String line) {
        state.sb.append(printJobConfig.pageConfig.getColumnSeparator());
        state.sb.append(line);
        state.sb.append(printJobConfig.pageConfig.getColumnSeparator());
        printLineEnd();
    }

    private void printLineEnd() {
        if ("unix".equals(printJobConfig.pageConfig.getLineEnd())) {
            state.sb.append("\n");
        } else {
            state.sb.append("\r\n");
        }
        state.currentLine++;
        state.currentColumn = 1;
    }

    public void printRecord(Qso qso) {
        StringBuilder line = new StringBuilder();
        String value;
        for (ColumnConfig column : printJobConfig.pageConfig.getLine().getColumns()) {
            value = getAdif3FieldFromRecord(qso, column);
            printValueToColumn(column, value, line);
        }
        printLine(line.toString());
    }

    private String getAdif3FieldFromRecord(Qso qso, ColumnConfig column) {
        String value = "";
        DateTimeFormatter timeFormat;
        DateTimeFormatter dateFormat;
        Adif3Record rec = qso.getRecord();
        boolean markdown = "md".equals(printJobConfig.filenameExtension);

        switch (column.getAdif()) {
            case "QSO_DATE":
                ZonedDateTime date = rec.getQsoDate();
                if (date != null) {
                    dateFormat = DateTimeFormatter.ofPattern(column.getFormat());
                    value = date.format(dateFormat);
                }
                break;
            case "TIME_ON":
                LocalTime time = rec.getTimeOn();
                if (time != null) {
                    timeFormat = DateTimeFormatter.ofPattern(column.getFormat());
                    value = time.format(timeFormat);
                }
                break;
            case "STATION_CALLSIGN":
                if (markdown && qso.getFrom().getQrzInfo() != null) {
                    value = String.format("[%s](https://qrz.com/db/%s)",
                            qso.getFrom().getCallsign(),
                            qso.getFrom().getQrzInfo().getCall());
                } else {
                    value = rec.getStationCallsign();
                }
                break;
            case "CALL":
                if (markdown && qso.getTo().getQrzInfo() != null) {
                    value = String.format("[%s](https://qrz.com/db/%s)",
                            qso.getTo().getCallsign(),
                            qso.getTo().getQrzInfo().getCall());
                } else {
                    value = rec.getCall();
                }
                break;
            case "BAND":
                if (rec.getBand() != null) {
                    value = rec.getBand().adifCode();
                }
                break;
            case "FREQ":
                if (rec.getFreq() != null) {
                    Double freq = rec.getFreq();
                    value = String.format(column.getFormat(), freq);
                }
                break;
            case "MODE":
                if (rec.getMode() != null) {
                    value = rec.getMode().adifCode();
                }
                break;
            case "RST_RCVD":
                value = rec.getRstRcvd();
                break;
            case "RST_SENT":
                value = rec.getRstSent();
                break;
            case "SOTA_REF":
                Sota sotaRef = rec.getSotaRef();
                if (sotaRef != null) {
                    value = rec.getSotaRef().getValue();
                }
                break;
            case "MY_SOTA_REF":
                Sota mySotaRef = rec.getMySotaRef();
                if (mySotaRef != null && !"G/LD-999".equalsIgnoreCase(mySotaRef.getValue())) {
                    value = rec.getMySotaRef().getValue();
                }
                break;
            case "NAME":
                value = rec.getName();
                break;
            case "FIRSTNAME":
                // Attempt to extract the firstname. This isn't foolproof by any means!
                String[] names = StringUtils.split(rec.getName());
                if (names != null && names.length > 0) {
                    value = names[0];
                    // This one primarily for Guru EA2IF
                    if (FIRSTNAME_SKIP.contains(value) && names.length > 1) {
                        value = names[1];
                    }
                }
                break;
            case "QTH":
                value = rec.getQth();
                break;
            case "GRIDSQUARE":
                if (rec.getGridsquare() != null) {
                    value = getNonDubiousGridsquareOptExt(rec);
                }
                break;
            case "COUNTRY":
                value = rec.getCountry();
                break;
            case "COMMENT":
                value = rec.getComment();
                break;
            case "DISTANCE":
                if (rec.getDistance() != null) {
                    Double dist = rec.getDistance();
                    value = String.format(column.getFormat(), dist);
                }
                break;
            case "SRX" :
                if (rec.getSrx() != null) {
                    value = String.format(column.getFormat(), rec.getSrx());
                } else if (rec.getSrxString() != null) {
                    value = rec.getSrxString();
                }
                break;
            case "STX" :
                if (rec.getStx() != null) {
                    value = String.format(column.getFormat(), rec.getStx());
                } else if (rec.getStxString() != null) {
                    value = rec.getStxString();
                }
                break;
            case "SIG" :
                if (rec.getSig() != null) {
                    value = rec.getSig();
                } else if (rec.getSotaRef() != null) {
                    value = "SOTA";
                } else if (rec.getWwffRef() != null) {
                    value = "WWFF";
                } else if (rec.getPotaRef() != null) {
                    value = "POTA";
                }
                break;
            case "SIG_INFO" :
                value = getActivityInfo(qso.getTo().getActivities(), markdown);
                break;
            case "QSL_STATUS":
                boolean sent = rec.getQslSDate() != null;
                boolean recvd = rec.getQslRDate() != null;
                Boolean bureau = null;
                Boolean direct = null;
                if (rec.getQslVia() != null) {
                    bureau = rec.getQslVia().toLowerCase(Locale.ROOT).contains("bureau");
                    direct = rec.getQslVia().toLowerCase(Locale.ROOT).contains("direct");
                }
                if (sent && recvd) {
                    value = "SR";
                } else if (sent) {
                    value = "S ";
                } else if (recvd) {
                    value = " R";
                } else if (Boolean.TRUE.equals(bureau) && Boolean.TRUE.equals(direct)) {
                    value = "BD";
                } else if (Boolean.TRUE.equals(bureau)) {
                    value = " B";
                } else if (Boolean.TRUE.equals(direct)) {
                    value = " D";
                } else {
                    value = "  ";
                }
                break;
            case "BEARING":
                // Determine if we have a bearing between stations
                Double bearing = GeodesicUtils.getBearing(rec.getMyCoordinates(), rec.getCoordinates());
                if (bearing != null) {
                    value = String.format("%03.0f", bearing);
                }
            default:
                logger.warning(String.format("Print formatting column %s not currently handled", column.getAdif()));
                break;
        }
        return value;
    }

    private String getNonDubiousGridsquareOptExt(Adif3Record rec) {
        String value;
        value = rec.getGridsquare().toUpperCase();
        if (MaidenheadLocatorConversion.isADubiousGridSquare(value)) {
            value = "";
        } else if (rec.getGridsquareExt() != null) {
            value += rec.getGridsquareExt().toUpperCase();
        }
        return value;
    }

    private String getActivityInfo(Collection<Activity> activities, boolean markdown) {
        String value = "";
        if (activities != null && !activities.isEmpty()) {
            Activity activity = activities.iterator().next();
            if (markdown && StringUtils.isNotEmpty(activity.getUrl()))  {
                value = String.format("[%s](%s)", activity.getRef(), activity.getUrl());
            } else {
                value = activity.getRef();
            }
        }
        return value;
    }

}
