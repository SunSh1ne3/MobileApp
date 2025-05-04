package org.example.Controller;

import org.example.DTO.Response.ErrorResponse;
import org.example.Model.*;
import org.example.Service.BicycleService;
import org.example.Service.TypeBicyclesService;
import org.example.Service.TypeBrakesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/bicycle")
public class BicycleController {
    @Autowired
    private BicycleService bicycleService;
    @Autowired
    private TypeBrakesService typeBrakesService;
    @Autowired
    private TypeBicyclesService typeBicyclesService;

    @GetMapping
    public ResponseEntity<List<Bicycle>> getAllBicycles() {
        List<Bicycle> bicycles = bicycleService.getAllBicyclesWithImages();
        return ResponseEntity.ok(bicycles);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getBicycleByID(@PathVariable Integer id) {
        try {
            Optional<Bicycle> bicycle = bicycleService.getBicycleByID(id);
            if (bicycle.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Bicycle not found"));
            }
            return ResponseEntity.ok(bicycle.get());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Object> getBicycleByName(@PathVariable String name) {
        try {
            Optional<Bicycle> bicycle = bicycleService.getBicycle(name);
            if (bicycle.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Bicycle not found"));
            }
            return ResponseEntity.ok(bicycle.get());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/parameters/type/{id_type}")
    public ResponseEntity<Object> getTypeBicycle(@PathVariable Integer id_type) {
        try {
            Optional<TypeBicycle> typeBicycle= typeBicyclesService.getTypeBicycleByID(id_type);
            if (typeBicycle.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("TypeBicycle not found"));
            }
            return ResponseEntity.ok(typeBicycle.get());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/parameters/brakes/{id_brake}")
    public ResponseEntity<Object> getTypeBrake(@PathVariable Integer id_brake) {
        try {
            Optional<TypeBrakes> typeBrakes = typeBrakesService.getTypeBrakeByID(id_brake);
            if (typeBrakes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("TypeBrakes not found"));
            }
            return ResponseEntity.ok(typeBrakes.get());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Object> addBicycle(@RequestBody Bicycle bicycleData) {
        try {
            Bicycle bicycle = bicycleService.addBicycle(bicycleData);
            return ResponseEntity.ok(bicycle);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Object> addImageToBicycle(@PathVariable Integer id, @RequestBody BicycleImage bicycleImage) {
        try {
            Bicycle bicycle = bicycleService.addImageToBicycle(id, bicycleImage);
            return ResponseEntity.status(HttpStatus.OK).body(bicycle);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/images")
    public ResponseEntity<Object> deleteAllImages(){
        try {
            bicycleService.deleteImages();
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred: " + e.getMessage()));
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBicycle(@PathVariable Integer id){
        try{
            bicycleService.deleteBicycleById(id);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
