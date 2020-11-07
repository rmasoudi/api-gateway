package ir.farabi.esb;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Message;

import java.util.ArrayList;
import java.util.List;

public class JoiningStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Message newIn = newExchange.getIn();
        List newBody = (List) newIn.getBody();
        List list = null;
        if (oldExchange == null) {
            list = new ArrayList();
            list.addAll(newBody);
            newIn.setBody(list);
            return newExchange;
        } else {
            Message in = oldExchange.getIn();
            list = in.getBody(List.class);
            list.addAll(newBody);
            return oldExchange;
        }
    }
}
