package uk.m0nom.adifproc.dxcc;

import lombok.Data;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Data
public class DxccEntity  {
    private final DateTimeFormatter dxccDateFormatter =  DateTimeFormatter.ofPattern("uuuu-MM-dd");

    private JsonDxccEntity jsonEntity;

    private Collection<String> prefixes;

    private ZonedDateTime validStartDate;
    private ZonedDateTime validEndDate;

    public DxccEntity(JsonDxccEntity jsonEntity) throws ParseException {
        this.jsonEntity = jsonEntity;
        setValidStartDate(parseDxccDateString(jsonEntity.getValidStart()));
        setValidEndDate(parseDxccDateString(jsonEntity.getValidEnd()));

    }

    private ZonedDateTime parseDxccDateString(String dateString) throws ParseException {
        ZonedDateTime date = null;
        if (!"".equals(dateString)) {
            date = ZonedDateTime.of(LocalDate.parse(dateString, dxccDateFormatter), LocalTime.of(0,0), ZoneOffset.UTC);
        }
        return date;
    }

    public int getEntityCode() { return jsonEntity.getEntityCode(); }

    public String getPrefixRegex() { return jsonEntity.getPrefixRegex(); }
    public String getPrefix() { return jsonEntity.getPrefix(); }
    public String getName() { return jsonEntity.getName();}
    public String getCountryCode() { return jsonEntity.getCountryCode();}
    public String getFlag() { return jsonEntity.getFlag();}

    public Collection<Integer> getItu() { return jsonEntity.getItu(); }
    public Collection<Integer> getCq() { return jsonEntity.getCq(); }
    public boolean hasValidStart() { return validStartDate != null; }
    public boolean hasValidEnd() { return validEndDate != null; }

    public boolean isValidForDate(ZonedDateTime date) {
        boolean valid = false;

        valid = !hasValidStart() || date.isAfter(getValidStartDate());
        valid &= !hasValidEnd() || date.isBefore(getValidEndDate());

        return valid;
    }
}
