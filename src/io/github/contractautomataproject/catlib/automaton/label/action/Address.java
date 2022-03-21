package io.github.contractautomataproject.catlib.automaton.label.action;

import io.github.contractautomataproject.catlib.automaton.label.Matchable;

import java.util.Objects;

public class Address implements Matchable<Address> {

    private final String sender;
    private final String receiver;
    private static final String ID_SEPARATOR = "_";
    private static final String ACTION_SEPARATOR = "@";

    public Address(String sender, String receiver) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(receiver);

        this.sender = sender;
        this.receiver = receiver;

        if (this.sender.isEmpty()||this.receiver.isEmpty())
            throw new IllegalArgumentException();
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(sender, address.sender) && Objects.equals(receiver, address.receiver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver);
    }

    @Override
    public String toString() {
        return this.sender +ID_SEPARATOR+this.receiver +ACTION_SEPARATOR;
    }

    @Override
    public boolean match(Address arg) {
        return sender.equals(arg.sender)&&receiver.equals(arg.receiver);
    }


    public static boolean isParsable(String lab) {
        String[] f = lab.split(ACTION_SEPARATOR);
        String[] p = f[0].split(ID_SEPARATOR);
        return (p.length==2 && f.length>=1);
    }

    public static Address parseAddress(String lab) {
        String[] f = lab.split(ACTION_SEPARATOR);
        String[] p = f[0].split(ID_SEPARATOR);
        if (p.length!=2 || f.length<1)
            throw new IllegalArgumentException();
        return new Address(p[0],p[1]);
    }

    public static String removeAddress(String lab){
        return lab.substring(lab.indexOf(ACTION_SEPARATOR)+1,lab.length());
    }

}
