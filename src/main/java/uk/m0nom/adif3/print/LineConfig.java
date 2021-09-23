package uk.m0nom.adif3.print;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LineConfig {
    List<ColumnConfig> columns;

    public LineConfig() {
        columns = new ArrayList<>();
    }

    public void addColumn(ColumnConfig column) {
        columns.add(column);
    }
}
