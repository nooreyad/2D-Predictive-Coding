import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

public class Main {

    static ArrayList<ArrayList<Integer>> compressedReds;
    static ArrayList<ArrayList<Integer>> compressedGreens;
    static ArrayList<ArrayList<Integer>> compressedBlues;
    static BufferedImage image;
    static FilesHandler handler;

    public static void main(String[] args) {

        JFrame frame = new JFrame("Vector Quantization");
        frame.setBounds(400, 100, 520, 400);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JButton compress = new JButton("Compress");
        compress.setBounds(200, 100, 120, 50);
        compress.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Path currentFilePath;
                JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int response = fileChooser.showOpenDialog(null);

                if (response == JFileChooser.APPROVE_OPTION) {
                    currentFilePath = Path.of(fileChooser.getSelectedFile().getAbsolutePath());
                } else {
                    System.out.println("Operation cancelled");
                    return;
                }

                String compressedFilePath = currentFilePath.getParent().toString() + "/" +
                        currentFilePath.getFileName().toString().split("\\.")[0] + "-compressed.bin";

                try {
                    new Main().compress(currentFilePath.toString(), compressedFilePath);

                    JLabel label = new JLabel("File successfully compressed.");
                    label.setBounds(180, 300, 200, 20);
                    frame.add(label);
                    frame.repaint();

                    new Timer(2000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frame.remove(label);
                            frame.repaint();
                        }
                    }).start();

                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        frame.add(compress);

        JButton decompress = new JButton("Decompress");
        decompress.setBounds(200, 200, 120, 50);
        decompress.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Path currentFilePath;
                JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int response = fileChooser.showOpenDialog(null);

                if (response == JFileChooser.APPROVE_OPTION) {
                    currentFilePath = Path.of(fileChooser.getSelectedFile().getAbsolutePath());
                } else {
                    System.out.println("Operation cancelled");
                    return;
                }

                String decompressedFilePath = currentFilePath.getParent().toString() + "/" +
                        currentFilePath.getFileName().toString().split("\\.")[0] + "-decompressed.txt";

