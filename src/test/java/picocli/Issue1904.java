package picocli;

import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.junit.Assert.*;

public class Issue1904 {

    enum DebugFacility { FOO, BAR, BAZ, ALL }

    @CommandLine.Command
    static class TestCommand implements Runnable {

        @CommandLine.Option(
            names = {"--debug"},
            paramLabel = "FACILITY",
            split = ",",
            arity = "0..1",
            fallbackValue = "ALL"
        )
        public Set<DebugFacility> debugFacilities = Collections.emptySet();

        @CommandLine.Parameters(arity = "1..*")
        public List<File> paths;

        public void run() {}
    }

    @Test
    public void singleOption() {
        final CommandLine commandLine = new CommandLine(new TestCommand());
        final CommandLine.ParseResult parseResult = commandLine.parseArgs("--debug", "ALL", "example.txt");
        final TestCommand command = (TestCommand) parseResult.commandSpec().userObject();
        assertEquals(EnumSet.of(DebugFacility.ALL), command.debugFacilities);
        assertFalse(command.paths.isEmpty());
    }

    @Test
    public void singleOptionWithCommandSeparatedValues() {
        final CommandLine commandLine = new CommandLine(new TestCommand());
        final CommandLine.ParseResult parseResult = commandLine.parseArgs("--debug", "FOO,BAR", "example.txt");
        final TestCommand command = (TestCommand) parseResult.commandSpec().userObject();
        assertEquals(EnumSet.of(DebugFacility.FOO, DebugFacility.BAR), command.debugFacilities);
        assertFalse(command.paths.isEmpty());
    }

    @Test
    public void singleOptionWithFallback() {
        final CommandLine commandLine = new CommandLine(new TestCommand());
        final CommandLine.ParseResult parseResult = commandLine.parseArgs("--debug", "example.txt");
        final TestCommand command = (TestCommand) parseResult.commandSpec().userObject();
        assertEquals(EnumSet.of(DebugFacility.ALL), command.debugFacilities);
        assertFalse(command.paths.isEmpty());
    }

    @Test
    public void multipleOptions() {
        final CommandLine commandLine = new CommandLine(new TestCommand());
        final CommandLine.ParseResult parseResult = commandLine.parseArgs("--debug", "FOO", "--debug", "BAR", "example.txt");
        final TestCommand command = (TestCommand) parseResult.commandSpec().userObject();
        assertEquals(EnumSet.of(DebugFacility.FOO, DebugFacility.BAR), command.debugFacilities);
        assertFalse(command.paths.isEmpty());
    }

    @Test
    public void multipleOptionsWithFallback() {
        final CommandLine commandLine = new CommandLine(new TestCommand());
        final CommandLine.ParseResult parseResult = commandLine.parseArgs("--debug", "--debug", "BAR", "example.txt");
        final TestCommand command = (TestCommand) parseResult.commandSpec().userObject();
        assertEquals(EnumSet.of(DebugFacility.ALL, DebugFacility.BAR), command.debugFacilities);
        assertFalse(command.paths.isEmpty());
    }
}
