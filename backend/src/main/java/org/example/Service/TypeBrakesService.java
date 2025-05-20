package org.example.Service;

import org.example.Model.TypeBicycle;
import org.example.Model.TypeBrakes;
import org.example.Repository.TypeBrakesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypeBrakesService {
    @Autowired
    private TypeBrakesRepository typeBrakesRepository;

    public Optional<TypeBrakes> getTypeBrakeByID(Integer id_brake) {
        Optional<TypeBrakes> typeBrakes = typeBrakesRepository.findById(id_brake);
        if (typeBrakes.isPresent()) {
            return typeBrakes;
        } else {
            throw new RuntimeException("TypeBrakes not found");
        }
    }

    public List<TypeBrakes> getTypesBrakes() {
        List<TypeBrakes> typesBicycle = typeBrakesRepository.findAll();
        if (!typesBicycle.isEmpty()) {
            return typesBicycle;
        } else {
            throw new RuntimeException("TypesBrakes not found");
        }
    }
}
