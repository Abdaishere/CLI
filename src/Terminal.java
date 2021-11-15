import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Terminal {
    Parser parser;

    public Terminal() {
        parser = new Parser();
    }

    public void run(String input) {
        parser.parse(input);
        chooseCommandAction();
    }

    //Implement each command in a method, for example:
    public String pwd() {
        return System.getProperty("user.dir");
    }

    public String echo() {
        return String.join(" ", parser.getArgs());
    }

    public String ls() {
        try {
            List<String> ls = Files.list(Paths.get(System.getProperty("user.dir")))
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());
            if (parser.getArgs().size() != 0 && parser.getArgs().get(0).equals("-r"))
                Collections.reverse(ls);
            return String.join(" ", ls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void mkdir() {
        ArrayList<String> args = parser.getArgs();
        for (int i = 0; i < args.size(); i++) {
            File theDir = new File(args.get(i));
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
        }
    }


    // path.toFile().listFiles().length == 0
    public void rmdir() {
        ArrayList<String> args = parser.getArgs();
        String dir = args.get(0);
        if (dir.equals("*")){
            File theDir = new File(System.getProperty("user.dir"));
            for(int i = 0; i<theDir.listFiles().length; i++) {

                if (theDir.listFiles()[i].listFiles().length == 0) {
                    theDir.listFiles()[i].delete();
                }
                
            }
        }
    }
    public void cd(String[] args) {

    }

    // ...
    //This method will choose the suitable command method to be called
    public void chooseCommandAction() {
        switch (parser.getCommandName()) {
            case "pwd": {
                System.out.println(pwd());
                break;
            }
            case "echo": {
                System.out.println(echo());
                break;
            }
            case "ls": {
                System.out.println(ls());
                break;
            }
            case "mkdir": {
                mkdir();
                break;
            }
            case "rmdir":{

            }
            default:
                throw new IllegalStateException("Unexpected value: " + parser.getCommandName());
        }
    }

    public static void main(String[] args) {
        String input;
        Scanner scan = new Scanner(System.in);
        Terminal terminal = new Terminal();
        input = scan.nextLine();
        while (!input.equals("exit")) {
            terminal.run(input);
            input = scan.nextLine();
        }
    }
}
