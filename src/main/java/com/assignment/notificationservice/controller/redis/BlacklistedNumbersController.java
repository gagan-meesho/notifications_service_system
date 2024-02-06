package com.assignment.notificationservice.controller.redis;

import com.assignment.notificationservice.dao.redis.BlacklistedNumbersRepository;
import com.assignment.notificationservice.dto.requestbodydto.BlacklistedNumbersDTO;
import com.assignment.notificationservice.dto.responsedto.BlacklistedNumbersResponseDTO;
import com.assignment.notificationservice.dto.responsedto.GeneralMessageDTO;
import com.assignment.notificationservice.entity.redis.BlacklistedNumbers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/blacklist")
@EnableCaching
public class BlacklistedNumbersController {
    BlacklistedNumbersRepository blacklistedNumbersRepository;
    RedisTemplate redisTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(BlacklistedNumbersController.class);


    public BlacklistedNumbersController(BlacklistedNumbersRepository blacklistedNumbersRepository, RedisTemplate redisTemplate) {
        this.blacklistedNumbersRepository = blacklistedNumbersRepository;
        this.redisTemplate = redisTemplate;
    }

    private static final String KEY="BLACKLISTED_NUMBER";
    @GetMapping("/")
    public ResponseEntity<BlacklistedNumbersResponseDTO> findAllBlacklistedNumbers(){
        try {
            List<String> result=redisTemplate.opsForHash().values(KEY);
            return ResponseEntity.ok(new BlacklistedNumbersResponseDTO(result));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BlacklistedNumbersResponseDTO(new ArrayList<>()));
        }
    }

    @PostMapping("/")
    public ResponseEntity<GeneralMessageDTO> addNewNumbersToBlacklistedNumbers(@RequestBody BlacklistedNumbersDTO blacklistedNumbersDTO){
        var phoneNumbers= blacklistedNumbersDTO.getPhoneNumbers();
//        System.out.println("here are your phone numbers dude "+ phoneNumbers);
        LOGGER.info(String.format("The list of numbers recieved to be blacklisted : %s",phoneNumbers.toString()));

        for(var phoneNumber:phoneNumbers){
            try{
                if(redisTemplate.opsForHash().hasKey(KEY,phoneNumber)) {
//                    System.out.println("this phone number already exists bro "+phoneNumber);
                    LOGGER.info("Phone number %s is already blocked.",phoneNumber);
                    continue;
                }
                redisTemplate.opsForHash().put(KEY,phoneNumber,phoneNumber);
                LOGGER.info("Added phone number %s to redis blacklisted list.",phoneNumber);
                var blacklistedNumber=new BlacklistedNumbers(phoneNumber);
                blacklistedNumbersRepository.save(blacklistedNumber);
                LOGGER.info("Added phone number %s to blacklisted database.",phoneNumber);
            }catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GeneralMessageDTO("false"));
            }
        }
        return ResponseEntity.ok(new GeneralMessageDTO("Successfully blacklisted"));
    }

    @DeleteMapping("/")
    public ResponseEntity<GeneralMessageDTO> deleteNumbersFromBlacklistedNumbers(@RequestBody BlacklistedNumbersDTO blacklistedNumbersDTO){
        var phoneNumbers= blacklistedNumbersDTO.getPhoneNumbers();
        System.out.println("here are your phone numbers"+ phoneNumbers);
        for(var phoneNumber:phoneNumbers){
            try{
                if(!redisTemplate.opsForHash().hasKey(KEY,phoneNumber)){
                    LOGGER.info(String.format("Phone number %s doesn't exist in the database.",phoneNumber));
                    continue;
                }
                redisTemplate.opsForHash().delete(KEY,phoneNumber);
                LOGGER.info(String.format("Deleted phone number %s from redis.",phoneNumber));
                blacklistedNumbersRepository.deleteSpecificPhoneNumber(phoneNumber);
                LOGGER.info(String.format("Deleted phone number %s from databse.",phoneNumber));
            }catch(Exception e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GeneralMessageDTO("bad request"));
            }
        }
        return ResponseEntity.ok(new GeneralMessageDTO("Successfully deleted"));
    }
}