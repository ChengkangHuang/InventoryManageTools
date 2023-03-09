package ca.mohawkcollege.ordersystem;

public class ItemRequest {
    private int itemId;
    private String itemName;
    private int itemImage;
    private boolean isEmpty;
    private int quantity;

    public ItemRequest() {
    }

    public ItemRequest(int itemId, String itemName, int itemImage) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemImage = itemImage;
    }

    public ItemRequest(int itemId, String itemName, int itemImage, boolean isEmpty, int quantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.isEmpty = isEmpty;
        this.quantity = quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemImage() {
        return itemImage;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}
