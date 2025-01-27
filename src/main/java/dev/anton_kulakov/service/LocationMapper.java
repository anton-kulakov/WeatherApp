package dev.anton_kulakov.service;

import dev.anton_kulakov.dto.LocationResponseDto;
import dev.anton_kulakov.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "latitude", target = "latitude")
    @Mapping(source = "longitude", target = "longitude")
    Location toLocation(LocationResponseDto locationResponseDto);
}
