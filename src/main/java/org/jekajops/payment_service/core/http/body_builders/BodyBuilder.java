package org.jekajops.payment_service.core.http.body_builders;

import java.util.HashMap;
import java.util.Map;

public interface BodyBuilder {
    BodyBuilder NO_BODY = new BodyBuilder() {
        @Override
        public String getJsonString() {
            return "";
        }

        @Override
        public Map getBodyMap() {
            return new HashMap();
        }
    };
    String getJsonString();
    Map getBodyMap();
}
