package uk.m0nom.adifproc.adif3.print;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * This class reads configuration information from a YAML file for the print formatter
 */
@Getter
@Setter
@NoArgsConstructor
public class PrintJobConfig {
    private static final Logger logger = Logger.getLogger(PrintJobConfig.class.getName());

    String filename;
    String name;
    String inEncoding;
    String outEncoding;
    String startCommand;
    String endCommand;
    String filenameExtension;

    PageConfig pageConfig;

    public void configure(String filename, InputStream yamlConfig) throws IOException {
        this.filename = filename;
        YamlMapping config = Yaml.createYamlInput(yamlConfig).readYamlMapping();
        YamlMapping printJob = config.yamlMapping("printJob");
        setName(printJob.string("name"));
        setInEncoding(printJob.string("inEncoding"));
        setOutEncoding(printJob.string("outEncoding"));
        setStartCommand(printJob.string("startCommand"));
        setEndCommand(printJob.string("endCommand"));
        setFilenameExtension(printJob.string("filenameExtension"));

        YamlMapping page = config.yamlMapping("page");
        pageConfig = new PageConfig();
        setPageConfig(pageConfig);
        pageConfig.setPageHeight(page.integer("pageHeight"));
        pageConfig.setPageWidth(page.integer("pageWidth"));
        pageConfig.setTopMargin(page.integer("topMargin"));
        pageConfig.setBottomMargin(page.integer("bottomMargin"));
        pageConfig.setLeftMargin(page.integer("leftMargin"));
        pageConfig.setRightMargin(page.integer("rightMargin"));
        pageConfig.setPageEnd(PrintUtils.stripQuotes(page.string("pageEnd")));
        pageConfig.setLineEnd(PrintUtils.stripQuotes(page.string("lineEnd")));
        pageConfig.setHeaderLine(PrintUtils.stripQuotes(page.string("headerLine")));
        pageConfig.setColumnSeparator(PrintUtils.stripQuotes(page.string("columnSeparator")));
        pageConfig.setHeaderSeparator(PrintUtils.stripQuotes(page.string("headerSeparator")));
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

}
