package de.tkunkel.mobileDataUsage.prcessor;

import de.tkunkel.mobileDataUsage.types.Configuration;
import de.tkunkel.mobileDataUsage.types.UserLoginType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginProcessor {
    @Autowired
    public Configuration configuration;

    public UserLoginType extractUserLoginInfo(HttpClient httpclient) throws IOException {
        HttpGet httpGet = new HttpGet("https://service.sim.de/");
        HttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);

        Document document = Jsoup.parse(body, "UTF-8");

        String csrfToken = document
                .selectFirst("#UserLoginType_csrf_token")
                .attr("value");

        String logindata = document
                .selectFirst("#UserLoginType_logindata")
                .attr("value");


        UserLoginType userLoginType = new UserLoginType();
        userLoginType.alias = configuration.simDeUser;
        userLoginType.password = configuration.simDePassword;
        userLoginType.csrf_token = csrfToken;
        userLoginType.logindata = logindata;

        return userLoginType;
    }
}
