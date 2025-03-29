package org.example.Controller;

import org.example.Service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    @GetMapping("/databases")
    public ResponseEntity<List<String>> listDatabases() {
        try {
            List<String> databases = databaseService.getAllDatabases();
            return ResponseEntity.ok(databases);
        } catch (Exception e) {
            e.printStackTrace(); // Выводим стек вызовов в консоль
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
