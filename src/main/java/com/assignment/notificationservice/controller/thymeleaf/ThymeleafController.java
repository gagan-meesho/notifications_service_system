package com.assignment.notificationservice.controller.thymeleaf;


import com.assignment.notificationservice.dto.requestbodydto.BlacklistedNumbersDTO;
import com.assignment.notificationservice.dto.requestbodydto.SendSmsResponseDTO;
import com.assignment.notificationservice.dto.responsedto.BlacklistedNumbersResponseDTO;
import com.assignment.notificationservice.dto.responsedto.SuccessfullySentSmsApiResponse;
import com.assignment.notificationservice.elasticsearch.SearchRequestDTO;
import com.assignment.notificationservice.entity.Request;
import com.assignment.notificationservice.entity.authentication.JwtRequest;
import com.assignment.notificationservice.entity.authentication.JwtResponse;
import com.assignment.notificationservice.entity.elasticsearch.SmsRequestIndex;
import jakarta.validation.Valid;
import org.apache.kafka.common.network.Mode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class ThymeleafController {

    private JwtRequest jwtRequest;
    private RestTemplate restTemplate;
    private SendSmsResponseDTO sendSmsResponseDTO;

    private SearchRequestDTO searchRequestDTO;

    private String jwtToken;

    public ThymeleafController(JwtRequest jwtRequest, RestTemplate restTemplate,SendSmsResponseDTO sendSmsResponseDTO,SearchRequestDTO searchRequestDTO) {
        this.jwtRequest = jwtRequest;
        this.restTemplate=restTemplate;
        this.sendSmsResponseDTO=sendSmsResponseDTO;
        this.searchRequestDTO=searchRequestDTO;
    }

    public Boolean isLoggedIn(){
        return jwtToken!=null;
    }

    @GetMapping("/showMyLoginPage")
    public String showMyLoginPage(Model model){

        model.addAttribute("jwtRequest",jwtRequest);
        return "fancy-login";
    }
    @PostMapping("/homepage")
    public String processForm(@Valid @ModelAttribute("jwtRequest")JwtRequest jwtRequest, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "fancy-login";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HttpEntity with the request body and headers
        HttpEntity<JwtRequest> entity = new HttpEntity<>(jwtRequest, headers);

        // Make a POST request to the /login endpoint
        try {
            ResponseEntity<JwtResponse> responseEntity = restTemplate.postForEntity(
                    "http://localhost:8080/auth/login",
                    entity,
                    JwtResponse.class
            );
            System.out.println("response entity is: " + responseEntity);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                JwtResponse jwtResponse = responseEntity.getBody();
                if (jwtResponse != null) {
                    String token = jwtResponse.getJwtToken();
                    jwtToken = token;
                    System.out.println("JWT Token: " + token);
                    return "homepage";
                } else {
                    System.err.println("JWT Response is null");
                }
            } else {
                System.err.println("Failed to log in. Status code: " + responseEntity.getStatusCodeValue());
            }
        }catch (Exception e)
        {
            System.out.println(e);
        }
        return "fancy-login";
    }


    @GetMapping("/homepage")
    public String getHomePage(){
        return "homepage";
    }

    @GetMapping("/sendsmsform")
    public String showSendSmsForm(Model model){

        model.addAttribute("sendSmsResponseDTO",sendSmsResponseDTO);
        return "send-sms-form";
    }

    @PostMapping("/processSendSms")
    public String processSendSms(@Valid @ModelAttribute("sendSmsResponseDTO")SendSmsResponseDTO sendSmsResponseDTO, BindingResult bindingResult){
        System.out.println("here is the sms details : "+ sendSmsResponseDTO);
        if(bindingResult.hasErrors()){
            return "send-sms-form";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        System.out.println(jwtToken);
        headers.add("Authorization","Bearer "+jwtToken);
        // Create HttpEntity with the request body and headers
        HttpEntity<SendSmsResponseDTO> entity = new HttpEntity<>(sendSmsResponseDTO, headers);

        // Make a POST request to the /login endpoint
        try {
            ResponseEntity<SuccessfullySentSmsApiResponse> responseEntity = restTemplate.postForEntity(
                    "http://localhost:8080/v1/sms/send",
                    entity,
                    SuccessfullySentSmsApiResponse.class
            );
            System.out.println("response entity is: " + responseEntity);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                SuccessfullySentSmsApiResponse successfullySentSmsApiResponse = responseEntity.getBody();
                if (successfullySentSmsApiResponse != null) {
                    return "success";
                } else {
                    System.err.println("Failed to send sms");
                }
            } else {
                System.err.println("Failed to send sms. Status code: " + responseEntity.getStatusCodeValue());
            }
        }catch (Exception e)
        {
            System.out.println(e);
        }
        return "homepage";
    }

    @GetMapping("/showsentsmsdetails")
    public String showSentSmsDetails(Model model){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+jwtToken);
        System.out.println("in show sent sms details");
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<List<Request>> responseEntity = restTemplate.exchange(
                "http://localhost:8080/v1/sms",
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<List<Request>>() {}
        );

