package uk.m0nom.activity.rota;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;

@Getter
@Setter
public class RotaInfo extends Activity {
    private String club;
    private String wab;
    private String grid;

    public RotaInfo() {
        super(ActivityType.ROTA);
    }

    @Override
    public String getUrl() {
        return "https://rota.barac.org.uk/";
    }
}
