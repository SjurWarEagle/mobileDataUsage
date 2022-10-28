package de.tkunkel.mobileDataUsage;

import de.tkunkel.mobileDataUsage.mqtt.MqttSender;
import de.tkunkel.mobileDataUsage.processor.LoginProcessor;
import de.tkunkel.mobileDataUsage.types.Configuration;
import de.tkunkel.mobileDataUsage.types.UserLoginType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackageClasses = Starter.class)
public class Starter {
    private final Logger LOG = LoggerFactory.getLogger(Starter.class);
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private LoginProcessor loginProcessor;
    @Autowired
    private MqttSender mqttSender;
    @Autowired
    private Configuration configuration;

    public static void main(String[] args) {
        new SpringApplicationBuilder(Starter.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Scheduled(cron = "@hourly")
    // https://spring.io/blog/2020/11/10/new-in-spring-5-3-improved-cron-expressions
    private void regularly() {
        requestAndExportData();
    }

    @PostConstruct
    private void start() {
        requestAndExportData();
    }

    private void requestAndExportData() {
        try {
            HttpClient httpclient = createHttpClient();
            UserLoginType userLoginType = loginProcessor.extractUserLoginInfo(httpclient);

            HttpPost httpPost = new HttpPost("https://service.sim.de/public/login_check");
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("UserLoginType[alias]", userLoginType.alias));
            nvps.add(new BasicNameValuePair("UserLoginType[password]", userLoginType.password));
            nvps.add(new BasicNameValuePair("UserLoginType[logindata]", userLoginType.logindata));
            nvps.add(new BasicNameValuePair("UserLoginType[csrf_token]", userLoginType.csrf_token));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));

            HttpResponse response2 = httpclient.execute(httpPost);
            HttpEntity entity = response2.getEntity();
            Document document = Jsoup.parse(EntityUtils.toString(entity), "UTF-8");
            String memoryInfo = Objects.requireNonNull(Objects.requireNonNull(document
                                    .selectFirst(".dataUsageBar"))
                            .selectFirst(".medium"))
                    .text();

            DataParser dataParser = new DataParser();
            int usedMemoryInMB = dataParser.extractUsedMemoryInMB(memoryInfo);
            int contractMemoryInMB = dataParser.extractContractVolume(memoryInfo);
            distributeUsageInfo(usedMemoryInMB, contractMemoryInMB);
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void distributeUsageInfo(int usedMemoryInMB, int contractMemoryInMB) throws IOException, MqttException {
        showUsageInConsole(usedMemoryInMB, contractMemoryInMB);
        writeUsageToFile(usedMemoryInMB, contractMemoryInMB);
        sendToMqtt(usedMemoryInMB, contractMemoryInMB);
    }

    private void writeUsageToFile(int usedMemoryInMB, int contractMemoryInMB) throws IOException {
        String fileName = configuration.outputFileName;
        if (!Files.exists(Paths.get(fileName))) {
            try (FileWriter fw = new FileWriter(Paths.get(fileName).toFile())) {
                fw.write("Date,contractMemoryInMB,usedMemoryInMB");
                fw.write("\n");
                fw.flush();
            }
        }
        try (FileWriter fw = new FileWriter(Paths.get(fileName).toFile(), true)) {
            Date now = new Date();
            fw.append(sdf.format(now))
                    .append(",")
                    .append(String.valueOf(contractMemoryInMB))
                    .append(",")
                    .append(String.valueOf(usedMemoryInMB))
                    .append("\n");
            fw.flush();
        }
    }

    private void showUsageInConsole(int usedMemoryInMB, int contractMemoryInMB) {
        Date now = new Date();

        String msg = sdf.format(now) + ": " + usedMemoryInMB + "MB / " + contractMemoryInMB + "MB";
        LOG.info(msg);
    }

    private void sendToMqtt(int usedMemoryInMB, int contractMemoryInMB) throws MqttException {
        mqttSender.sendUsageDate(usedMemoryInMB, contractMemoryInMB);
    }

    private HttpClient createHttpClient() {
        CookieStore cookieStore = new BasicCookieStore();
        return HttpClientBuilder
                .create()

                .setDefaultCookieStore(cookieStore)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36")
                .setRedirectStrategy(new LaxRedirectStrategy())

                .build();
    }

}
