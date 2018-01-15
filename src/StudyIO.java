import java.io.*;

public class StudyIO {

    public static void main(String[] args) {
//        FileInputStream();
//        FileOutputStream();
        try {
            InputStream inputStream = new FileInputStream("source\\ECHO.mp3");
            OutputStream outputStream = new FileOutputStream("target\\ECHO.mp3");
            int len;
            long startTime = System.currentTimeMillis();
            while ((len = inputStream.read()) != -1) {
                outputStream.write(len);
            }
            System.out.println(System.currentTimeMillis() - startTime);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void FileOutputStream() {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("output.txt", true);
            String str = "ZMZ_TEST_TWICE";
            byte[] bs = str.getBytes();
            for (byte b : bs) {
                fileOutputStream.write(b);
            }
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void FileInputStream() {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("test.txt");
            int b;
            while (true) {
                b = fileInputStream.read();
                if (b == -1) {
                    break;
                }
                System.out.println(b);
            }
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
