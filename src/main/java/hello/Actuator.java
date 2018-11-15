package hello;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import response.DnsRecords;
import response.Record;
import response.UpdatedRecord;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Actuator implements Runnable {

    private static final String BASE_URL = "https://api.cloudflare.com/client/v4/";
    private static final Logger log = LoggerFactory.getLogger(Actuator.class);

    @Override
    public void run() {
        try {
            File configFile = new File("config.properties");
            FileInputStream fileInputStream = new FileInputStream(configFile);
            Properties properties = new Properties();
            properties.load(fileInputStream);

            String auth_key = properties.getProperty("auth_key");
            String auth_email = properties.getProperty("auth_email");
            String returnIPServer = properties.getProperty("returnIPServer");
            String zone_id = properties.getProperty("zone_id");
            String record_name = properties.getProperty("record_name");


            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Content-Type", "application/json");
            headers.set("X-Auth-Key", auth_key);
            headers.set("X-Auth-Email", auth_email);

//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            String recordsURL = BASE_URL + "zones/" + zone_id + "/dns_records";
            ResponseEntity<DnsRecords> exchange = restTemplate.exchange(recordsURL + "?name=" + record_name, HttpMethod.GET, entity, DnsRecords.class);


            if (200 == exchange.getStatusCodeValue()) {
                DnsRecords body = exchange.getBody();
                if (body.isSuccess() && body.getResult().size() == 1) {
                    Record record = body.getResult().get(0);
                    String record_id = record.getId();
                    String address = record.getContent();
                    String updateURL = recordsURL + "/" + record_id;

                    while (true) {
                        long sleepTime = 10000;
                        try {
                            log.info("oldAddress ... " + address);
                            String newAddress = restTemplate.getForObject(returnIPServer, String.class);
                            log.info("newAddress ... " + newAddress);
                            if (isNotEmpty(newAddress)) {
                                if (!address.equals(newAddress)) {
                                    JSONObject requestJson = new JSONObject();
                                    requestJson.put("type", "A");
                                    requestJson.put("name", record_name);
                                    requestJson.put("content", newAddress);

                                    HttpEntity<String> entity1 = new HttpEntity<>(requestJson.toString(), headers);

                                    ResponseEntity<UpdatedRecord> updateResponse = restTemplate.exchange(updateURL, HttpMethod.PUT, entity1, UpdatedRecord.class);

                                    if (updateResponse.getStatusCodeValue() == 200) {
                                        UpdatedRecord updateResponseBody = updateResponse.getBody();
                                        if (updateResponseBody.isSuccess()) {
                                            address = updateResponseBody.getResult().getContent();
                                            log.info("update success, sleep 10 second");
                                            sleepTime = 10000;
                                        } else {
                                            log.info("update error, sleep one hour ... ");
                                            sleepTime = 3600000;
                                        }
                                    }

                                }
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            sleepTime = 600000;
                        } finally {
                            log.info("sleep " + sleepTime / 1000 + " second");
                            Thread.sleep(sleepTime);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static boolean isEmpty(String str) {
        return ((str == null) || (str.length() == 0));
    }

    private static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
