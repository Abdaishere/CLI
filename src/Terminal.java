import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.nio.file.*;


public class Terminal {
    Parser parser;
    File workingDirectory;

    public Terminal() {
        parser = new Parser();
        workingDirectory = new File(System.getProperty("user.dir"));
    }

    public void run(String input) throws IOException {
        parser.parse(input);
        chooseCommandAction();
    }

    public String pwd() {
        return workingDirectory.getPath();
    }


    public void cd() throws IOException {
        ArrayList<String> args = parser.getArgs();
        if (args.size() == 0) {
            workingDirectory = new File(System.getProperty("user.dir"));
            return;
        }
        String sourcePath = args.get(0);
        if (sourcePath.equals("..")) {
            String parent = workingDirectory.getParent();
            File f = new File(parent);
            workingDirectory = f.getAbsoluteFile();
        } else {
            File f = makeAbsolute(sourcePath);
            if (!f.exists()) {
                throw new NoSuchFileException(f.getAbsolutePath(), null, "does not exist");
            }
            if (f.isFile()) {
                throw new IOException("Can't cd into file");
            } else workingDirectory = f.getAbsoluteFile();
        }
    }

    public File makeAbsolute(String sourcePath) {
        File f = new File(sourcePath);
        if (!f.isAbsolute()) {
            f = new File(workingDirectory.getAbsolutePath(), sourcePath);
        }
        return f.getAbsoluteFile();
    }


    public String echo() {
        return String.join(" ", parser.getArgs());
    }

    public String ls() {
        try {
            List<String> ls = Files.list(Paths.get(workingDirectory.getPath()))
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
        for (String arg : args) {
            File theDir;
            if (arg.contains(":"))
                theDir = new File(arg);
            else
                theDir = new File(workingDirectory.getPath() + "\\" + arg);
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
        }
    }

    public void rmdir() {
        ArrayList<String> args = parser.getArgs();
        String dir = args.get(0);
        try {
            if (dir.equals("*")) {
                File[] tmp = workingDirectory.listFiles();
                assert tmp != null;
                for (File file : tmp) {
                    if (!file.isFile()) {
                        if (file.listFiles().length == 0) {
                            file.delete();
                        }
                    }
                }
            } else {
                File theDir;
                if (dir.contains(":"))
                    theDir = new File(dir);
                else
                    theDir = new File(workingDirectory.getPath() + "\\" + dir);
                if (!theDir.delete())
                    System.out.println("Error file not found");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void touch() {
        ArrayList<String> tmp = parser.getArgs();
        File file;

        // check if full or short path
        if (tmp.get(0).contains(":")) {
            file = new File(tmp.get(0));
        } else {
            file = new File(workingDirectory.getPath() + "\\" + tmp.get(0));
        }

        try {
            if (!file.exists())
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
            file = new File(workingDirectory.getPath() + "\\" + tmp.get(0));
            try {
                Scanner scan = new Scanner(file);
                while (scan.hasNextLine()) {
                    System.out.println(scan.nextLine());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        } else if (tmp.size() == 2) {
            file = new File(workingDirectory.getPath() + "\\" + tmp.get(0));

            file2 = new File(workingDirectory.getPath() + "\\" + tmp.get(1));
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
            file = new File(workingDirectory.getPath() + "\\" + tmp.get(0));
            file.delete();
        } else {
            System.out.println("file not found");
        }
    }

    public void cp() throws IOException {
        ArrayList<String> tmp = parser.getArgs();
        if (!tmp.get(0).equals("-r")) {
            try {
                File sourceLocation = new File(workingDirectory.getPath() + "\\" + tmp.get(0));
                File targetLocation = new File(workingDirectory.getPath() + "\\" + tmp.get(1));

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
                File sourceLocation = new File(workingDirectory.getPath() + "\\" + tmp.get(1));
                File targetLocation = new File(workingDirectory.getPath() + "\\" + tmp.get(2));
                if (sourceLocation.isDirectory()) {
                    if (!targetLocation.exists()) {
                        targetLocation.mkdir();
                    }

                    String[] children = sourceLocation.list();
                    for (String child : children) {
                        copy(new File(sourceLocation, child),
                                new File(targetLocation, child));
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
            for (String child : children) {
                copy(new File(sourceLocation, child),
                        new File(targetLocation, child));
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

    public void chooseCommandAction() throws IOException {
        switch (parser.getCommandName()) {
            case "pwd" -> System.out.println(pwd());
            case "echo" -> System.out.println(echo());
            case "ls" -> System.out.println(ls());
            case "mkdir" -> mkdir();
            case "rmdir" -> rmdir();
            case "touch" -> touch();
            case "cat" -> cat();
            case "rm" -> rm();
            case "cp" -> cp();
            case "cd" -> cd();
            default -> throw new IllegalStateException("Unexpected value: " + parser.getCommandName());
        }
    }

    public static void main(String[] args) {
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
