package org.example.api.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Warehouse {
    private int warehouseId;
    private String name;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private int inventoryQuantity;
}
