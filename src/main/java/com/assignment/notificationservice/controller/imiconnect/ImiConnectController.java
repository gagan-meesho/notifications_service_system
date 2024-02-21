package com.assignment.notificationservice.controller.imiconnect;

import com.assignment.notificationservice.constants.imiconnect.ImiConnectConstants;
import com.assignment.notificationservice.entity.imiconnect.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.*;

@RestController
@RequestMapping("/v1/smsing")
@Slf4j
public class ImiConnectController {
    ObjectMapper oj = new ObjectMapper();

    @Value("${IMI_API_KEY}")
    private String IMI_API_KEY;

    @PostMapping("/sendimi")
    public void sendImi() {
        log.info("sending imi");
        ImiConnectRequest imiConnectRequest = new ImiConnectRequest("sms",
                new Channels(new Sms(
                        "hi this is some message")
                ),
                List.of(
                        new Msisdn(List.of("+918123070802"), 1)
                )
        );
        log.debug("body of imirequest is : {}", imiConnectRequest);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("key", IMI_API_KEY);
            HttpEntity<ImiConnectRequest> httpEntity = new HttpEntity<ImiConnectRequest>(imiConnectRequest, headers);

            ResponseEntity<FinalResponse> responseEntity = restTemplate.exchange(ImiConnectConstants.host, HttpMethod.POST, httpEntity, FinalResponse.class);
            FinalResponse response = responseEntity.getBody();
            try {
                log.info("imi connect Response is {}", oj.writeValueAsString(response));
            }
            catch (Exception e){
                log.error("Failed to send sms via ImiConnect");
            }
    }
}
