package org.technoready.meliecommerce.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponseDTO {
    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private double total;
    private List<OrderDetailsResponseDTO> details;
}
