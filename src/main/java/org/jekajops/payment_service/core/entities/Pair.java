package org.jekajops.payment_service.core.entities;

import java.util.Objects;

public class Pair <T>{
    private T o1;
    private T o2;

    public Pair(T o1, T o2) {
        this.o1 = o1;
        this.o2 = o2;
    }


    public T getO1() {
        return o1;
    }

    public void setO1(T o1) {
        this.o1 = o1;
    }

    public T getO2() {
        return o2;
    }

    public void setO2(T o2) {
        this.o2 = o2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return Objects.equals(o1, pair.o1) &&
                Objects.equals(o2, pair.o2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(o1, o2);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "o1=" + o1 +
                ", o2=" + o2 +
                '}';
    }
}
