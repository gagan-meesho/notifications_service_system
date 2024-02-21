package com.assignment.notificationservice.controller.redis;

import com.assignment.notificationservice.dao.redis.BlacklistedNumbersRepository;
import com.assignment.notificationservice.dto.requestDTO.redis.BlacklistedNumbersDTO;
import com.assignment.notificationservice.dto.responseDTO.redis.BlacklistedNumbersResponseDTO;
import com.assignment.notificationservice.dto.responseDTO.api.GeneralMessageDTO;
import com.assignment.notificationservice.entity.redis.BlacklistedNumbers;
import com.assignment.notificationservice.exception.InternalServerErrorException;
import com.assignment.notificationservice.exception.RedisInteralServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @GetMapping("/")
    public ResponseEntity<BlacklistedNumbersResponseDTO> findAllBlacklistedNumbers() throws RedisInteralServerErrorException {
        log.debug("Retrieving all blacklisted numbers");
        try {
            List<String> result = redisTemplate.opsForHash().values(KEY);
            log.debug("Retrieved {} blacklisted numbers successfully", result.size());
            return ResponseEntity.ok(new BlacklistedNumbersResponseDTO(result));
        } catch (Exception e) {
            log.error("Error occurred while retrieving blacklisted numbers from redis", e);
           throw new RedisInteralServerErrorException("Error occurred while retrieving blacklisted numbers");
        }
    }

    @PostMapping("/")
    public ResponseEntity<BlacklistedNumbersResponseDTO> addNewNumbersToBlacklistedNumbers(@RequestBody BlacklistedNumbersDTO blacklistedNumbersDTO) throws RedisInteralServerErrorException {
        List<String> phoneNumbers = blacklistedNumbersDTO.getPhoneNumbers();
        log.debug(String.format("The list of numbers recieved to be blacklisted : %s", phoneNumbers.toString()));
        List<String> successfullyBlacklistedNumbers=new ArrayList<>();
        for (String phoneNumber : phoneNumbers) {
            try {
                if (redisTemplate.opsForHash().hasKey(KEY, phoneNumber)) {
                    log.debug("Phone number %s is already blocked.", phoneNumber);
                    continue;
                }
                redisTemplate.opsForHash().put(KEY, phoneNumber, phoneNumber);
                log.debug("Added phone number %s to redis blacklisted list.", phoneNumber);
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
        List<String> successfullyBlacklistedNumbers = new ArrayList<>();
        for (String phoneNumber : phoneNumbers) {
            try {
                if (!redisTemplate.opsForHash().hasKey(KEY, phoneNumber)) {
                    log.debug("Phone number {} doesn't exist in the database.",phoneNumber);
                    continue;
                }
                redisTemplate.opsForHash().delete(KEY, phoneNumber);
                log.debug(String.format("Deleted phone number %s from redis.", phoneNumber));
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
