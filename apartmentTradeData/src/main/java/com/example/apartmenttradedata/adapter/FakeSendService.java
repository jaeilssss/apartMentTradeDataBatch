package com.example.apartmenttradedata.adapter;

import org.springframework.stereotype.Service;

@Service
public class FakeSendService implements SendService{
    @Override
    public void send(String email, String message) {
        System.out.println("email : " + email + "\nmessage : "+message);
    }


}
