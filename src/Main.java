import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("test.jpg");
        FilesHandler handler = new FilesHandler();
        BufferedImage image = ImageIO.read(file);
        Color[][] imageArray = handler.get2DPixelArray(image);
        ColorGetter colorGetter = new ColorGetter();
        ArrayList<ArrayList<Integer>> reds = colorGetter.getReds(imageArray, image.getHeight(), image.getWidth());
        ArrayList<ArrayList<Integer>> greens = colorGetter.getGreens(imageArray, image.getHeight(), image.getWidth());
        ArrayList<ArrayList<Integer>> blues = colorGetter.getBlues(imageArray, image.getHeight(), image.getWidth());
        Compression compressionRed = new Compression(reds, handler.imageH, handler.imageW);
        ArrayList<ArrayList<Integer>> compressedReds = compressionRed.compress();
        Compression compressionGreen = new Compression(greens, handler.imageH, handler.imageW);
        ArrayList<ArrayList<Integer>> compressedGreens = compressionGreen.compress();
        Compression compressionBlue = new Compression(blues, handler.imageH, handler.imageW);
        ArrayList<ArrayList<Integer>> compressedBlues = compressionBlue.compress();
        BufferedImage imageOut = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
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
//    public static void main(String[] args) {
//
//        Quantizer quantizer = new Quantizer();
//
//        JFrame frame = new JFrame("Vector Quantization");
//        frame.setBounds(400, 100, 520, 400);
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.setLayout(null);
//
//        JButton compress = new JButton("Compress");
//        compress.setBounds(200, 100, 120, 50);
//        compress.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                Path currentFilePath;
//                JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
//
//                int response = fileChooser.showOpenDialog(null);
//
//                if (response == JFileChooser.APPROVE_OPTION) {
//                    currentFilePath = Path.of(fileChooser.getSelectedFile().getAbsolutePath());
//                } else {
//                    System.out.println("Operation cancelled");
//                    return;
//                }
//
//                String compressedFilePath = currentFilePath.getParent().toString() + "/" +
//                        currentFilePath.getFileName().toString().split("\\.")[0] + "-compressed.bin";
//
//                try {
//                    quantizer.compress(currentFilePath.toString(), compressedFilePath);
//
//                    JLabel label = new JLabel("File successfully compressed.");
//                    label.setBounds(180, 300, 200, 20);
//                    frame.add(label);
//                    frame.repaint();
//
//                    new Timer(2000, new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//                            frame.remove(label);
//                            frame.repaint();
//                        }
//                    }).start();
//
//                } catch (FileNotFoundException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
//        });
//        frame.add(compress);
//
//        JButton decompress = new JButton("Decompress");
//        decompress.setBounds(200, 200, 120, 50);
//        decompress.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                Path currentFilePath;
//                JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
//
//                int response = fileChooser.showOpenDialog(null);
//
//                if (response == JFileChooser.APPROVE_OPTION) {
//                    currentFilePath = Path.of(fileChooser.getSelectedFile().getAbsolutePath());
//                } else {
//                    System.out.println("Operation cancelled");
//                    return;
//                }
//
//                String decompressedFilePath = currentFilePath.getParent().toString() + "/" +
//                        currentFilePath.getFileName().toString().split("\\.")[0] + "-decompressed.txt";
//
//                try {
//                    quantizer.decompress(currentFilePath.toString(), decompressedFilePath);
//
//                    JLabel label = new JLabel("File successfully decompressed.");
//                    label.setBounds(180, 300, 200, 20);
//                    frame.add(label);
//                    frame.repaint();
//
//                    new Timer(2000, new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//                            frame.remove(label);
//                            frame.repaint();
//                        }
//                    }).start();
//
//                } catch (IOException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
//        });
//        frame.add(decompress);
//
//        frame.setVisible(true);
//    }
}
