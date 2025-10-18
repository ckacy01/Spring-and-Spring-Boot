package org.technoready.meliecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class OrderDetailsDTO {
    private Long productId;
    private int quantity;
}
