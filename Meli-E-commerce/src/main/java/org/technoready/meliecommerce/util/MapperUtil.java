package org.technoready.meliecommerce.util;

import org.technoready.meliecommerce.dto.OrderDetailsResponseDTO;
import org.technoready.meliecommerce.dto.OrderResponseDTO;
import org.technoready.meliecommerce.dto.ProductDTO;
import org.technoready.meliecommerce.dto.UserDTO;
import org.technoready.meliecommerce.entity.Order;
import org.technoready.meliecommerce.entity.Product;
import org.technoready.meliecommerce.entity.User;

import java.util.List;

public class MapperUtil {

    public static ProductDTO toDTO(Product entity){
        return ProductDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .created_at(entity.getCreated_at())
                .active(entity.isActive())
                .build();
    }

    public static Product toEntity(ProductDTO dto){
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .created_at(dto.getCreated_at())
                .active(dto.isActive())
                .build();
    }

    public static UserDTO toDTO(User user){
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .createDate(user.getCreateDate())
                .active(user.isActive())
                .build();
    }

    public static User toEntity(UserDTO dto){
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .createDate(dto.getCreateDate())
                .active(dto.isActive())
                .build();
    }

    public static OrderResponseDTO toDTO(Order order){
        return OrderResponseDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .createdAt(order.getCreatedAt())
                .total(order.getTotal())
                .active(order.isActive())
                .details(order.getDetails().stream()
                        .map(d -> OrderDetailsResponseDTO.builder()
                                .productId(d.getProduct().getId())
                                .productName(d.getProductName())
                                .descriptionSnap(d.getDescriptionSnap())
                                .quantity(d.getQuantity())
                                .unitPrice(d.getUnitPrice())
                                .build())
                        .toList())
                .build();
    }

    public static List<OrderResponseDTO> toDTOList(List<Order> orders) {
        return orders.stream()
                .map(MapperUtil::toDTO)
                .toList();
    }

}
