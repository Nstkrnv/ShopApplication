package sample;

import static java.lang.String.valueOf;

public class PythonLauncher {

    public static void Pyth (int id) {
     //   String testFilePath = "C:\\work\\coldun.py";
        try {
            // Microsoft Windows NT or later
            ProcessBuilder pb = new ProcessBuilder("python.exe", "C:\\Users\\Настя\\IdeaProjects\\trial1\\coldun.py", valueOf(id));
            Process p = pb.start();

            // Microsoft Windows 95/98
            // Runtime.getRuntime().exec("c:\\windows\\notepad.exe " + testFilePath);
            p.waitFor();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

}
