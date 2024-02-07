package uk.m0nom.adifproc.adif3.label;

import org.apache.logging.log4j.util.Strings;
import org.apache.maven.shared.utils.StringUtils;
import org.marsik.ham.adif.enums.QslSent;
import org.marsik.ham.adif.enums.QslVia;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.adif3.contacts.AlphabeticQsoComparator;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.contacts.Qsos;
import uk.m0nom.adifproc.callsign.CallsignUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Adif3LabelFormatter {
    private final static int PAGE_WIDTH = 97;
    private final static int PAGE_LENGTH = 75;

    private final static int LABEL_WIDTH = 27;

    private final static int[] Y_POS = {2, 11, 21, 30, 39, 49, 58, 67};
    private final static int[] X_POS = {0, 34, 69};

    public Adif3LabelFormatterResult format(Qsos qsos, String dontQslCallsigns, Integer qslLabelStartPosition) {
        Adif3LabelFormatterResult result = new Adif3LabelFormatterResult();
        StringBuilder sb = new StringBuilder();
        Collection<String> excludeCallsigns = processDontQslCallsignString(dontQslCallsigns);

        List<Qso> qsosToQsl = new ArrayList<>(qsos.getQsos().size());
        for (Qso qso : qsos.getQsos()) {
            String callsign = qso.getTo().getCallsign().toUpperCase();
            if (qso.isQslViaBureau() && !excludeCallsigns.contains(callsign)) {
                qsosToQsl.add(qso);
            }
        }
        qsosToQsl.sort(new AlphabeticQsoComparator());
        List<Page> pages = formatQsos(qsosToQsl, qslLabelStartPosition);
        for (Page page : pages) {
            Collection<String> contents = page.dumpPage();
            for (String content : contents) {
                sb.append(content).append("\n");
            }
        }
        result.setCallsigns(qsosToQsl.stream().map(qso -> qso.getTo().getCallsign()).distinct().sorted().collect(Collectors.toList()));
        result.setLabels(sb.toString());

        return result;
    }

    private Collection<String> processDontQslCallsignString(String dontQslCallsigns) {
        Collection<String> dontQslCallsignList = new ArrayList<>();
        String strippedList = dontQslCallsigns.replaceAll(",", " ").replaceAll("\\s+", " ").toUpperCase();
        if (!strippedList.isEmpty()) {
            String[] splitList = strippedList.split(" ");
            dontQslCallsignList = Arrays.asList(splitList);
        }
        return dontQslCallsignList;
    }

    public List<Page> formatQsos(List<Qso> qsos, Integer startPosition) {
        List<Page> pages = new ArrayList<>(1);
        int qsoIndex = 0;
        int qsoCount = qsos.size();
        int pageIndex = 1;
        int initialX = 0;
        int initialY = 0;

        if (startPosition != null) {
            initialX = (startPosition - 1) % 3;
            initialY = (startPosition - 1) / 3;
        }
        while (qsoIndex < qsoCount) {
            Page page = new Page(pageIndex, PAGE_WIDTH, PAGE_LENGTH);
            pages.add(page);
            for (int y = initialY; y < Y_POS.length; y++) {
                for (int x = initialX; x < X_POS.length; x++) {
                    //System.out.println(String.format("Page: %d, y: %d, x: %d, qso: %d", pageIndex, Y_POS[y], X_POS[x], (qsoIndex+1)));
                    Qso qso = qsos.get(qsoIndex++);
                    impressQso(page, qso, X_POS[x], Y_POS[y]);

                    qso.getRecord().setQslSentVia(QslVia.BUREAU);
                    qso.getRecord().setQslSent(QslSent.SENT);
                    qso.getRecord().setQslSDate(LocalDate.now());

                    if (qsoIndex == qsoCount) {
                        return pages;
                    }
                }
                initialY = 0;
                initialX = 0;
            }
            pageIndex++;
        }
        return pages;
    }

//      000000000111111111122222222
//      123456789012345678901234567
//    0┌───────────────────────────┐
//    1│M0NOM as EA8/M0NOM/P       │
//    2│via: Bureau                │
//    3│Date     Time Band RST Mode│
//    4│20220312 1134 160  599 CW  │
//    5│From: G/LD-050             │
//    6│Thanks for great QSO!      │
//    7│                    PSE QSL│
//     └───────────────────────────┘

    private void impressQso(Page page, Qso qso, int offsetX, int offsetY) {
        var rec = qso.getRecord();
        String call = qso.getTo().getCallsign() != null ? qso.getTo().getCallsign() : "";
        String ownerCallsign = "";
        if (qso.getTo().getQrzInfo() != null && qso.getTo().getQrzInfo().getCall() != null) {
            ownerCallsign = CallsignUtils.stripSuffix(qso.getTo().getQrzInfo().getCall());
        }
        page.writeString(ownerCallsign, offsetX, offsetY);
        if (!StringUtils.equalsIgnoreCase(call, ownerCallsign)) {
            page.writeString(StringUtils.left("as " + call, LABEL_WIDTH - ownerCallsign.length() -5),offsetX + ownerCallsign.length()+1, offsetY);
        }
        if (Strings.isNotBlank(rec.getQslVia())) {
            page.writeString(String.format("via %s", StringUtils.abbreviate(rec.getQslVia(), LABEL_WIDTH - 5)), offsetX, offsetY + 1);
        }
        page.writeString("Date     Time Band RST Mode", offsetX, offsetY + 2);

        DateTimeFormatter dateS = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = "";
        if (qso.getRecord().getQsoDate() != null) {
            date = dateS.format(qso.getRecord().getQsoDate());
        }
        DateTimeFormatter timeS = DateTimeFormatter.ofPattern("HHmm");
        String time = "";
        if (qso.getRecord().getTimeOn() != null) {
            time = timeS.format(qso.getRecord().getTimeOn());
        }

        String band = rec.getBand() == null ? "    " : StringUtils.rightPad(rec.getBand().adifCode(), 4);
        String rst = StringUtils.isEmpty(rec.getRstSent()) ? "   " : StringUtils.leftPad(rec.getRstSent(), 3);
        String mode = rec.getMode() == null ? "    " : StringUtils.rightPad(rec.getMode().adifCode(), 4);

        page.writeString(String.format("%s %s %s %s %s", date, time, band, rst, mode), offsetX, offsetY + 3);
        String activity = getMaybeMyActivityRef(qso);
        page.writeString(activity, offsetX, offsetY + 4);
        if (qso.getRecord().getQslMsg() != null) {
            String qslMsg = qso.getRecord().getQslMsg();
            page.writeString(StringUtils.left(qslMsg, LABEL_WIDTH), offsetX, offsetY + 5);
            if (qslMsg.length() > LABEL_WIDTH) {
                page.writeString(StringUtils.left(qslMsg.substring(LABEL_WIDTH), 22), offsetX,offsetY + 6);
            }
        }
        if (qso.getRecord().getQslRDate() != null) {
            page.writeString("THX QSL", offsetX + 21, offsetY + 6);
        } else {
            page.writeString("PSE QSL", offsetX + 21, offsetY + 6);
        }
    }

    private String getMaybeMyActivityRef(Qso qso) {
        String activityList = qso.getFrom().getActivities()
                .stream()
                .map(Activity::getRef)
                .collect(Collectors.joining (" "));
        if (StringUtils.isNotEmpty(activityList)) {
            return StringUtils.left("From " + activityList, LABEL_WIDTH);
        }
        return "";
    }

}
