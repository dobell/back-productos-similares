/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dobell.similarproducts.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bean entidad para los datos de producto
 * @author dobell
 */
public class ProductDetail {
    private String id;
    private String name;
    private double price;
    @JsonProperty("availability")
    private boolean available;

    // Constructors
    public ProductDetail() {}

    public ProductDetail(String id, String name, double price, boolean available) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.available = available;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}