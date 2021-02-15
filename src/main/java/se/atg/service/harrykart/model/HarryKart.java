package se.atg.service.harrykart.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

/**
 * Class to represent the elements of a Harry Kart race: Participants, Loops, and power-ups
 */
@JacksonXmlRootElement(localName = "harryKart")
public class HarryKart implements Serializable {
    int numberOfLoops;
    @JacksonXmlProperty(localName = "startList")
    List<Participant> participants;
    @JacksonXmlProperty(localName = "powerUps")
    List<Loop> loops;

    public HarryKart() {
    }


    public HarryKart(int numberOfLoops, List<Participant> participants, List<Loop> loops) {
        this.numberOfLoops = numberOfLoops;
        this.participants = participants;
        this.loops = loops;
    }


    public int getNumberOfLoops() {
        return numberOfLoops;
    }


    public List<Participant> getParticipants() {
        return participants;
    }


    public List<Loop> getLoops() {
        return loops;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
