package uk.m0nom.adif3;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
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

public class Adif3PrintFormatter {
    class PrintState {
        StringBuilder sb;
        int currentPage;
        int currentLine;
        int currentColumn;
        int currentRecord;
    }

    private YamlMapping config = null;
    private PrintJobConfig printJobConfig;
    private PageConfig pageConfig;

    private PrintState state;

    public PrintJobConfig getPrintJobConfig() { return printJobConfig; }

    public void configure(String yamlConfigFile) throws IOException {
        config = Yaml.createYamlInput(new File(yamlConfigFile)).readYamlMapping();
        YamlMapping printJob = config.yamlMapping("printJob");
        printJobConfig = new PrintJobConfig();
        printJobConfig.setInEncoding(printJob.string("inEncoding"));
        printJobConfig.setOutEncoding(printJob.string("outEncoding"));
        printJobConfig.setStartCommand(printJob.string("startCommand"));
        printJobConfig.setEndCommand(printJob.string("endCommand"));

        YamlMapping page = config.yamlMapping("page");
        pageConfig = new PageConfig();
        printJobConfig.setPageConfig(pageConfig);
        pageConfig.setPageHeight(page.integer("pageHeight"));
        pageConfig.setPageWidth(page.integer("pageWidth"));
        pageConfig.setTopMargin(page.integer("topMargin"));
        pageConfig.setBottomMargin(page.integer("bottomMargin"));
        pageConfig.setLeftMargin(page.integer("leftMargin"));
        pageConfig.setRightMargin(page.integer("rightMargin"));
        pageConfig.setPageEnd(stripQuotes(page.string("pageEnd")));
        pageConfig.setLineEnd(stripQuotes(page.string("lineEnd")));
        pageConfig.setHeaderLine(stripQuotes(page.string("headerLine")));
        LineConfig line = new LineConfig();
        pageConfig.setLine(line);

        Collection<YamlNode> nodes = config.yamlSequence("columns").values();


        for (YamlNode node : nodes) {
            ColumnConfig column = new ColumnConfig();
            YamlMapping colMap = node.asMapping().yamlMapping("column");

            // each column is a mapping
            column.setAdif(colMap.string("adif"));
            column.setHeader(colMap.string("header"));
            column.setStart(colMap.integer("start"));
            column.setLength(colMap.integer("length"));
            column.setAlign(colMap.string("align"));
            column.setFormat(colMap.string("format"));
            line.addColumn(column);
        }
    }

    private String stripQuotes(String str) {
        String stripped = str;
        if (stripped.startsWith("'")) {
            stripped = stripped.substring(1, str.length());
        }
        if (stripped.endsWith("'")) {
            stripped = stripped.substring(0, stripped.length()-1);
        }
        return stripped;
    }
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
        return (state.currentLine == 1 || state.currentLine % pageConfig.getPageHeight() == 0);
    }

    private void handlePageBreak(Adif3 log) {
        if (state.currentLine != 1) {
            // Need to end the previous page
            // Add a Control-L
            printPageEnd();
        }
        for (int i = 0; i < pageConfig.getTopMargin(); i++) {
            printLineEnd();
        }
        printHeader(log);
        for (int i = 0; i < pageConfig.getHeaderMargin(); i++) {
            printLineEnd();
        }
    }

    private void printPageEnd() {
        for (int i = 0; i < pageConfig.getBottomMargin(); i++) {
            printLineEnd();
        }
        state.sb.append(pageConfig.getPageEnd());
        state.currentPage++;
    }

    private void printHeader(Adif3 log) {
        if (pageConfig.getHeaderLine().length() > 0) {
            if ("COLUMN_NAMES".equals(pageConfig.getHeaderLine())) {
                printColumnHeaders();
            } else {
                state.sb.append("TODO");
            }
        }
    }

    private void printColumnHeaders() {
        StringBuilder line = new StringBuilder();
        LineConfig lineConfig = pageConfig.getLine();
        List<ColumnConfig> columnConfigs = lineConfig.getColumns();
        for (ColumnConfig columnConfig : columnConfigs) {
            printColumnHeader(columnConfig, line);
        }
        printLine(line.toString());
    }

    private void printColumnHeader(ColumnConfig column, StringBuilder line) {
        String header = column.getHeader();
        int position = column.getStart();
        printValueToColumn(column, header, line);
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
        for (int i = state.currentColumn; i < position; i++) {
            line.append(' ');
            state.currentColumn++;
        }
    }

    private void printLine(String line) {
        state.sb.append(line);
        printLineEnd();
    }

    private void printLineEnd() {
        switch (pageConfig.getLineEnd()) {
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
        for (ColumnConfig column : pageConfig.getLine().getColumns()) {
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
                    value = rec.getSotaRef().adifCode();
                }
                break;
            case "MY_SOTA_REF":
                Sota mySotaRef = rec.getMySotaRef();
                if (mySotaRef != null && !"G/LD-999".equalsIgnoreCase(mySotaRef.adifCode())) {
                    value = rec.getMySotaRef().adifCode();
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
        }
        return value;
    }
}
