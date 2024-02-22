package com.assignment.notificationservice.controller.redis;

import com.assignment.notificationservice.dao.redis.BlacklistedNumbersRepository;
import com.assignment.notificationservice.dto.requestDTO.redis.BlacklistedNumbersDTO;
import com.assignment.notificationservice.dto.responseDTO.redis.BlacklistedNumbersResponseDTO;
import com.assignment.notificationservice.entity.redis.BlacklistedNumbers;
import com.assignment.notificationservice.exception.BadRequestErrorException;
import com.assignment.notificationservice.exception.RedisInteralServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/blacklist")
@EnableCaching
@Slf4j
public class BlacklistedNumbersController {
    BlacklistedNumbersRepository blacklistedNumbersRepository;
    RedisTemplate redisTemplate;

    public BlacklistedNumbersController(BlacklistedNumbersRepository blacklistedNumbersRepository, RedisTemplate redisTemplate) {
        this.blacklistedNumbersRepository = blacklistedNumbersRepository;
        this.redisTemplate = redisTemplate;
    }

    @Value("${REDIS_KEY}")
    private String KEY;

    public Boolean isValidNumbers(List<String> phoneNumbers){
        for (String str : phoneNumbers) {
            if (!str.matches("[0-9]+") || str.length() < 7 || str.length() > 10) {
                return false;
            }
        }
        return true;
    }
    @GetMapping("/")
    public ResponseEntity<BlacklistedNumbersResponseDTO> findAllBlacklistedNumbers() throws RedisInteralServerErrorException {
        try {
            List<String> result = redisTemplate.opsForHash().values(KEY);
            log.debug("Retrieved {} blacklisted numbers successfully", result.size());
            return ResponseEntity.ok(new BlacklistedNumbersResponseDTO(result));
        } catch (Exception e) {
            log.error("Error occurred while retrieving blacklisted numbers from redis");
           throw new RedisInteralServerErrorException("Error occurred while retrieving blacklisted numbers");
        }
    }

    @PostMapping("/")
    public ResponseEntity<BlacklistedNumbersResponseDTO> addNewNumbersToBlacklistedNumbers(@RequestBody BlacklistedNumbersDTO blacklistedNumbersDTO) throws RedisInteralServerErrorException {
        List<String> phoneNumbers = blacklistedNumbersDTO.getPhoneNumbers();
        if(!isValidNumbers(phoneNumbers)){
            throw new BadRequestErrorException();
        }
        List<String> successfullyBlacklistedNumbers=new ArrayList<>();
        for (String phoneNumber : phoneNumbers) {
            try {
                if (redisTemplate.opsForHash().hasKey(KEY, phoneNumber)) {
                    continue;
                }
                redisTemplate.opsForHash().put(KEY, phoneNumber, phoneNumber);
                var blacklistedNumber = new BlacklistedNumbers(phoneNumber);
                blacklistedNumbersRepository.save(blacklistedNumber);
                log.debug("Added phone number %s to blacklisted database.", phoneNumber);
                successfullyBlacklistedNumbers.add(phoneNumber);
            } catch (Exception e) {
                throw new RedisInteralServerErrorException("Error while adding number to redis");
            }
        }
        return ResponseEntity.ok(new BlacklistedNumbersResponseDTO(successfullyBlacklistedNumbers));
    }

    @DeleteMapping("/")
    public ResponseEntity<BlacklistedNumbersResponseDTO> deleteNumbersFromBlacklistedNumbers(@RequestBody BlacklistedNumbersDTO blacklistedNumbersDTO) throws RedisInteralServerErrorException {
        List<String> phoneNumbers = blacklistedNumbersDTO.getPhoneNumbers();
        if(!isValidNumbers(phoneNumbers)){
            throw new BadRequestErrorException();
        }
        List<String> successfullyBlacklistedNumbers = new ArrayList<>();
        for (String phoneNumber : phoneNumbers) {
            try {
                if (!redisTemplate.opsForHash().hasKey(KEY, phoneNumber)) {
                    continue;
                }
                redisTemplate.opsForHash().delete(KEY, phoneNumber);
                blacklistedNumbersRepository.deleteSpecificPhoneNumber(phoneNumber);
                log.debug(String.format("Deleted phone number %s from databse.", phoneNumber));
                successfullyBlacklistedNumbers.add(phoneNumber);
            } catch (Exception e) {
               throw new RedisInteralServerErrorException("Error while deleting number from redis");
            }
        }
        return ResponseEntity.ok(new BlacklistedNumbersResponseDTO(successfullyBlacklistedNumbers));
    }
}
