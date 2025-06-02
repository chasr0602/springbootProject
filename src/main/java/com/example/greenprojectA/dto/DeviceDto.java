package com.example.greenprojectA.dto;

import com.example.greenprojectA.entity.Device;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceDto {
  private Integer idx;
  private String deviceCode;
  private Integer companyId;

  // Entity to Dto
  public static DeviceDto createDeviceDto(Optional<Device> optionalDevice) {
    return DeviceDto.builder()
            .idx(optionalDevice.get().getIdx())
            .deviceCode(optionalDevice.get().getDeviceCode())
            .companyId(optionalDevice.get().getCompanyId())
            .build();
  }
}
