public class Quantizer {
    int quantized;
    int lowerVal;
    int upperVal;
    int dequantized;
    private final int step = 64;
    Quantizer(int quantize, int lowerVal){
        this.quantized = quantize;
        this.lowerVal = lowerVal;
        this.upperVal = lowerVal + step;
        this.dequantized = (this.lowerVal+this.upperVal)/2;
    }
}
