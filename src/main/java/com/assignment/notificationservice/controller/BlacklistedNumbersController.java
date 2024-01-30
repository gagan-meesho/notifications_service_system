package com.assignment.notificationservice.controller;

import com.assignment.notificationservice.dao.BlacklistedNumbersRepository;
import com.assignment.notificationservice.dto.BlacklistedNumbersDTO;
import com.assignment.notificationservice.dto.BlacklistedNumbersResponseDTO;
import com.assignment.notificationservice.dto.GeneralMessageDTO;
import com.assignment.notificationservice.entity.BlacklistedNumbers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/v1/blacklist")
@EnableCaching
public class BlacklistedNumbersController {
    BlacklistedNumbersRepository blacklistedNumbersRepository;

    @Autowired
    public BlacklistedNumbersController(BlacklistedNumbersRepository blacklistedNumbersRepository){
        this.blacklistedNumbersRepository=blacklistedNumbersRepository;
    }

    @GetMapping("/")
    public ResponseEntity<BlacklistedNumbersResponseDTO> findAllBlacklistedNumbers(){
        List<String> result=blacklistedNumbersRepository.getAllBlacklistedPhoneNumbers();
        BlacklistedNumbersResponseDTO responseEntity=new BlacklistedNumbersResponseDTO(result);
        return ResponseEntity.ok(responseEntity);
    }

    @PostMapping("/")
    public ResponseEntity<GeneralMessageDTO> addNewNumbersToBlacklistedNumbers(@RequestBody BlacklistedNumbersDTO blacklistedNumbersDTO){
        var phoneNumbers= blacklistedNumbersDTO.getPhoneNumbers();
        System.out.println("here are your phone numbers"+ phoneNumbers);
        for(var phoneNumber:phoneNumbers){
            // to do: before saving in the database make sure it isn't already present in the database by making a look up in redis.
            var blacklistedNumber=new BlacklistedNumbers(phoneNumber);
            blacklistedNumbersRepository.save(blacklistedNumber);
            //to do: after saving in the database, save it in the redis as well.
        }
        return ResponseEntity.ok(new GeneralMessageDTO("Successfully blacklisted"));
    }

    @DeleteMapping("/")
    public ResponseEntity<GeneralMessageDTO> deleteNumbersFromBlacklistedNumbers(@RequestBody BlacklistedNumbersDTO blacklistedNumbersDTO){
        var phoneNumbers= blacklistedNumbersDTO.getPhoneNumbers();
        System.out.println("here are your phone numbers"+ phoneNumbers);
        for(var phoneNumber:phoneNumbers){
            // to do: before saving in the database make sure it is already present in the database by making a look up in redis.
            blacklistedNumbersRepository.deleteSpecificPhoneNumber(phoneNumber);
            //to do: after deleting from the database, delete it from the redis as well.
        }
        return ResponseEntity.ok(new GeneralMessageDTO("Successfully deleted"));
    }
}
