package uk.m0nom.adifproc.qrz;


import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="QRZDatabase", namespace = QrzNamespace.URI)
public class QrzDatabase {
    private String version;
    private QrzSession session;
    private QrzCallsign callsign;

    public String getVersion() {
        return version;
    }

    @XmlAttribute(name="version")
    public void setVersion(String version) {
        this.version = version;
    }

    public QrzSession getSession() {
        return session;
    }

    @XmlElement(name="Session", namespace = QrzNamespace.URI)
    public void setSession(QrzSession session) {
        this.session = session;
    }

    public QrzCallsign getCallsign() {
        return callsign;
    }

    @XmlElement(name="Callsign", namespace = QrzNamespace.URI)
    public void setCallsign(QrzCallsign callsign) {
        this.callsign = callsign;
    }
}
