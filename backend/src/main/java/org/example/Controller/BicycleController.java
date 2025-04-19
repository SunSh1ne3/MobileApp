package org.example.Controller;

import org.example.DTO.Response.ErrorResponse;
import org.example.Model.Bicycle;
import org.example.Service.BicycleService;
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

    @GetMapping
    public ResponseEntity<List<Bicycle>> getAllBicycles() {
        List<Bicycle> bicycles = bicycleService.findAllBicycle();
        return ResponseEntity.ok(bicycles);
    }

    @GetMapping("/{name}")
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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBicycle(@PathVariable Long id){
        try{
            bicycleService.deleteBicycleById(id);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
