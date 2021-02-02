package de.medmanagement.model;

import java.util.ArrayList;

public class UserData {
    private ArrayList<Drug> drugList = new ArrayList<Drug>();

    private Order order = new Order();

    public ArrayList<Drug> getDrugList() {
        return new ArrayList(drugList);
    }

    public boolean containsDrug(Drug drug) {
        return drugList.contains(drug);
    }

    public void addDrug(Drug drug) {
        drugList.add(drug);
    }

    // ToDo: Löschen
    public Drug getDrug(String drugName) {
        return drugList.stream().filter(drug -> drug.getName().equals(drugName)).findFirst().orElse(null);
    }

    public Drug getDrug(Integer drugId) {
        return drugList.stream().filter(drug -> drug.getId().equals(drugId)).findFirst().orElse(null);
    }

    public boolean removeDrug(Drug drug) {
        return drugList.remove(drug);
    }

    public Order getOrder() {
        return order;
    }

    public void clearOrder() {
        order.clearData();
    }
}
