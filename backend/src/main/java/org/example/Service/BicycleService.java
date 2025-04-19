package org.example.Service;

import org.example.Model.Bicycle;
import org.example.Repository.BicycleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BicycleService {
    @Autowired
    private BicycleRepository bicycleRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    public List<Bicycle> findAllBicycle() {
        return bicycleRepository.findAll();
    }

    public Optional<Bicycle> getBicycle(String name) {
        try {
            Optional<Bicycle> bicycle = bicycleRepository.findByName(name);
            if (bicycle.isEmpty()) {
                return Optional.empty();
            }
            return bicycle;
        } catch (Exception e) {
            logger.error("Error during bicycle found: {}", e.getMessage(), e);
            throw e;
        }
    }


    public Bicycle addBicycle(Bicycle bicycleData) {
        try {
            if (bicycleRepository.findByName(bicycleData.getName()).isPresent()) {
                throw new IllegalArgumentException("This bicycle already exists");
            }

            Bicycle newBicycle = new Bicycle();
            newBicycle.setName(bicycleData.getName());

            if(bicycleData.getWeight()!=null)
                newBicycle.setWeight(bicycleData.getWeight());
            if(bicycleData.getFrameMaterial()!=null)
                newBicycle.setFrameMaterial(bicycleData.getFrameMaterial());
            if(bicycleData.getWheelSize()!=null)
                newBicycle.setWheelSize(bicycleData.getWheelSize());

            if(bicycleData.getTypeBrakes()!=null)
                newBicycle.setTypeBrakes(bicycleData.getTypeBrakes());
            if(bicycleData.getTypeBicycle()!=null)
                newBicycle.setTypeBicycle(bicycleData.getTypeBicycle());
            if(bicycleData.getAge()!=null)
                newBicycle.setAge(bicycleData.getAge());

            if(bicycleData.getNumberSpeeds()!=null)
                newBicycle.setNumberSpeeds(bicycleData.getNumberSpeeds());
            if(bicycleData.getMaximumLoad()!=null)
                newBicycle.setMaximumLoad(bicycleData.getMaximumLoad());

            return bicycleRepository.save(newBicycle);
        } catch (Exception e) {
            logger.error("Error during bicycle added: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void deleteBicycleById(Long id){
        if (bicycleRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Not found user with ID: " + id);
        }
        bicycleRepository.deleteById(id);
    }

}
