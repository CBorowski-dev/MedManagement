package de.medmanagement.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "drug_dose_history_entities")
public class DrugDoseHistoryEntry implements Comparable, Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @NotNull
    private float morningDose;
    @NotNull
    private float noonDose;
    @NotNull
    private float eveningDose;
    @NotNull
    private Date creationDate;

    private String comment;

    // Default constructor is needed for Hibernate
    public DrugDoseHistoryEntry() {}

    public DrugDoseHistoryEntry(float morningDose, float noonDose, float eveningDose, Date creationDate, String comment) {
        this.morningDose = morningDose;
        this.noonDose = noonDose;
        this.eveningDose = eveningDose;
        // Creation date ist normilized to format yyyy-MM-dd and hour, minute, etc. is set to 0.
        // This means that drug dose changes are assumed to occur at midnight, regadless when
        // the user inputs it into the system.
        this.creationDate = Utils.normilizeDate(creationDate);
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getMorningDose() {
        return morningDose;
    }

    public void setMorningDose(float morningDose) {
        this.morningDose = morningDose;
    }

    public float getNoonDose() {
        return noonDose;
    }

    public void setNoonDose(float noonDose) {
        this.noonDose = noonDose;
    }

    public float getEveningDose() {
        return eveningDose;
    }

    public void setEveningDose(float eveningDose) {
        this.eveningDose = eveningDose;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int compareTo(Object o) {
        if (this.creationDate.before(getCreationDate())) {
            return -1;
        } else if (this.creationDate.after(getCreationDate())) {
            return 1;
        } else {
            return 0;
        }
    }
}
