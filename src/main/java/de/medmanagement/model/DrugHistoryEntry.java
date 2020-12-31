package de.medmanagement.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "drug_history_entities")
public class DrugHistoryEntry implements Comparable, Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @NotNull
    private int count;
    @NotNull
    private Date creationDate;

    private String comment;

    // Default constructor is needed for Hibernate
    public DrugHistoryEntry() {}

    public DrugHistoryEntry(int count, Date creationDate, String comment) {
        this.count = count;
        this.creationDate = Utils.normilizeDate(creationDate);
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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
