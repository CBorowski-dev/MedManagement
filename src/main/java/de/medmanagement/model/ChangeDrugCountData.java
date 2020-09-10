package de.medmanagement.model;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ChangeDrugCountData {

    Integer id;
    String name;
    Float currentDrugCount;
    int count;
    String comment;

    public ChangeDrugCountData() {}

    public ChangeDrugCountData(Drug drug) {
        id = drug.getId();
        name = drug.getName();
        currentDrugCount = drug.getCurrentDrugCount();
    }
}
