package de.tkunkel.mobileDataUsage.google;

import com.google.api.client.auth.oauth2.Credential;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface IGoogleAuthorizeUtil {
    Credential authorize() throws IOException, GeneralSecurityException;
}
