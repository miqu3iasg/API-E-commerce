package com.application.API_E_commerce.adapters.outbound.entities.address;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tb_addresses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JpaAddressEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String street;

  private String city;

  private String state;

  private String zipCode;

  private String country;
}
