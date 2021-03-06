package com.usian.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CatNode {
    @JsonProperty("n")
    private String name;
    @JsonProperty("i")
    private List<?> item;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<?> getItem() {
        return item;
    }

    public void setItem(List<?> item) {
        this.item = item;
    }

    public CatNode(String name, List<?> item) {
        this.name = name;
        this.item = item;
    }

    public CatNode() {
    }
}
