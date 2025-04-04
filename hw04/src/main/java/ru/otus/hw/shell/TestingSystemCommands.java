package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent(value = "Testing System Commands")
@RequiredArgsConstructor
public class TestingSystemCommands {

    private final TestRunnerService testRunnerService;

    @ShellMethod(value = "Run student test", key = {"start", "run", "r"})
    public String runTest() {
        testRunnerService.run();
        return "\nTest complete, please, check your results above";
    }
}
