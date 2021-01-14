package org.jekajops.payment_service.core.http.body_builders;

import org.jekajops.payment_service.core.http.pojos.Pojo;

import java.util.HashMap;
import java.util.Map;

public class PojoBuilder implements BodyBuilder{
    Pojo pojo;

    public PojoBuilder(Pojo pojo) {
        this.pojo = pojo;
    }

    @Override
    public String getJsonString() {
        return pojo.getJsonString();
    }

    @Override
    public Map getBodyMap() {
        return new HashMap();
    }
}
