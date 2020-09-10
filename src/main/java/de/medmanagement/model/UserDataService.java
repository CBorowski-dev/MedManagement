package de.medmanagement.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserDataService {

    @Autowired
    private DrugRepository drugRepository;

    // Map with UserData objects which contains the drugs and orders for the current users.
    private HashMap<String, UserData> dataMap = new HashMap<String, UserData>();

    /**
     * Default-Konstruktor
     */
    public UserDataService() {
    }

    /**
     *
     * @param userName
     * @return
     */
    private UserData getUserData(String userName) {
        Object value = dataMap.get(userName);
        if (value != null) {
            return (UserData) value;
        } else {
            UserData ud = new UserData();
            dataMap.put(userName, ud);
            return ud;
        }
    }

    /**
     *
     * @param drugData
     * @param userName
     */
    public void createDrug(DrugDTO drugData, String userName) {
        Drug drug = new Drug(drugData.getName(),
                drugData.getCount(),
                new Date(),
                drugData.getMorningDose(),
                drugData.getNoonDose(),
                drugData.getEveningDose(),
                drugData.isOriginalDrug(),
                drugData.getDefaultPackageSize(),
                drugData.getComment(),
                userName);
        UserData userData = getUserData(userName);
        if (!userData.containsDrug(drug)) {
            userData.addDrug(drug);
//            drugRepository.save(drug);
        }
    }

    /**
     *
     * @param drugId
     * @return
     */
    public boolean deleteDrug(Integer drugId, String userName) {
        if (drugId != null) {
            UserData userData = getUserData(userName);
            Drug drug = userData.getDrug(drugId);
            drugRepository.deleteById(drugId);
            return userData.removeDrug(drug);
        }
        return false;
    }

    /**
     *
     * @param drugName
     * @param userName
     * @return
     */
    public boolean deleteDrug(String drugName, String userName) {
        if (drugName != null) {
            UserData userData = getUserData(userName);
            Drug drug = userData.getDrug(drugName);
            drugRepository.deleteById(drug.getId());
            return userData.removeDrug(drug);
        }
        return false;
    }

    /**
     *
     * @param userName
     */
    public boolean deleteAllDrugs(String userName) {
        if (userName != null) {
            List<Drug> userDrugs = drugRepository.findAllByUserName(userName);
            UserData userData = getUserData(userName);
            for (Drug drug : userDrugs) {
                drugRepository.deleteById(drug.getId());
                userData.removeDrug(drug);
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @param userName
     * @return
     */
    public ArrayList<Drug> getDrugs(String userName) {
        UserData userData = getUserData(userName);
        if (userData.getDrugList().size() == 0) {
            // Initial fetch data from database
            Iterator<Drug> iter = drugRepository.findAllByUserName(userName).iterator();
            while (iter.hasNext()) {
                userData.addDrug(iter.next());
            }
        }
        return userData.getDrugList();
    }

    /**
     *
     * @param drugId
     * @param count
     * @param comment
     * @param userName
     * @return
     */
    public boolean changeDrugCount(Integer drugId, int count, String comment, String userName) {
        if (drugId != null) {
            UserData userData = getUserData(userName);
            Drug drug = userData.getDrug(drugId);
            DrugHistoryEntry entry = new DrugHistoryEntry(count, new Date(), comment);
            drug.addHistoryEntry(entry);
            drugRepository.save(drug);

            return true;
        }
        return false;
    }

    /**
     *
     * @param drugName
     * @return
     */
    public boolean containsDrug(String drugName, String userName) {
        UserData userData = getUserData(userName);
        if (userData.getDrug(drugName) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param drugId
     * @return
     */
    public Drug getDrug(Integer drugId, String userName) {
        if (drugId != null) {
            UserData userData = getUserData(userName);
            return userData.getDrug(drugId);
        }
        return null;
    }

    /**
     *
     * @param drugData
     * @param userName
     */
    public void updateDrug(DrugDTO drugData, String userName) {
        UserData userData = getUserData(userName);
        Drug drug = userData.getDrug(drugData.getId());
        if (drug != null) {
            drug.setName(drugData.getName());
            drug.setMorningDose(drugData.getMorningDose());
            drug.setNoonDose(drugData.getNoonDose());
            drug.setEveningDose(drugData.getEveningDose());
            drug.setOriginalDrug(drugData.isOriginalDrug());
            drug.setDefaultPackageSize(drugData.getDefaultPackageSize());

            drugRepository.save(drug);
        }
    }

    /**
     *
     * @param drugId
     * @param userName
     * @return
     */
    public OrderItem getNewOrderItem(Integer drugId, String userName) {
        if (drugId != null) {
            UserData userData = getUserData(userName);
            Drug drug = userData.getDrug(drugId);
            return new OrderItem(drug);
        }
        return null;
    }

    /**
     *
     * @param userName
     */
    public void clearData(String userName) {
        dataMap.remove(userName);
    }

    /**
     *
     * @param userName
     */
    public void getClearOrder(String userName) {
        UserData userData = getUserData(userName);
        userData.clearOrder();
    }

    /**
     *
     * @param userName
     * @return
     */
    public Object getOrderItems(String userName) {
        UserData userData = getUserData(userName);
        return userData.getOrder().getOrderItems();
    }

    /**
     *
     * @param isOriginalDrug
     * @param userName
     * @return
     */
    public Object getOrderItems(boolean isOriginalDrug, String userName) {
        UserData userData = getUserData(userName);
        return userData.getOrder().getOrderItems(isOriginalDrug);
    }

    /**
     *
     * @param orderItem Order item which should be added to the order.
     * @param userName Name of the user, for which the order item should be added to the order.
     */
    public void addOrderItem(OrderItem orderItem, String userName) {
        UserData userData = getUserData(userName);
        userData.getOrder().addOrderItem(orderItem);
    }

    /**
     * Deletes the order item associated with the drugId and the userName.
     * @param drugId ID of the drug associated with the order item to be deleted.
     * @param userName Name of the user, for which the order item should be deleted from the order.
     */
    public void deleteOrderItem(Integer drugId, String userName) {
        UserData userData = getUserData(userName);
        userData.getOrder().deleteOrderItem(drugId);
    }

    /**
     *
     * @param drugId
     * @param userName
     * @return
     */
    public DrugDTO getDrugData(Integer drugId, String userName) {
        if (drugId != null) {
            UserData userData = getUserData(userName);
            Drug drug = userData.getDrug(drugId);
            return new DrugDTO(drug);
        }
        return null;
    }

}