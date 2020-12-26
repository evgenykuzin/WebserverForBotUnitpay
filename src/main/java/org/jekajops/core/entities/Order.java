package org.jekajops.core.entities;

import java.sql.Timestamp;

public record Order(
        int id,
        int prankId,
        int userId,
        String phone,
        String callId,
        long startTime) {}

