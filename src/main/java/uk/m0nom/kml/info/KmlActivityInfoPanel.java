package uk.m0nom.kml.info;

import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import uk.m0nom.activity.Activity;
import uk.m0nom.adif3.control.TransformControl;

public class KmlActivityInfoPanel {
    public String getPanelContentForActivity(TransformControl control, Activity activity) {
        final Context context = new Context();
        context.setVariable(activity.getType().getActivityName().toLowerCase(), activity);
        String html = control.getTemplateEngine().process(new TemplateSpec("KmlActivityInfo", TemplateMode.XML), context);
        return html.replace("\n", "");
    }

    private void setVariable(Context context, String key, String value) {
        if (StringUtils.isNotEmpty(value)) {
            context.setVariable(key, value);
        }
    }
}
