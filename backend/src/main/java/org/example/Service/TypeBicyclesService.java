package org.example.Service;

import org.example.Model.TypeBicycle;
import org.example.Repository.TypeBicyclesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TypeBicyclesService {
    @Autowired
    private TypeBicyclesRepository typeBicyclesRepository;

    public Optional<TypeBicycle> getTypeBicycleByID(Integer id_bicycle) {
        Optional<TypeBicycle> typeBicycle = typeBicyclesRepository.findById(id_bicycle);
        if (typeBicycle.isPresent()) {
            return typeBicycle;
        } else {
            throw new RuntimeException("TypeBrakes not found");
        }
    }
}
