import java.util.ArrayList;

public class Parser {
    String commandName;
    ArrayList<String> args;

    public Parser() {
        commandName = new String();
        args = new ArrayList<>();
    }

    //This method will divide the input into commandName and args
//where "input" is the string command entered by the user
    public boolean parse(String input) {
        if (input == null)
            return false;
        String[] tmp = input.split(" ");
        commandName = tmp[0];
        args.clear();
        for (int i = 1; i < tmp.length; i++) {
            args.add(tmp[i]);
        }
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public ArrayList<String> getArgs() {
        return args;
    }
}