                try {
                    new Main().decompress(currentFilePath.toString(), decompressedFilePath);

                    JLabel label = new JLabel("File successfully decompressed.");
                    label.setBounds(180, 300, 200, 20);
                    frame.add(label);
                    frame.repaint();

                    new Timer(2000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frame.remove(label);
                            frame.repaint();
                        }
                    }).start();

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        frame.add(decompress);

        frame.setVisible(true);
    }

    public void compress(String inputFilePath, String outputFilePath) throws IOException {

        File file = new File("test.jpg");
        handler = new FilesHandler();
        image = ImageIO.read(file);

        Color[][] imageArray = handler.get2DPixelArray(image);
        ColorGetter colorGetter = new ColorGetter();
        ArrayList<ArrayList<Integer>> reds = colorGetter.getReds(imageArray, image.getHeight(), image.getWidth());
        ArrayList<ArrayList<Integer>> greens = colorGetter.getGreens(imageArray, image.getHeight(), image.getWidth());
        ArrayList<ArrayList<Integer>> blues = colorGetter.getBlues(imageArray, image.getHeight(), image.getWidth());

        Compression compressionRed = new Compression(reds, handler.imageH, handler.imageW);
        compressedReds = compressionRed.compress();
        ArrayList<Integer> originalTopBorderRed = compressionRed.originalTopBorder;
        ArrayList<Integer> originalLeftBorderRed = compressionRed.originalLeftBorder;

        Compression compressionGreen = new Compression(greens, handler.imageH, handler.imageW);
        compressedGreens = compressionGreen.compress();
        ArrayList<Integer> originalTopBorderGreen = compressionGreen.originalTopBorder;
        ArrayList<Integer> originalLeftBorderGreen = compressionGreen.originalLeftBorder;

        Compression compressionBlue = new Compression(blues, handler.imageH, handler.imageW);
        compressedBlues = compressionBlue.compress();
        ArrayList<Integer> originalTopBorderBlue = compressionBlue.originalTopBorder;
        ArrayList<Integer> originalLeftBorderBlue = compressionBlue.originalLeftBorder;

        ArrayList<ArrayList<Integer>> originalStoredTopContent = new ArrayList<>();
        ArrayList<ArrayList<Integer>> originalStoredLeftContent = new ArrayList<>();
        originalStoredTopContent.add(originalTopBorderRed);
        originalStoredTopContent.add(originalTopBorderGreen);
        originalStoredTopContent.add(originalTopBorderBlue);
        originalStoredLeftContent.add(originalLeftBorderRed);
        originalStoredLeftContent.add(originalLeftBorderGreen);
        originalStoredLeftContent.add(originalLeftBorderBlue);

        File compressionFile = new File("test-compressed.bin");
        FileWriter compressionFileWriter = new FileWriter(compressionFile);

        compressionFileWriter.write(image.getWidth());
        compressionFileWriter.write(image.getHeight());
        for (ArrayList<Integer> arr: originalStoredTopContent) {
            for (Integer integer : arr) {
                compressionFileWriter.write(FilesHandler.toBinary(integer));
            }
        }
        for (ArrayList<Integer> arr: originalStoredLeftContent) {
            for (Integer integer : arr) {
                compressionFileWriter.write(FilesHandler.toBinary(integer));
            }
        }

        compressionFileWriter.close();
    }

    public void decompress(String compressedFilePath, String decompressedFilePath) throws IOException {
        
        File decompressionFile = new File("test-compressed.bin");
        FileReader decompressionFileReader = new FileReader(decompressionFile);
        
        Integer imageWidth = decompressionFileReader.read();
        Integer imageHeight = decompressionFileReader.read();

        ArrayList<Integer> originalTopBorderRed = new ArrayList<>();
        ArrayList<Integer> originalLeftBorderRed = new ArrayList<>();
        ArrayList<Integer> originalTopBorderGreen = new ArrayList<>();
        ArrayList<Integer> originalLeftBorderGreen = new ArrayList<>();
        ArrayList<Integer> originalTopBorderBlue = new ArrayList<>();
        ArrayList<Integer> originalLeftBorderBlue = new ArrayList<>();

        for (int i = 0; i < imageWidth; i++) {
            originalTopBorderRed.add(decompressionFileReader.read());
        }
        for (int i = 0; i < imageWidth; i++) {
            originalTopBorderGreen.add(decompressionFileReader.read());
        }
        for (int i = 0; i < imageWidth; i++) {
            originalTopBorderBlue.add(decompressionFileReader.read());
        }
        for (int i = 0; i < imageHeight; i++) {
            originalLeftBorderRed.add(decompressionFileReader.read());
        }
        for (int i = 0; i < imageHeight; i++) {
            originalLeftBorderGreen.add(decompressionFileReader.read());
        }
        for (int i = 0; i < imageHeight; i++) {
            originalLeftBorderBlue.add(decompressionFileReader.read());
        }

        BufferedImage imageOut = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        System.out.println("here");
        System.out.println(image.getWidth() + " " + image.getHeight());
        for (int i = 0; i < handler.imageH; i++) {
            int j;
            for (j = 0; j < handler.imageW; j++) {
                int[] pixel = new int[3];
                pixel[0] = Math.abs(compressedReds.get(i).get(j));
                if (pixel[0] > 255) pixel[0] = 255;
                pixel[1] = Math.abs(compressedGreens.get(i).get(j));
                if (pixel[1] > 255) pixel[1] = 255;
                pixel[2] = Math.abs(compressedBlues.get(i).get(j));
                if (pixel[2] > 255) pixel[2] = 255;
                System.out.println(pixel[0] + " " + pixel[1] + " " + pixel[2]);
                Color c = new Color(pixel[0], pixel[1], pixel[2]);
                imageOut.setRGB(i, j, c.getRGB());
                g2d.drawImage(imageOut, null, 0, 0);
            }
        }
        ImageIO.write(imageOut, "jpg", new File("newImage.jpg"));
    }

}
