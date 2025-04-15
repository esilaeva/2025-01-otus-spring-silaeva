package ru.otus.hw.commands;

import org.h2.tools.Console;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.sql.SQLException;

@ShellComponent
public class ConsoleCommands {

    @ShellMethod(value = "Show H2 console", key = "cshow")
    public void showH2Console() throws SQLException {
        Console.main();
    }
}
