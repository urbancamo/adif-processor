package uk.m0nom.adifproc.adif3.label;

import org.apache.logging.log4j.util.Strings;
import org.apache.maven.shared.utils.StringUtils;
import org.marsik.ham.adif.enums.QslSent;
import org.marsik.ham.adif.enums.QslVia;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.contacts.Qsos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Adif3LabelFormatter {
    private final static int PAGE_WIDTH = 97;
    private final static int PAGE_LENGTH = 75;

    private final static int LABEL_WIDTH = 27;

    private final static int[] Y_POS = {2, 11, 21, 30, 39, 49, 58, 67};
    private final static int[] X_POS = {0, 34, 69};

    public StringBuilder format(Qsos qsos) {
        StringBuilder sb = new StringBuilder();

        List<Page> pages = formatQsos(qsos.getQsos().stream().filter(Qso::isQslViaBureau).collect(Collectors.toList()));
        for (int i = 0; i < pages.size(); i++) {
            Collection<String> contents = pages.get(i).dumpPage();
            Iterator<String> iterator = contents.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next() + "\n");
            }
        }
        return sb;
    }

    public List<Page> formatQsos(List<Qso> qsos) {
        List<Page> pages = new ArrayList<>(1);
        int qsoIndex = 0;
        int qsoCount = qsos.size();
        int pageIndex = 1;

        while (qsoIndex < qsoCount) {
            Page page = new Page(pageIndex, PAGE_WIDTH, PAGE_LENGTH);
            pages.add(page);
            for (int y = 0; y < Y_POS.length; y++) {
                for (int x = 0; x < X_POS.length; x++) {
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
            }
            pageIndex++;
        }
        return pages;
    }

//      000000000111111111122222222
//      123456789012345678901234567
//    0┌───────────────────────────┐
//    1│EA8/M0NOM/P                │
//    2│via: Bureau                │
//    3│Date     Time Band RST Mode│
//    4│20220312 1134 160  599 CW  │
//    5│From: G/LD-050             │
//    6│Thanks for great QSO!      │
//    7│                    PSE QSL│
//     └───────────────────────────┘

    private void impressQso(Page page, Qso qso, int offsetX, int offsetY) {
        page.writeString(String.format(qso.getRecord().getCall()), offsetX, offsetY);
        if (Strings.isNotBlank(qso.getRecord().getQslVia())) {
            page.writeString(String.format("via %s", StringUtils.abbreviate(qso.getRecord().getQslVia(), LABEL_WIDTH-5)), offsetX, offsetY + 1);
        }
        page.writeString(String.format("Date     Time Band RST Mode"), offsetX, offsetY+2);

        DateTimeFormatter dateS = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = dateS.format(qso.getRecord().getQsoDate());
        DateTimeFormatter timeS = DateTimeFormatter.ofPattern("hhmm");
        String time = timeS.format(qso.getRecord().getTimeOn());

        String band = StringUtils.rightPad(qso.getRecord().getBand().adifCode(),4);
        String rst = StringUtils.leftPad(qso.getRecord().getRstSent(), 3);
        String mode = StringUtils.rightPad(qso.getRecord().getMode().adifCode(), 4);

        page.writeString(String.format("%s %s %s %s %s", date, time, band, rst, mode), offsetX, offsetY+3);
        String activity = getMaybeMyActivityRef(qso);
        page.writeString(activity, offsetX, offsetY+4);
        if (qso.getRecord().getQslMsg() != null) {
            String qslMsg = StringUtils.abbreviate(qso.getRecord().getQslMsg(), LABEL_WIDTH);
            page.writeString(qslMsg, offsetX, offsetY + 5);
        }
        if (qso.getRecord().getQslRDate() != null) {
            page.writeString("THX QSL", offsetX+21, offsetY+6);
        } else {
            page.writeString("PSE QSL", offsetX+21, offsetY+6);
        }
    }

    private String getMaybeMyActivityRef(Qso qso) {
        String activity = "";
        if (qso.getRecord().getMySotaRef() != null) {
            activity = String.format("From SOTA: %s", qso.getRecord().getMySotaRef().getValue());
        } else if (qso.getRecord().getMyWwffRef() != null) {
            activity = String.format("From WWFF: %s", qso.getRecord().getMyWwffRef().getValue());
        } else if (qso.getFrom().hasActivity()) {
            Activity act = qso.getFrom().getActivities().values().iterator().next();
            activity = String.format("From %s: %s", act.getName(), act.getRef());
        }
        return StringUtils.abbreviate(activity, LABEL_WIDTH);
    }

    private String getMaybeTheirActivityRef(Qso qso) {
        String activity = "";
        if (qso.getRecord().getSotaRef() != null) {
            activity = String.format(" SOTA: %s", qso.getRecord().getSotaRef().getValue());
        } else if (qso.getRecord().getWwffRef() != null) {
            activity = String.format(" WWFF: %s", qso.getRecord().getWwffRef().getValue());
        } else if (qso.getTo().hasActivity()) {
            Activity act = qso.getTo().getActivities().values().iterator().next();
            activity = String.format(" %s: %s", act.getName(), act.getRef());
        }
        return StringUtils.abbreviate(activity, LABEL_WIDTH);
    }
}
