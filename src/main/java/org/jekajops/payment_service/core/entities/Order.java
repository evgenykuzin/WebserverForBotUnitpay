package org.jekajops.payment_service.core.entities;

public record Order(
        int id,
        int prankId,
        int userId,
        String phone,
        String callId,
        long startTime) {}

