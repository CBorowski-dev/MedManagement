package de.medmanagement.model;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class DrugDTO {

    Integer id;
    String name;
    float morningDose;
    float noonDose;
    float eveningDose;
    int count;
    boolean isOriginalDrug = false;
    int defaultPackageSize;
    String comment;

    public DrugDTO() {}

    public DrugDTO(Drug drug) {
        id = drug.getId();
        name = drug.getName();
        morningDose = drug.getMorningDose();
        noonDose = drug.getNoonDose();
        eveningDose = drug.getEveningDose();
        isOriginalDrug = drug.isOriginalDrug();
        defaultPackageSize = drug.getDefaultPackageSize();
    }

}
