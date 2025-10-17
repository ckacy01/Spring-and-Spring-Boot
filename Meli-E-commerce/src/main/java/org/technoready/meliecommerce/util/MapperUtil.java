package org.technoready.meliecommerce.util;

import org.technoready.meliecommerce.dto.ProductDTO;
import org.technoready.meliecommerce.entity.Product;

public class MapperUtil {

    public static ProductDTO toDTO(Product entity){
        new ProductDTO();
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
        new Product();
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .created_at(dto.getCreated_at())
                .active(dto.isActive())
                .build();
    }
}
