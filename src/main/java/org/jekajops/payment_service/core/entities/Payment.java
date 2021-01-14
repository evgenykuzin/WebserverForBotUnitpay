package org.jekajops.payment_service.core.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Payment {
    private String method, account, date, operator, paymentType, projectId,
            phone, payerSum, payerCurrency, signature,
            orderSum, orderCurrency, unitpayId, test,
            profit, errorMessage, has3ds, subscriptionId, ip;

    public static final String PAY = "pay";
    public static final String CHECK = "check";
    public static final String ERROR = "error";
    public static final String PREAUTH = "preauth";


    public Payment() {
    }

    public Payment(String method, String account, String date, String operator, String paymentType, String projectId, String phone, String payerSum, String payerCurrency, String signature, String orderSum, String orderCurrency, String unitpayId, String test, String profit, String errorMessage, String has3ds, String subscriptionId, String ip) {
        this.method = method;
        this.account = account;
        this.date = date;
        this.operator = operator;
        this.paymentType = paymentType;
        this.projectId = projectId;
        this.phone = phone;
        this.payerSum = payerSum;
        this.payerCurrency = payerCurrency;
        this.signature = signature;
        this.orderSum = orderSum;
        this.orderCurrency = orderCurrency;
        this.unitpayId = unitpayId;
        this.test = test;
        this.profit = profit;
        this.errorMessage = errorMessage;
        this.has3ds = has3ds;
        this.subscriptionId = subscriptionId;
        this.ip = ip;
    }

    public Payment(Map<String, String> map) {
        final Map<String, String> newMap = new HashMap<>();
        map.forEach((k, v) -> newMap.put(k.replaceAll("params\\[|]", ""), v));
        map = newMap;
        this.method = map.get("method");
        this.account = map.get("account");
        this.date =  map.get("date");
        this.operator =  map.get("operator");
        this.paymentType =  map.get("paymentType");
        this.projectId =  map.get("projectId");
        this.phone =  map.get("phone");
        this.payerSum =  map.get("payerSum");
        this.payerCurrency =  map.get("payerCurrency");
        this.signature =  map.get("signature");
        this.orderSum =  map.get("orderSum");
        this.orderCurrency =  map.get("orderCurrency");
        this.unitpayId =  map.get("unitpayId");
        this.test =  map.get("test");
        this.profit = map.get("profit");
        this.errorMessage = map.get("errorMessage");
        this.has3ds = map.get("3ds");
        this.subscriptionId = map.get("subscriptionId");
        this.ip = map.get("ip");
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPayerSum() {
        return payerSum;
    }

    public void setPayerSum(String payerSum) {
        this.payerSum = payerSum;
    }

    public String getPayerCurrency() {
        return payerCurrency;
    }

    public void setPayerCurrency(String payerCurrency) {
        this.payerCurrency = payerCurrency;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getOrderSum() {
        return orderSum;
    }

    public void setOrderSum(String orderSum) {
        this.orderSum = orderSum;
    }

    public String getOrderCurrency() {
        return orderCurrency;
    }

    public void setOrderCurrency(String orderCurrency) {
        this.orderCurrency = orderCurrency;
    }

    public String getUnitpayId() {
        return unitpayId;
    }

    public void setUnitpayId(String unitpayId) {
        this.unitpayId = unitpayId;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getHas3ds() {
        return has3ds;
    }

    public void setHas3ds(String has3ds) {
        this.has3ds = has3ds;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(method, payment.method) &&
                Objects.equals(account, payment.account) &&
                Objects.equals(date, payment.date) &&
                Objects.equals(operator, payment.operator) &&
                Objects.equals(paymentType, payment.paymentType) &&
                Objects.equals(projectId, payment.projectId) &&
                Objects.equals(phone, payment.phone) &&
                Objects.equals(payerSum, payment.payerSum) &&
                Objects.equals(payerCurrency, payment.payerCurrency) &&
                Objects.equals(signature, payment.signature) &&
                Objects.equals(orderSum, payment.orderSum) &&
                Objects.equals(orderCurrency, payment.orderCurrency) &&
                Objects.equals(unitpayId, payment.unitpayId) &&
                Objects.equals(test, payment.test) &&
                Objects.equals(profit, payment.profit) &&
                Objects.equals(errorMessage, payment.errorMessage) &&
                Objects.equals(has3ds, payment.has3ds) &&
                Objects.equals(subscriptionId, payment.subscriptionId) &&
                Objects.equals(ip, payment.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, account, date, operator, paymentType, projectId, phone, payerSum, payerCurrency, signature, orderSum, orderCurrency, unitpayId, test, profit, errorMessage, has3ds, subscriptionId, ip);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "method='" + method + '\'' +
                ", account='" + account + '\'' +
                ", date='" + date + '\'' +
                ", operator='" + operator + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", projectId='" + projectId + '\'' +
                ", phone='" + phone + '\'' +
                ", payerSum='" + payerSum + '\'' +
                ", payerCurrency='" + payerCurrency + '\'' +
                ", signature='" + signature + '\'' +
                ", orderSum='" + orderSum + '\'' +
                ", orderCurrency='" + orderCurrency + '\'' +
                ", unitpayId='" + unitpayId + '\'' +
                ", test='" + test + '\'' +
                ", profit='" + profit + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", has3ds='" + has3ds + '\'' +
                ", subscriptionId='" + subscriptionId + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
