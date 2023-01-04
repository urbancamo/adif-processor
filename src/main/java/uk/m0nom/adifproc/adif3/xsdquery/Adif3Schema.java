package uk.m0nom.adifproc.adif3.xsdquery;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class Adif3Schema {
    private Map<String, Adif3Field> fields = new HashMap<>();
    private Map<String, Adif3Type> types = new HashMap<>();

    public void setTypes(Set<Adif3Type> typeSet) {
        for (Adif3Type type : typeSet) {
            types.put(type.getName(), type);
        }
    }

    public void setFields(Set<Adif3Field> fieldSet) {
        for (Adif3Field field : fieldSet) {
            fields.put(field.getName(), field);
        }
    }

    public void addType(Adif3Type type) {
        types.put(type.getName(), type);
    }

    public Adif3Type getType(String typeName) { return types.get(typeName); }
    public Adif3Field getField(String fieldName) { return fields.get(fieldName); }
}
