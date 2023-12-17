import java.util.ArrayList;

public class QuantizerTable {
    ArrayList<Quantizer> quantizerTable = new ArrayList<>();
    QuantizerTable(){
        int i = 0, j = 0;
        while (i <= 256){
            Quantizer q = new Quantizer(i, j++);
            quantizerTable.add(q);
            i += q.upperVal + 1;
        }
    }
    public int searchToQuantize(int value){
        for (int i = 0; i < quantizerTable.size(); i++){
            if (quantizerTable.get(i).lowerVal <= value && value <= quantizerTable.get(i).upperVal){
                return quantizerTable.get(i).quantized;
            }
        }
        return -1;
    }
    public int searchToDequantize(int value){
        for (int i = 0; i < quantizerTable.size(); i++){
            if (quantizerTable.get(i).quantized == value){
                return quantizerTable.get(i).dequantized;
            }
        }
        return -1;
    }
}
