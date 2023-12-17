import java.awt.*;
import java.util.ArrayList;

public class ColorGetter {
    ArrayList<ArrayList<Integer>> getReds(Color[][] image, int imgH, int imgW){
        ArrayList<ArrayList<Integer>> reds = new ArrayList<>();
        for (int i = 0; i < imgH; i++){
            reds.add(new ArrayList<>());
            for (int j = 0; j < imgW; j++){
                reds.get(i).add(image[i][j].getRed());
            }
        }
        return reds;
    }
    ArrayList<ArrayList<Integer>> getGreens(Color[][] image, int imgH, int imgW){
        ArrayList<ArrayList<Integer>> greens = new ArrayList<>();
        for (int i = 0; i < imgH; i++){
            greens.add(new ArrayList<>());
            for (int j = 0; j < imgW; j++){
                greens.get(i).add(image[i][j].getGreen());
            }
        }
        return greens;
    }
    ArrayList<ArrayList<Integer>> getBlues(Color[][] image, int imgH, int imgW){
        ArrayList<ArrayList<Integer>> blues = new ArrayList<>();
        for (int i = 0; i < imgH; i++){
            blues.add(new ArrayList<>());
            for (int j = 0; j < imgW; j++){
                blues.get(i).add(image[i][j].getBlue());
            }
        }
        return blues;
    }
}
