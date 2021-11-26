package uk.m0nom.adif3.print;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for a row in the print output file, consisting of all the column definitions that make up that row
 */
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
