package de.medmanagement.model;

import java.util.ArrayList;

public class Order {

    private ArrayList<OrderItem> orderItemList = new ArrayList<OrderItem>();

    /**
     *
     * @param orderItem
     */
    public void addOrderItem(OrderItem orderItem) {
        if (!orderItemList.contains(orderItem)) {
            orderItemList.add(orderItem);
        }
    }

    /**
     *
     * @return
     */
    public ArrayList<OrderItem> getOrderItems() {
        return orderItemList;
    }

    /**
     *
     * @param isOriginalDrug
     * @return
     */
    public ArrayList<OrderItem> getOrderItems(boolean isOriginalDrug) {
        ArrayList<OrderItem> selectedOrderItems = new ArrayList<OrderItem>();

        orderItemList.forEach(orderItem -> {
            if (orderItem.isOriginalDrug() == isOriginalDrug) {
                selectedOrderItems.add(orderItem);
            }
        });

        return selectedOrderItems;
    }

    /**
     *
     * @param drugId
     * @return
     */
    public boolean deleteOrderItem(Integer drugId) {
        if (drugId != null) {
            for (OrderItem orderItem : orderItemList) {
                if (orderItem.getDrugId().equals(drugId)) {
                    return orderItemList.remove(orderItem);
                }
            }
        }
        return false;
    }

    /**
     *
     */
    public void clearData() {
        orderItemList.clear();
    }
}
