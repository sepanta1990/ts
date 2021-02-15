package se.atg.service.harrykart.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
/**
 * @author Ali Fathizadeh 2021-02-15
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ranking {

    @JsonProperty("position")
    private Integer position;
    @JsonProperty("horse")
    private String horse;

    public Ranking() {
    }

    public Ranking(Integer position, String horse) {
        this.position = position;
        this.horse = horse;
    }

    public Integer getPosition() {
        return position;
    }


    public void setPosition(Integer position) {
        this.position = position;
    }


    public String getHorse() {
        return horse;
    }


    public void setHorse(String horse) {
        this.horse = horse;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}