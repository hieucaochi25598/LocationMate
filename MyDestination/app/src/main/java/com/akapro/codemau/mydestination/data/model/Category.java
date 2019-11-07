package com.akapro.codemau.mydestination.data.model;

public class Category {
    private String categoryID;
    private String categoryName;

    public String getCategoryID() {
        return categoryID;
    }

    public Category(String categoryID, String categoryName) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
