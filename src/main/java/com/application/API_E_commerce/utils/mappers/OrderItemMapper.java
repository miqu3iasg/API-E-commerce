package com.application.API_E_commerce.utils.mappers;

import com.application.API_E_commerce.adapters.outbound.entities.order.JpaOrderItemEntity;
import com.application.API_E_commerce.adapters.outbound.entities.product.JpaProductEntity;
import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import com.application.API_E_commerce.domain.product.Product;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface OrderItemMapper {

  OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

  @Mappings({
          @Mapping(source = "id", target = "id"),
          @Mapping(source = "order", target = "order", ignore = true), // Evitar loop bidirecional
          @Mapping(source = "product", target = "product", qualifiedByName = "productToJpa"),
          @Mapping(source = "quantity", target = "quantity"),
          @Mapping(source = "unitPrice", target = "unitPrice")
  })
  JpaOrderItemEntity toJpa(OrderItem domain);


  @Mappings({
          @Mapping(source = "id", target = "id"),
          @Mapping(source = "order", target = "order", ignore = true),
          @Mapping(source = "product", target = "product", qualifiedByName = "productToDomain"),
          @Mapping(source = "quantity", target = "quantity"),
          @Mapping(source = "unitPrice", target = "unitPrice")
  })
  OrderItem toDomain(JpaOrderItemEntity jpa);

  @Named("orderItemsToJpa")
  default List<JpaOrderItemEntity> orderItemsToJpa(List<OrderItem> items) {
    return items == null
            ? null
            : items.stream().map(OrderItemMapper.INSTANCE::toJpa).toList();
  }

  @Named("orderItemsToDomain")
  default List<OrderItem> orderItemsToDomain(List<JpaOrderItemEntity> items) {
    return items == null
            ? null
            : items.stream().map(OrderItemMapper.INSTANCE::toDomain).toList();
  }
}
