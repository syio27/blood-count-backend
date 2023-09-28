package com.pja.bloodcount.service.contract;

public interface NotifierService {
    void notifyUser(String email, String subject, String message);
}
