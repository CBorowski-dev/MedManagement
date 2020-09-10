package de.medmanagement.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class OrderItem {
    Integer drugId = -1;
    String drugName = "Platzhalter";
    int numberOfPacks = 1;
    int packSize = 100;
    boolean isOriginalDrug = false;
    float currentDrugCount;

    public OrderItem() {}

    public OrderItem(Drug drug) {
        drugId = drug.getId();
        drugName = drug.getName();
        packSize = drug.getDefaultPackageSize();
        isOriginalDrug = drug.isOriginalDrug();
        currentDrugCount = drug.getCurrentDrugCount();
    }

}
