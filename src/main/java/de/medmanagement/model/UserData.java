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
        for (Drug drug : drugList) {
            if (drug.getName().equals(drugName)) {
                return drug;
            }
        }
        return null;
    }

    public Drug getDrug(Integer drugId) {
        for (Drug drug : drugList) {
            if (drug.getId().equals(drugId)) {
                return drug;
            }
        }
        return null;
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
