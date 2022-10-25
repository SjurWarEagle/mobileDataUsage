package de.tkunkel.mobileDataUsage.google;

import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface ISheetsServiceUtil {
    Sheets getSheetsService() throws IOException, GeneralSecurityException;
}
