package ir.farabi.esb;

import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@ConfigurationProperties(prefix = "gateway")
public class APIRouter extends RouteBuilder {

    private String api2, api1;

    private static final String REST_ENDPOINT =
            "http:%s?httpClient.connectTimeout=1000" +
                    "&bridgeEndpoint=true" +
                    "&copyHeaders=true" +
                    "&connectionClose=true";

    @Override
    public void configure() {
        from("direct:api1").streamCaching()
                .toF(REST_ENDPOINT, api1)
                .log("Response from api1 :${body}")
                .convertBodyTo(String.class)
                .unmarshal().json(JsonLibrary.Jackson)
                .end();

        from("direct:api2").streamCaching()
                .toF(REST_ENDPOINT, api2)
                .log("Response from api2 : +${body}")
                .convertBodyTo(String.class)
                .unmarshal().json(JsonLibrary.Jackson)
                .end();

        rest()
                .get("/gateway").enableCORS(true)
                .route()
                .multicast(new JoiningStrategy())
                .parallelProcessing()
                .to("direct:api1")
                .to("direct:api2")
                .end()
                .marshal().json(JsonLibrary.Jackson)
                .convertBodyTo(String.class)
                .endRest();
    }

    public void setApi2(String api2) {
        this.api2 = api2;
    }

    public void setApi1(String api1) {
        this.api1 = api1;
    }

}
