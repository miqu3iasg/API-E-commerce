package com.application.API_E_commerce.utils.mappers;

import com.application.API_E_commerce.adapters.outbound.entities.order.JpaOrderEntity;
import com.application.API_E_commerce.adapters.outbound.entities.user.JpaUserEntity;
import com.application.API_E_commerce.domain.order.Order;
import com.application.API_E_commerce.domain.user.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = { UserMapper.class, OrderItemMapper.class, PaymentMapper.class })
public interface OrderMapper {

  OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

  @Mappings({
          @Mapping(source = "id", target = "id"),
          @Mapping(source = "user", target = "user", qualifiedByName = "userToJpa"),
          @Mapping(source = "status", target = "status"),
          @Mapping(source = "orderDate", target = "orderDate"),
          @Mapping(source = "totalValue", target = "totalValue"),
          @Mapping(source = "currency", target = "currency"),
          @Mapping(source = "description", target = "description"),
          @Mapping(source = "items", target = "items", qualifiedByName = "orderItemsToJpa"),
          @Mapping(source = "payment", target = "payment", qualifiedByName = "paymentToJpa"),
  })
  JpaOrderEntity toJpa(Order domain);

  @Mappings({
          @Mapping(source = "id", target = "id"),
          @Mapping(source = "user", target = "user", qualifiedByName = "userToDomain"),
          @Mapping(source = "status", target = "status"),
          @Mapping(source = "orderDate", target = "orderDate"),
          @Mapping(source = "totalValue", target = "totalValue"),
          @Mapping(source = "currency", target = "currency"),
          @Mapping(source = "description", target = "description"),
          @Mapping(source = "items", target = "items", qualifiedByName = "orderItemsToDomain"),
          @Mapping(source = "payment", target = "payment", qualifiedByName = "paymentToDomain"),
  })
  Order toDomain(JpaOrderEntity jpa);

  @Named("userToJpa")
  default JpaUserEntity userToJpa(User user) {
    return user == null ? null : UserMapper.INSTANCE.toJpa(user);
  }

  @Named("userToDomain")
  default User userToDomain(JpaUserEntity jpaUser) {
    return jpaUser == null ? null : UserMapper.INSTANCE.toDomain(jpaUser);
  }
}