// Extract the list of Request objects from the ResponseEntity
        List<Request> requestList = responseEntity.getBody();
        System.out.println("sms details "+requestList);
        model.addAttribute("requestList",requestList);
        System.out.println(requestList);
        return "sms-details";
    }

    @GetMapping("/showblacklistednumbersdetails")
    public String showBlacklistedNumbersDetails(Model model){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Add custom header
        headers.set("Authorization", "Bearer "+jwtToken);

        // Create request entity with headers
        HttpEntity<?> entity = new HttpEntity<>(headers);

        // Make the request
        String url = "http://localhost:8080/v1/blacklist/";
        ResponseEntity<BlacklistedNumbersResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                BlacklistedNumbersResponseDTO.class
        );
        BlacklistedNumbersResponseDTO result=response.getBody();
        if(result!=null)
        System.out.println("blacklisted numbers response "+ result.getData());
        model.addAttribute("result",result);
        return "blacklisted-details";
    }

    @GetMapping("/elasticsearchform")
    public String showElasticSearchForm(Model model){
        model.addAttribute("searchRequestDTO",searchRequestDTO);
        return "elastic-search-form";
    }

    @PostMapping("/elasticsearchqueryresult")
    public String getElasticSearchQuery(@Valid @ModelAttribute("searchRequestDTO")SearchRequestDTO searchRequestDTO, BindingResult bindingResult,Model model){

        if(bindingResult.hasErrors()){
            return "elastic-search-form";
        }
        System.out.println("search request dto : "+searchRequestDTO.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtToken);

        HttpEntity<SearchRequestDTO> entity = new HttpEntity<>(searchRequestDTO, headers);

        try {
            ParameterizedTypeReference<List<SmsRequestIndex>> responseType = new ParameterizedTypeReference<List<SmsRequestIndex>>() {};

// Make the POST request to the endpoint
            ResponseEntity<List<SmsRequestIndex>> responseEntity = restTemplate.exchange(
                    "http://localhost:8080/v1/elasticsearch/search",
                    HttpMethod.POST,
                    entity,
                    responseType
            );

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                List<SmsRequestIndex> smsRequestIndices = responseEntity.getBody();
                for (SmsRequestIndex smsRequestIndex : smsRequestIndices) {
                    System.out.println(smsRequestIndex.toString());
                }
                model.addAttribute("result",smsRequestIndices);
                return "elastic-search-result";
            } else {
                // Handle non-OK status code
                System.err.println("Failed to get search results. Status code: " + responseEntity.getStatusCodeValue());
            }
        } catch (Exception e) {
            // Handle RestClientException
            e.printStackTrace();
        }
        return "elastic-search-form";
    }
}
