import java.io.*;
import java.util.*;

public class Compression {
    int imageH, imageW;
    QuantizerTable quantizer = new QuantizerTable();

    ArrayList<ArrayList<Integer>> image;
    ArrayList<ArrayList<Integer>> predictedImage = new ArrayList<>();
    ArrayList<ArrayList<Integer>> difference = new ArrayList<>();
    ArrayList<ArrayList<Integer>> quantizedDifference = new ArrayList<>();
    ArrayList<ArrayList<Integer>> de_quantizedDifference = new ArrayList<>();
    ArrayList<ArrayList<Integer>> decodedImage = new ArrayList<>();
    Compression(ArrayList<ArrayList<Integer>> image, int h, int w){
        this.image = image;
        this.imageW = w;
        this.imageH = h;
    }
    void initiateAllMatrices(){
        for (int row = 0; row < imageH; row++){
            predictedImage.add(new ArrayList<>());
            decodedImage.add(new ArrayList<>());
            difference.add(new ArrayList<>());
            quantizedDifference.add(new ArrayList<>());
            de_quantizedDifference.add(new ArrayList<>());
            for (int col = 0; col < imageW; col++){
                if (col == 0 || row == 0){
                    predictedImage.get(row).add(image.get(row).get(col));
                    decodedImage.get(row).add(image.get(row).get(col));
                    difference.get(row).add(image.get(row).get(col));
                    quantizedDifference.get(row).add(image.get(row).get(col));
                    de_quantizedDifference.get(row).add(image.get(row).get(col));
                }
                else{
                    break;
                }
            }
        }
    }
    void calculatePredictions(){
        for (int row = 1; row < imageH; row++){
            for (int col = 1; col < imageW; col++){
                int a = decodedImage.get(row).get(col-1);
                int b = decodedImage.get(row-1).get(col-1);
                int c = decodedImage.get(row-1).get(col);
                if (b <= Math.min(a, c)) {
                    predictedImage.get(row).add(Math.max(a, c));
                }
                else if (b >= Math.max(a, c)) {
                    predictedImage.get(row).add(Math.min(a, c));
                }
                else {
                    predictedImage.get(row).add(a + c - b);
                }
                difference.get(row).add(image.get(row).get(col) - predictedImage.get(row).get(col));
                calculateQuantizedAndDequantizedDifference(row, col);
                int p = predictedImage.get(row).get(col);
                int d = de_quantizedDifference.get(row).get(col);
                if (Math.abs(d) > p) decodedImage.get(row).add(p+ (Math.abs(d)/2));
                else decodedImage.get(row).add(p + d);
            }
        }
    }
    public void print(){
        for (int i = 0; i < imageH; i++){
            for (int j = 0; j < imageW; j++){
                System.out.print(decodedImage.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }

    void calculateQuantizedAndDequantizedDifference(int i, int j){
        int x = quantizer.searchToQuantize(difference.get(i).get(j));
        quantizedDifference.get(i).add(x);
        int y = quantizer.searchToDequantize(difference.get(i).get(j));
        de_quantizedDifference.get(i).add(y);
    }

    public ArrayList<ArrayList<Integer>> compress(){
        initiateAllMatrices();
        calculatePredictions();
        return decodedImage;
    }


//    public void compress(String inputFilePath, String outputFilePath) throws FileNotFoundException {
//        StringBuilder originalStream = new StringBuilder();
//        Integer numLines = 0;
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
//            // read file to string and count lines
//            while (reader.ready()) {
//                originalStream.append(reader.readLine());
//                originalStream.append('\n');
//                numLines++;
//            }
//            reader.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (originalStream.toString().isEmpty()) {
//            return;
//        }
//
//        Integer codebookLength = 4;
//        Integer vectorHeight = 2;
//        Integer vectorWidth = 2;
//        Integer vectorLength = vectorWidth * vectorHeight;
//        Integer numVectors = (int) Math.pow((double) numLines, 2) / vectorLength;
//
//        // extract vectors from strings
//        String[] lines = originalStream.toString().split("\n");
//        List<String[]> initialVectors = new ArrayList<>();
//
//        for (int i = 0; i < numLines; i++) {
//            initialVectors.add(lines[i].split(" "));
//        }
//
//        // build custom-sized vectors
//        List<Vector<Double>> vectors = new ArrayList<>();
//
//        for (int i = 0; i < numLines; i += vectorHeight) {
//            for (int j = 0; j < numLines; j += vectorWidth) {
//
//                Vector<Double> currentVector = new Vector<>(vectorLength);
//                for (int k = 0; k < vectorHeight; k++) {
//                    for (int l = 0; l < vectorWidth; l++) {
//                        currentVector.add(Double.valueOf(initialVectors.get(i + k)[j + l]));
//
//                    }
//                }
//
//                vectors.add(currentVector);
//            }
//        }
//
//        List<Vector<Double>> codebook = buildCodebook(vectors, codebookLength);
//
////        System.out.println(codebook);
////
////        for (int i = 0; i < numVectors; i++) {
////            System.out.println(vectors.get(i) + " -> " + findClosestCodebookVector(vectors.get(i), codebook));
////        }
//
//        // match labels to suitable vectors
//        Vector<Integer> labels = new Vector<>();
//        for (int i = 0; i < numVectors; i++) {
//            labels.add(findClosestCodebookVector(vectors.get(i), codebook));
//        }
//        // save the output
//        BinaryFilesHandler.writeCompressedOutput(codebook, labels, outputFilePath);
//
//    }
//
//    private List<Vector<Double>> buildCodebook(List<Vector<Double>> initialVectors, Integer requiredSize) {
//
//        Integer numVectors = initialVectors.size();
//        List<Vector<Double>> codebook = new ArrayList<>();
//
//        // calculate first average vector
//        Vector<Double> codebookInitialVector = new Vector<>();
//        for (int i = 0; i < initialVectors.get(0).size(); i += 1) {
//            Double avg = 0.0;
//            for (int j = 0; j < numVectors; j += 1) {
//                avg += initialVectors.get(j).get(i);
//            }
//            codebookInitialVector.add(avg / numVectors);
//        }
//        codebook.add(codebookInitialVector);
//
//        // loop till filling codebook
//        while (codebook.size() < requiredSize) {
//
//            List<Vector<Double>> updatedCodebook = new ArrayList<>();
//
//            // do the splitting for every codebook vector
//            for (int i = 0; i < codebook.size(); i += 1) {
//                updatedCodebook.add(splitVector(codebook.get(i), -1.0));
//                updatedCodebook.add(splitVector(codebook.get(i), 1.0));
//            }
//
//            List<Vector<Double>> updatedCodebook2 = new ArrayList<>();
//
//            List<Vector<Vector<Double>>> quantized = quantize(initialVectors, updatedCodebook);
//
//            // calculate averages for every matched group separately
//            for (int i = 0; i < quantized.size(); i++) {
//
//                Vector<Double> codeBookVector = new Vector<>();
//                for (int j = 0; j < quantized.get(i).get(0).size(); j += 1) {
//                    Double avg = 0.0;
//                    for (int k = 0; k < quantized.get(i).size(); k++) {
//                        avg += quantized.get(i).get(k).get(j);
//                    }
//                    codeBookVector.add(avg / quantized.get(i).size());
//                }
//                updatedCodebook2.add(codeBookVector);
//
//            }
//
//            codebook = updatedCodebook;
//        }
//
//        List<Vector<Double>> updatedCodebook2;
//
//        // loop till no changed groups
//        while (true) {
//
//            updatedCodebook2 = new ArrayList<>();
//
//            List<Vector<Vector<Double>>> quantized = quantize(initialVectors, codebook);
//
//            // calculate averages for every matched group separately
//            // same as previous loop
//            for (int i = 0; i < quantized.size(); i++) {
//
//                Vector<Double> codeBookVector = new Vector<>();
//                for (int j = 0; j < quantized.get(i).get(0).size(); j += 1) {
//                    Double avg = 0.0;
//                    for (int k = 0; k < quantized.get(i).size(); k++) {
//                        avg += quantized.get(i).get(k).get(j);
//                    }
//                    codeBookVector.add(avg / quantized.get(i).size());
//                }
//                updatedCodebook2.add(codeBookVector);
//
//            }
//
//            // stop when no change
//            if (codebook.equals(updatedCodebook2)) {
//                break;
//            }
//
//            codebook = updatedCodebook2;
//        }
//
//        return updatedCodebook2;
//    }
//
//    private Vector<Double> splitVector(Vector<Double> initialVector, Double scale) {
//        Vector<Double> split = new Vector<>(initialVector.size());
//        for (int i = 0; i < initialVector.size(); i++) {
//            if (initialVector.get(i) % 1 == 0) {
//                split.add(initialVector.get(i) + (scale));
//            } else if (scale > 0) {
//                split.add(Math.ceil(initialVector.get(i)));
//            } else if (scale < 0) {
//                split.add(Math.floor(initialVector.get(i)));
//            }
//        }
//        return split;
//    }
//
//    private List<Vector<Vector<Double>>> quantize(List<Vector<Double>> inputVectors, List<Vector<Double>> codebook) {
//
//        // group vectors in a vector with index equal to its index in codebook
//
//        List<Vector<Vector<Double>>> quantizedIndices = new ArrayList<>(codebook.size());
//
//        for (int i = 0; i < codebook.size(); i++) {
//            quantizedIndices.add(new Vector<>());
//        }
//
//        // do the vector-codebook matching
//        for (Vector<Double> inputVector : inputVectors) {
//            int bestIndex = findClosestCodebookVector(inputVector, codebook);
//            quantizedIndices.get(bestIndex).add(inputVector);
//        }
//        return quantizedIndices;
//    }
//
//    private Integer findClosestCodebookVector(Vector<Double> inputVector, List<Vector<Double>> codebook) {
//        // find index of best matching codebook vector to the input vector
//        Integer bestIndex = 0;
//        Double bestDistance = Double.MAX_VALUE;
//        for (int i = 0; i < codebook.size(); i++) {
//            Double distance = calculateDistance(inputVector, codebook.get(i));
//            if (distance < bestDistance) {
//                bestDistance = distance;
//                bestIndex = i;
//            }
//        }
//        return bestIndex;
//    }
//
//    private Double calculateDistance(Vector<Double> vector1, Vector<Double> vector2) {
//        Double sum = 0.0;
//        for (int i = 0; i < vector1.size(); i++) {
//            sum += Math.pow(vector1.get(i) - vector2.get(i), 2);
//        }
//        return Math.sqrt(sum);
//    }


//    public void decompress(String compressedFilePath, String decompressedFilePath) throws IOException {
//        Vector<Integer> compressionValues = BinaryFilesHandler.readCompressedFile(compressedFilePath);
//        int codebookSize = compressionValues.get(0);
//        int singleVectorLength = compressionValues.get(1);
//        int numberOfVectors = compressionValues.get(2);
//        Vector<Vector<Integer>> codebook = new Vector<>();
//        int endOverHead = 3 + (codebookSize * singleVectorLength);
//        for (int i = 3; i < endOverHead; i++) {
//            Vector<Integer> rowInCodebook = new Vector<>(singleVectorLength);
//            for (int j = 0; j < singleVectorLength; j++) {
//                rowInCodebook.add(compressionValues.get(i++));
//            }
//            codebook.add(rowInCodebook);
//            i--;
//        }
//        int originalSize = numberOfVectors * singleVectorLength;
//        int originalDimension = (int) Math.sqrt(originalSize);
//        int blockDimension = (int) Math.sqrt(singleVectorLength);
//        Vector<String> original = new Vector<>();
//        int numBlockInRow = originalDimension / blockDimension;
//        for (int i = endOverHead; i < compressionValues.size(); i++) {
//            Vector<Vector<Integer>> tempCodebook = new Vector<>();
//            int temp = numBlockInRow;
//            while (temp != 0) {
//                tempCodebook.add(codebook.get(compressionValues.get(i)));
//                i++;
//                temp--;
//            }
//            int times = blockDimension * blockDimension;
//            int index = 0;
//            while (index < times) {
//                StringBuilder row = new StringBuilder();
//                for (int j = 0; j < tempCodebook.size(); j++) {
//                    int temp2 = index;
//                    while (temp2 != (index + blockDimension)) {
//                        row.append(Integer.toString(tempCodebook.get(j).get(temp2)));
//                        row.append(" ");
//                        temp2++;
//                    }
//                }
//                index += blockDimension;
//                original.add(row.toString());
//            }
//            i--;
//        }
////        File file = new File(System.getProperty("user.dir") + "/decompressedOutput" + ".txt");
//        File file = new File(decompressedFilePath);
//        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//        try {
//            for (String row : original) {
//                StringBuilder sb = new StringBuilder(row);
//                sb.deleteCharAt(row.length() - 1);
//                writer.write(sb.toString());
//                writer.write("\n");
//            }
//            writer.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
