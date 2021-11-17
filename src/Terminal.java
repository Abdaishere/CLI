import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Terminal {
    Parser parser;

    public Terminal() {
        parser = new Parser();
    }

    public void run(String input) throws IOException {
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
        try {
            if (dir.equals("*")) {
                File theDir = new File(System.getProperty("user.dir"));
                File[] tmp = theDir.listFiles();
                for (int i = 0; i < tmp.length; i++) {
                    File file = tmp[i];
                    if (!file.isFile()) {
                        if (file.listFiles().length == 0) {
                            file.delete();
                        }
                    }
                }
            } else {

                File theDir;
                // check if full or short path
                if (dir.contains(":")) {
                    theDir = new File(dir);
                } else {
                    theDir = new File(System.getProperty("user.dir") + "\\" + dir);
                }
                System.out.println(1);
                if (theDir.listFiles().length == 0) {
                    theDir.delete();
                }

            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void touch() {
        ArrayList<String> tmp = parser.getArgs();
        File file;

        // check if full or short path
        if (tmp.get(0).contains(":")) {
            file = new File(tmp.get(0));
        } else {
            file = new File(System.getProperty("user.dir") + "\\" + tmp.get(0));
        }

        try {
            if (!new File(tmp.get(0)).exists())
                file.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void cat() {
        ArrayList<String> tmp = parser.getArgs();
        File file;
        File file2;
        if (tmp.size() == 1) {
            file = new File(System.getProperty("user.dir") + "\\" + tmp.get(0));
            try {
                Scanner scan = new Scanner(file);
                while (scan.hasNextLine()) {
                    System.out.println(scan.nextLine());
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        } else if (tmp.size() == 2) {
            file = new File(System.getProperty("user.dir") + "\\" + tmp.get(0));

            file2 = new File(System.getProperty("user.dir") + "\\" + tmp.get(1));
            try {
                Scanner scan = new Scanner(file);


                String fileContent = "";
                while (scan.hasNextLine()) {
                    fileContent = fileContent.concat(scan.nextLine() + "\n");
                }

                Scanner scan2 = new Scanner(file2);
                while (scan2.hasNextLine()) {
                    fileContent = fileContent.concat(scan2.nextLine() + "\n");

                }
                System.out.println(fileContent);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public void rm() {
        ArrayList<String> tmp = parser.getArgs();
        if (tmp.size() == 1) {
            File file;
            file = new File(System.getProperty("user.dir") + "\\" + tmp.get(0));
            file.delete();
        } else {
            System.out.println("Error");
            return;
        }
    }

    public void cp() throws IOException {
        ArrayList<String> tmp = parser.getArgs();
        if (!tmp.get(0).equals("-r")) {
            FileReader ins = null;
            FileWriter outs = null;
            try {
                File sourceLocation = new File(System.getProperty("user.dir") + "\\" + tmp.get(0));
                File targetLocation = new File(System.getProperty("user.dir") + "\\" + tmp.get(1));

                InputStream in = new FileInputStream(sourceLocation);
                OutputStream out = new FileOutputStream(targetLocation);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } else {
            try {
                File sourceLocation = new File(System.getProperty("user.dir") + "\\" + tmp.get(1));
                File targetLocation = new File(System.getProperty("user.dir") + "\\" + tmp.get(2));
                if (sourceLocation.isDirectory()) {
                    if (!targetLocation.exists()) {
                        targetLocation.mkdir();
                    }

                    String[] children = sourceLocation.list();
                    for (int i = 0; i < children.length; i++) {
                        copy(new File(sourceLocation, children[i]),
                                new File(targetLocation, children[i]));
                    }
                } else {

                    InputStream in = new FileInputStream(sourceLocation);
                    OutputStream out = new FileOutputStream(targetLocation);

                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                }
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    public void copy(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copy(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    public void cd(String[] args) {

    }

    // ...
//This method will choose the suitable command method to be called
    public void chooseCommandAction() throws IOException {
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
            case "rmdir": {
                rmdir();
                break;
            }
            case "touch": {
                touch();
                break;
            }
            case "cat": {
                cat();
                break;
            }
            case "rm": {
                rm();
                break;
            }
            case "cp": {
                cp();
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + parser.getCommandName());
        }
    }

    public static void main(String[] args) throws IOException {
        String input;
        Scanner scan = new Scanner(System.in);
        Terminal terminal = new Terminal();
        input = scan.nextLine();
        while (!input.equals("exit")) {
            try {
                terminal.run(input);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            input = scan.nextLine();
        }
    }
}
