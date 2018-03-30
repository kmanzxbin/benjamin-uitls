package gov.gdcrj;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

public class QueryRemark {

    String url = "http://www.gdcrj.com/gdwssq/jdcx_dy.html?&method=find&ywbh={id}";

    public static void main(String[] args) {
        new QueryRemark().queryRemark("030800125537460");
    }

    public void queryRemark(String id) {
        try {

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpUriRequest httpRequest = new HttpGet(url.replace("{id}", id));

            HttpResponse httpResponse = httpClient.execute(httpRequest);
            byte[] content = new byte[(int) httpResponse.getEntity()
                    .getContentLength()];
            httpResponse.getEntity().getContent().read(content);

            System.out.println(new String(content));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
