package com.inmohouse.backend.backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.inmohouse.backend.backend.entities.User;
import com.inmohouse.backend.backend.services.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private UserService service;
   
    @GetMapping
    public List<User> list() {
        return this.service.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<User> userOptional = this.service.findbyId(id);
        if(userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(userOptional.orElseThrow());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "usuario no encontrado con id:" + id));
    }
    
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {        
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(user));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        Optional<User> useroptional = this.service.findbyId(id);
        if(useroptional.isPresent()) {
            User userBD = useroptional.get();
            userBD.setEmail(user.getEmail());
            userBD.setNombre(user.getNombre());
            userBD.setPassword(user.getPassword());
            return ResponseEntity.ok(this.service.save(userBD));
        }        
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<User> userOptional = this.service.findbyId(id);
        if(userOptional.isPresent()) {
            this.service.deletebyId(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
