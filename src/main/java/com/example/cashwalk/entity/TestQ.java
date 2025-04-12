package com.example.cashwalk.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TestQ {
    @Id
    private Long id;
    private String name;
}
