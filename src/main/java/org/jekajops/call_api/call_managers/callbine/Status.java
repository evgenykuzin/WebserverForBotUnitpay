package org.jekajops.call_api.call_managers.callbine;

import org.jekajops.call_api.call_managers.CallObject;

import java.util.Objects;

public class Status implements CallObject {
    private String id, phone, duration, record_url, status, attempt, completion_flag;
    public Status(String id, String phone, String duration, String record_url, String status, String attempt, String completion_flag) {
        this.id = id;
        this.phone = phone;
        this.duration = duration;
        this.record_url = record_url;
        this.status = status;
        this.attempt = attempt;
        this.completion_flag = completion_flag;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRecord_url() {
        return record_url;
    }

    public void setRecord_url(String record_url) {
        this.record_url = record_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttempt() {
        return attempt;
    }

    public void setAttempt(String attempt) {
        this.attempt = attempt;
    }

    public String getCompletion_flag() {
        return completion_flag;
    }

    public void setCompletion_flag(String completion_flag) {
        this.completion_flag = completion_flag;
    }

    @Override
    public String toString() {
        return "Status{" +
                "id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", duration='" + duration + '\'' +
                ", record_url='" + record_url + '\'' +
                ", status='" + status + '\'' +
                ", attempt='" + attempt + '\'' +
                ", completion_flag='" + completion_flag + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status1 = (Status) o;
        return Objects.equals(id, status1.id) &&
                Objects.equals(phone, status1.phone) &&
                Objects.equals(duration, status1.duration) &&
                Objects.equals(record_url, status1.record_url) &&
                Objects.equals(status, status1.status) &&
                Objects.equals(attempt, status1.attempt) &&
                Objects.equals(completion_flag, status1.completion_flag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phone, duration, record_url, status, attempt, completion_flag);
    }
}
