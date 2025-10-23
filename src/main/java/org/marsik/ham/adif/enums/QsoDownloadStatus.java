package org.marsik.ham.adif.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum QsoDownloadStatus implements AdifEnumCode {
    DOWNLOADED("Y"),
    NOT_DOWNLOADED("N"),
    IGNORE("I");

    private final String code;

    QsoDownloadStatus(String code) {
        this.code = code;
    }

    @Override
    public String adifCode() {
        return code;
    }

    private final static Map<String, QsoDownloadStatus> reverse = new HashMap<>();

    static {
        Stream.of(values()).forEach(v -> reverse.put(v.adifCode(), v));
    }

    public static QsoDownloadStatus findByCode(String code) {
        return reverse.get(code.toUpperCase());
    }
}
