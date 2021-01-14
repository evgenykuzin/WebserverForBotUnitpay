package org.jekajops.payment_service.core.http.pojos;

public interface PojoDefault extends Pojo {
    @Override
    default String getJsonString() {
        return gsonParse();
    }
}
