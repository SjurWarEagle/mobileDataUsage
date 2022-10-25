package de.tkunkel.mobileDataUsage.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
public class SheetsServiceUtil implements  ISheetsServiceUtil {

    private static final String APPLICATION_NAME = "Google Sheets Example";
    @Autowired
    public IGoogleAuthorizeUtil googleAuthorizeUtil;

    @Override
    public Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = googleAuthorizeUtil.authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential).setApplicationName(APPLICATION_NAME).build();
    }

}
