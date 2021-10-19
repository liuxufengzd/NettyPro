package liu.code.myExample.example7;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
public class ServiceCommandObject {
    private List<Command> commands;
    @Getter
    @Setter
    @ToString
    public static class Command {
        private String name;
        private String[] aliases;
    }
}
