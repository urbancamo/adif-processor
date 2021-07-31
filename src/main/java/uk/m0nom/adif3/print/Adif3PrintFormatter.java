package uk.m0nom.adif3.print;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.types.Sota;
import uk.m0nom.adif3.print.ColumnConfig;
import uk.m0nom.adif3.print.LineConfig;
import uk.m0nom.adif3.print.PageConfig;
import uk.m0nom.adif3.print.PrintJobConfig;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class Adif3PrintFormatter {
    private static final Logger logger = Logger.getLogger(Adif3PrintFormatter.class.getName());

    class PrintState {
        StringBuilder sb;
        int currentPage;
        int currentLine;
        int currentColumn;
        int currentRecord;
    }

    public Adif3PrintFormatter() {
        printJobConfig = new PrintJobConfig();
    }

    private YamlMapping config = null;
    private PrintJobConfig printJobConfig;

    private PrintState state;

    public PrintJobConfig getPrintJobConfig() { return printJobConfig; }


    public StringBuilder format(Adif3 log) {
        resetPrintState();

        for (Adif3Record rec : log.getRecords()) {
            if (atPageBreak()) {
                handlePageBreak(log);
            }
            printRecord(rec);
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

    private void handlePageBreak(Adif3 log) {
        if (state.currentLine != 1) {
            // Need to end the previous page
            // Add a Control-L
            printPageEnd();
        }
        for (int i = 0; i < printJobConfig.pageConfig.getTopMargin(); i++) {
            printLineEnd();
        }
        printHeader(log);
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

    private void printHeader(Adif3 log) {
        if (printJobConfig.pageConfig.getHeaderLine().length() > 0) {
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
        String separator = StringUtils.repeat(printJobConfig.pageConfig.getHeaderSeparator().charAt(0), column.getLength()-1);
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
        switch (printJobConfig.pageConfig.getLineEnd()) {
            case "unix":
                state.sb.append("\n");
                break;
            default:
                state.sb.append("\r\n");
        }
        state.currentLine++;
        state.currentColumn = 1;
    }

    public void printRecord(Adif3Record rec) {
        StringBuilder line = new StringBuilder();
        String value = "";
        for (ColumnConfig column : printJobConfig.pageConfig.getLine().getColumns()) {
            value = getAdif3FieldFromRecord(rec, column);
            printValueToColumn(column, value, line);
        }
        printLine(line.toString());
    }

    private String getAdif3FieldFromRecord(Adif3Record rec, ColumnConfig column) {
        String value = "";
        DateTimeFormatter timeFormat;
        DateTimeFormatter dateFormat;

        switch (column.getAdif()) {
            case "QSO_DATE":
                LocalDate date = rec.getQsoDate();
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
                value = rec.getStationCallsign();
                break;
            case "CALL":
                value = rec.getCall();
                break;
            case "BAND":
                value = rec.getBand().adifCode();
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
            case "QTH":
                value = rec.getQth();
                break;
            case "GRIDSQUARE":
                value = rec.getGridsquare();
                if ("JJ00aa".equals(value)) {
                    value = "";
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
                }
                break;
            case "SIG_INFO" :
                if (rec.getSigInfo() != null) {
                    value = rec.getSigInfo();
                }
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
            default:
                logger.warning(String.format("Print formatting column %s not currently handled", column.getAdif()));
                break;
        }
        return value;
    }
}
