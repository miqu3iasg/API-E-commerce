package com.application.API_E_commerce.common.utils.mappers;

import com.application.API_E_commerce.adapters.outbound.entities.order.JpaOrderItemEntity;
import com.application.API_E_commerce.domain.order.orderitem.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface OrderItemMapper {

	OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

	@Named("orderItemsToJpa")
	default List<JpaOrderItemEntity> orderItemsToJpa (List<OrderItem> items) {
		return items == null
				? null
				: items.stream().map(OrderItemMapper.INSTANCE::toJpa).collect(Collectors.toList());
	}

	@Mappings({
			@Mapping(source = "id", target = "id"),
			@Mapping(source = "order", target = "order", ignore = true), // Evitar loop bidirecional
			@Mapping(source = "product", target = "product", qualifiedByName = "productToJpa"),
			@Mapping(source = "quantity", target = "quantity"),
			@Mapping(source = "unitPrice", target = "unitPrice")
	})
	JpaOrderItemEntity toJpa (OrderItem domain);

	@Named("orderItemsToDomain")
	default List<OrderItem> orderItemsToDomain (List<JpaOrderItemEntity> items) {
		return items == null
				? null
				: items.stream().map(OrderItemMapper.INSTANCE::toDomain).collect(Collectors.toList());
	}

	@Mappings({
			@Mapping(source = "id", target = "id"),
			@Mapping(source = "order", target = "order", ignore = true),
			@Mapping(source = "product", target = "product", qualifiedByName = "productToDomain"),
			@Mapping(source = "quantity", target = "quantity"),
			@Mapping(source = "unitPrice", target = "unitPrice")
	})
	OrderItem toDomain (JpaOrderItemEntity jpa);

}
