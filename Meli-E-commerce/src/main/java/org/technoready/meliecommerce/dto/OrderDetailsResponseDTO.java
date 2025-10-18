package org.technoready.meliecommerce.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDetailsResponseDTO {
    private Long productId;
    private String productName;
    private String descriptionSnap;
    private int quantity;
    private double unitPrice;
}

