package de.tkunkel.mobileDataUsage.google;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EntityScan(basePackageClasses = ExperimentsStarter.class)
public class ExperimentsStarter {
    private static final String SPREADSHEET_ID = "1hRMGsiVh2dUMqOyJk2tVIs3B_U5-oMPHpVmvzNl-DrQ";
    @Autowired
    public GoogleAuthorizeUtil googleAuthorizeUtil;
    @Autowired
    public SheetsServiceUtil sheetsServiceUtil;

    public static void main(String[] args) throws Exception {
        new ExperimentsStarter().start();
    }

    private void start() throws Exception {
        Sheets sheetsService = sheetsServiceUtil.getSheetsService();
        googleAuthorizeUtil.authorize();

        ValueRange body = new ValueRange()
                .setValues(Arrays.asList(
                        List.of("Expenses January"),
                        Arrays.asList("books", "30"),
                        Arrays.asList("pens", "10"),
                        List.of("Expenses February"),
                        Arrays.asList("clothes", "20"),
                        Arrays.asList("shoes", "5")));
        UpdateValuesResponse result = sheetsService.spreadsheets().values()
                .update(SPREADSHEET_ID, "A1", body)
                .setValueInputOption("RAW")
                .execute();
    }
}
