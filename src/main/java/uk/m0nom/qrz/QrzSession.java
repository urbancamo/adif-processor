package uk.m0nom.qrz;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Session", namespace = QrzNamespace.URI)
public class QrzSession {
    private String key;
    private Integer count;
    private String SubExp;
    private String gmTime;
    private String remark;
    private String error;

    public String getKey() {
        return key;
    }

    @XmlElement(name="Key", namespace = QrzNamespace.URI)
    public void setKey(String key) {
        this.key = key;
    }

    public Integer getCount() {
        return count;
    }

    @XmlElement(name="Count", namespace = QrzNamespace.URI)
    public void setCount(Integer count) {
        this.count = count;
    }

    public String getSubExp() {
        return SubExp;
    }

    @XmlElement(name="SubExp", namespace = QrzNamespace.URI)
    public void setSubExp(String subExp) {
        SubExp = subExp;
    }

    public String getGmTime() {
        return gmTime;
    }

    @XmlElement(name="GMTime", namespace = QrzNamespace.URI)
    public void setGmTime(String gmTime) {
        this.gmTime = gmTime;
    }

    public String getRemark() {
        return remark;
    }

    @XmlElement(name="Remark", namespace = QrzNamespace.URI)
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getError() {
        return error;
    }

    @XmlElement(name="Error", namespace = QrzNamespace.URI)
    public void setError(String error) {
        this.error = error;
    }
}
