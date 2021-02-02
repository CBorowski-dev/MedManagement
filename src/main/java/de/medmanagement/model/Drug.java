package de.medmanagement.model;

import com.sun.istack.NotNull;
import org.hibernate.annotations.SortNatural;

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
    private boolean isOriginalDrug = false;
    @NotNull
    private int defaultPackageSize;
    @NotNull
    private String userName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id")
    @SortNatural
    @OrderBy("creationDate DESC")
    private List<DrugHistoryEntry> drugHistoryEntries = new ArrayList<DrugHistoryEntry>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id")
    @SortNatural
    @OrderBy("creationDate DESC")
    private List<DrugDoseHistoryEntry> drugDoseHistoryEntries = new ArrayList<DrugDoseHistoryEntry>();

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
        this.isOriginalDrug = isOriginalDrug;
        this.defaultPackageSize = defaultPackageSize;
        this.userName = userName;

        drugHistoryEntries.add(new DrugHistoryEntry(count, creationDate, comment));
        drugDoseHistoryEntries.add(new DrugDoseHistoryEntry(morningDose, noonDose, eveningDose, creationDate, "Initial dose"));
    }

    /**
     *
     * @return
     */
    private DrugDoseHistoryEntry getLatestDrugDoseHistoryEntry() {
        return drugDoseHistoryEntries.get(0);
    }

    /**
     *
     * @return
     */
    private int getConsumedDrugCount() {
        int consumedDrugCount = 0;
        DrugDoseHistoryEntry entryOld = null;
        // Initialization of entryNew is necessary for the case when there is only one
        // drug dose entry for the current drug. Then the for loop is not performed and
        // therefore the initialization for the entryNew as well.
        DrugDoseHistoryEntry entryNew = drugDoseHistoryEntries.get(0);

        for (int i = drugDoseHistoryEntries.size()-1; i>0; i--) {
            entryOld = drugDoseHistoryEntries.get(i);
            entryNew = drugDoseHistoryEntries.get(i-1);
            consumedDrugCount += calculateDrugConsumption(entryOld, entryOld.getCreationDate(), entryNew.getCreationDate());
        }
        consumedDrugCount += calculateDrugConsumption(entryNew, entryNew.getCreationDate(), new Date());

        return consumedDrugCount;
    }

    /**
     *
     * @param entry
     * @param oldDate
     * @param newDate
     * @return
     */
    private int calculateDrugConsumption(DrugDoseHistoryEntry entry, Date oldDate, Date newDate) {
        Duration duration = Duration.ofMillis(newDate.getTime() - oldDate.getTime());
        int dayCount = (int) duration.getSeconds()/60/60/24;
        return (int) (dayCount * (entry.getMorningDose() + entry.getEveningDose() + entry.getNoonDose()));
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
        return getLatestDrugDoseHistoryEntry().getMorningDose();
    }

    public void setMorningDose(float morningDose) {
        getLatestDrugDoseHistoryEntry().setMorningDose(morningDose);
    }

    public float getNoonDose() {
        return getLatestDrugDoseHistoryEntry().getNoonDose();
    }

    public void setNoonDose(float noonDose) {
        getLatestDrugDoseHistoryEntry().setNoonDose(noonDose);
    }

    public float getEveningDose() {
        return getLatestDrugDoseHistoryEntry().getEveningDose();
    }

    public void setEveningDose(float eveningDose) {
        getLatestDrugDoseHistoryEntry().setEveningDose(eveningDose);
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
        DrugDoseHistoryEntry entryNew = drugDoseHistoryEntries.get(0);
        float countPerDay = entryNew.getMorningDose() + entryNew.getNoonDose() + entryNew.getEveningDose();
        return (int) Math.floor((getTotalDrugCount() - getConsumedDrugCount()) / countPerDay);
    }

    /**
     * Provides the total drug count.
     * @return total drug count
     */
    private int getTotalDrugCount() {
        return drugHistoryEntries.stream().mapToInt(entry -> entry.getCount()).sum();
    }

    /**
     * Provides the current drug count.
     * @return
     */
    public float getCurrentDrugCount() {
        return getTotalDrugCount() - getConsumedDrugCount();
    }

    /**
     * Provides the date of the oldest history entry for this drug.
     * @return date of the oldest history entry
     */
    private Date getOldestCountUpdate() {
        return drugHistoryEntries.get(drugHistoryEntries.size()-1).getCreationDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Drug drug = (Drug) o;
        return name.equals(drug.name);
    }

}