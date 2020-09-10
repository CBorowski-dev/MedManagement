package de.medmanagement.model;

import com.sun.istack.NotNull;

import javax.persistence.*;

import java.time.Duration;
import java.util.*;

/**
 * Stellt das konkrete Medikament dar. Inkl. der Einnahmemenge,
 * Anzahl Tabletten und dem zugehörigen Datum der letzten Zählung.
 */
@Entity // This tells Hibernate to make a table out of this class
@Table(name = "drugs")
public class Drug {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @NotNull
    @Column(unique = true)
    private String name;
    @NotNull
    private float morningDose;
    @NotNull
    private float noonDose;
    @NotNull
    private float eveningDose;
    @NotNull
    private boolean isOriginalDrug = false;
    @NotNull
    private int defaultPackageSize;
    @NotNull
    private String userName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id")
    private Set<DrugHistoryEntry> drugHistoryEntries = new HashSet<DrugHistoryEntry>();

    // Default constructor is needed for Hibernate
    public Drug() {}

    /**
     *
     * @param name Drug name
     * @param count Initial drug count
     * @param creationDate
     * @param morningDose Morning dose
     * @param noonDose Noon dose
     * @param eveningDose Evening dose
     * @param comment Comment
     * @param userName User name
     */
    public Drug(String name, int count, Date creationDate, float morningDose, float noonDose, float eveningDose, boolean isOriginalDrug, int defaultPackageSize, String comment, String userName) {
        this.name = name;
        this.morningDose = morningDose;
        this.noonDose = noonDose;
        this.eveningDose = eveningDose;
        this.isOriginalDrug = isOriginalDrug;
        this.defaultPackageSize = defaultPackageSize;
        this.userName = userName;

        drugHistoryEntries.add(new DrugHistoryEntry(count, creationDate, comment));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isOriginalDrug() {
        return isOriginalDrug;
    }

    public void setOriginalDrug(boolean originalDrug) {
        isOriginalDrug = originalDrug;
    }

    public int getDefaultPackageSize() {
        return defaultPackageSize;
    }

    public void setDefaultPackageSize(int defaultPackageSize) {
        this.defaultPackageSize = defaultPackageSize;
    }

    /**
     * Adds a history entry for this drug.
     * @param entry
     */
    public void addHistoryEntry(DrugHistoryEntry entry) {
        drugHistoryEntries.add(entry);
    }

    /**
     * Provides the day count since the first logged history entry for this drug.
     * @return day count
     */
    public int getDaysOff() {
        Date currentDate = new Date();
        Duration duration = Duration.ofMillis(currentDate.getTime() - getOldestCountUpdate().getTime());

        // Day count since first logged history entry for this drug
        return (int) duration.getSeconds()/60/60/24;
    }

    /**
     * Provides the day count the drug will available.
     * @return day count
     */
    public int getDaysLeft() {
        float countPerDay = morningDose + noonDose + eveningDose;
        return (int) Math.floor(getTotalDrugCount() / countPerDay) - getDaysOff();
    }

    /**
     * Provides the total drug count.
     * @return total drug count
     */
    private int getTotalDrugCount() {
        int count = 0;
        for (DrugHistoryEntry entry : drugHistoryEntries) {
            count = count + entry.getCount();
        }
        return count;
    }

    /**
     * Provides the current drug count.
     * @return
     */
    public float getCurrentDrugCount() {
        float countPerDay = morningDose + noonDose + eveningDose;
        return getTotalDrugCount() - ( getDaysOff() * countPerDay );
    }

    /**
     * Provides the date of the oldest history entry for this drug.
     * @return date of the oldest history entry
     */
    private Date getOldestCountUpdate() {
        Date oldestUpdate = null;
        for (DrugHistoryEntry entry : drugHistoryEntries) {
            if (oldestUpdate == null || oldestUpdate.after(entry.getCreationDate())) {
                oldestUpdate = entry.getCreationDate();
            }
        }
        return oldestUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Drug drug = (Drug) o;
        return name.equals(drug.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, morningDose, noonDose, eveningDose);
    }

    @Override
    public String toString() {
        StringBuilder x = new StringBuilder();

        x.append(name);
        x.append(' ');
        x.append(morningDose);
        x.append('|');
        x.append(noonDose);
        x.append('|');
        x.append(eveningDose);
        x.append(" : ");
        x.append(getDaysOff());
        x.append(" -> ");
        x.append(getDaysLeft());

        return x.toString();
    }

}