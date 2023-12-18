import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class FilesHandler {
    int imageH, imageW;
    Color[][] colors;
    public Color[][] get2DPixelArray(BufferedImage sampleImage) {
        imageW = sampleImage.getWidth();
        imageH = sampleImage.getHeight();
        ArrayList<ArrayList<Integer>> finalResult = new ArrayList<>();
        colors = new Color[imageH][imageW];
        for (int row = 0; row < imageH; row++) {
            for (int col = 0; col < imageW; col++) {
                colors[row][col] = new Color(sampleImage.getRGB(row, col));
            }
        }
//        for (int i = 0; i < imageH; i++){
//            finalResult.add(new ArrayList<>());
//            for (int j = 0; j < imageW; j++){
//                Color c = colors[i][j];
//                finalResult.get(i).add(c.getRed() + c.getBlue() + c.getGreen());
//            }
//        }
        return colors;
    }
    public void print(ArrayList<ArrayList<Integer>> image){
        for (int i = 0; i < imageH; i++){
            for (int j = 0; j < imageW; j++){
                System.out.print(image.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }
    public void printColors(){
        for (int i = 0; i < imageH; i++){
            for (int j = 0; j < imageW; j++){
                Color c = colors[i][j];
                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();
                System.out.print( "(" + r + " " + g + " " + b + ")");
            }
            System.out.println();
        }
    }

//    public static void writeCompressedOutput(List<Vector<Double>> codebook, Vector<Integer> labels, String outputFilePath) throws FileNotFoundException {
//        int codebookLength = codebook.size();
//        int singleVectorSize = codebook.get(0).size();
//        int numberOfVectors = labels.size();
//        int overheadSize = 3 + (codebookLength * singleVectorSize);
//        byte[] overhead  = new byte[overheadSize];
//        overhead[0] = toBinary(codebookLength);
//        overhead[1] = toBinary(singleVectorSize);
//        overhead[2] = toBinary(numberOfVectors);
//        int st = 3;
//        for(int i = 0; i < codebookLength; i++){
//            for(int j = 0; j < codebook.get(i).size(); j++){
//                overhead[st++] = toBinary((int) Math.round(codebook.get(i).get(j)));
//            }
//        }
//        byte[] mappedIndices = new byte[numberOfVectors];
//        for(int i = 0; i < numberOfVectors; i++){
//            mappedIndices[i] = toBinary(labels.get(i));
//        }
//        try {
//            FileOutputStream fileWriter = new FileOutputStream(outputFilePath);
//            for(int i = 0; i < overheadSize; i++){
//                fileWriter.write(overhead[i]);
//            }
//            for(int i = 0; i < numberOfVectors; i++){
//                fileWriter.write(mappedIndices[i]);
//            }
//            fileWriter.flush();
//            fileWriter.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static Vector<Integer> readCompressedFile(String filePath) throws FileNotFoundException {
//        File file = new File(filePath);
//        Vector<Integer> compressedValues = new Vector<>();
//
//        try {
//            FileInputStream fileIn = new FileInputStream(file);
//            DataInputStream dataIn = new DataInputStream(fileIn);
//            byte temp;
//            int value;
//            while((temp = (byte) dataIn.read()) != -1){
//                value = temp;
//                compressedValues.add(value);
//            }
//            fileIn.close();
//            dataIn.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return compressedValues;
//    }
//
    static byte toBinary(int num){
        StringBuilder binaryString = new StringBuilder();
        for(int i = 7; i >= 0; i--){
            int bit = (num >> i) & 1;
            binaryString.append(bit);
        }
        return binaryStringToByte(binaryString.toString());
    }

    static byte binaryStringToByte(String binaryString){
        int decimal = Integer.parseInt(binaryString, 2);
        return (byte) decimal;
    }
    
}
