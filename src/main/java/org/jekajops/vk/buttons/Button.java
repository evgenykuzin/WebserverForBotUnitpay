package org.jekajops.vk.buttons;

import org.jekajops.vk.VKManager;
import org.jekajops.vk.commands.Command;

import java.util.Objects;

public class Button {
    private Command command;
    private String color;
    private String name;
    private boolean inline;
    public static final String PRIMARY = "primary";
    public static final String SECONDARY = "secondary";
    public static final String NEGATIVE = "negative";
    public static final String POSITIVE = "positive";


    public Button(Command command) {
        this.command = command;
        name = command.name;
        this.inline = false;
    }

    public Button(Command command, String color, boolean inline) {
        this.command = command;
        name = command.name;
        this.color = color;
        this.inline = inline;
    }

    public Button(Command command, String color) {
        this.command = command;
        name = command.name;
        this.color = color;
        this.inline = false;
    }

    public Button(Button button) {
        set(button);
    }

    public Button(String name) {
        this.name = name;
    }

    public Button(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Button set(Button button) {
        this.name = button.name;
        this.command = button.command;
        this.color = button.color;
        return this;
    }

    public Command getCommand() {
        return command;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public boolean isInline() {
        return inline;
    }

    public void setInline(boolean inline) {
        this.inline = inline;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Button button = (Button) o;
        return inline == button.inline && Objects.equals(command, button.command) && Objects.equals(color, button.color) && Objects.equals(name, button.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, color, name, inline);
    }

    @Override
    public String toString() {
        return "Button{" +
                "command=" + command +
                ", color='" + color + '\'' +
                ", name='" + name + '\'' +
                ", inline=" + inline +
                '}';
    }

}