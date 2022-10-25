package de.tkunkel.mobileDataUsage.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.List;

@Component
public class GoogleAuthorizeUtil implements IGoogleAuthorizeUtil{
    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Override
    public Credential authorize() throws IOException, GeneralSecurityException {
        List<String> scopes = List.of(SheetsScopes.SPREADSHEETS);

        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(GoogleAuthorizeUtil.class.getResourceAsStream("/mobiledatausage.p12"), "notasecret".toCharArray());
        PrivateKey pk = (PrivateKey)keystore.getKey("privatekey", "notasecret".toCharArray());

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId("mobiledatausage@mobiledatausage.iam.gserviceaccount.com")
//                .setServiceAccountId("094f43b127715db73b4df7de10b641b6e4398502")
                .setServiceAccountScopes(scopes)
                .setServiceAccountPrivateKey(pk)
                // .setRefreshListeners(refreshListeners)
                //.setServiceAccountUser("email.com")
                .build();


        credential.refreshToken();
//
//
//        InputStream in = GoogleAuthorizeUtil.class.getResourceAsStream("/client_secret.json");
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));
//
//
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), clientSecrets, scopes).setDataStoreFactory(new MemoryDataStoreFactory())
//                .setAccessType("offline").build();
//        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

        return credential;
    }

}
