package com.rhewumapp.Activity.MeshConveterData;
import java.util.HashMap;
import java.util.Map;

public class MeshConverterUtils {
    // Example conversion methods (implement the actual conversion logic)
    // Conversion data: each key is a DIN value and the corresponding value is an array containing DIN, ASTM, and Tyler equivalents
    private static final Map<Double, String[]> conversionMap = new HashMap<>();
    private static final Map<Double, Double> dinToAstmMap = new HashMap<>();
    private static final Map<Double, Double> dinToTylerMap = new HashMap<>();
    private static final Map<Double, Double> astmToDinMap = new HashMap<>();
    private static final Map<Double, Double> astmToTylerMap = new HashMap<>();
    private static final Map<Double, Double> tylerToDinMap = new HashMap<>();
    private static final Map<Double, Double> tylerToAstmMap = new HashMap<>();

    static {
        // Populate the conversion data
        conversionMap.put(125.0, new String[]{"125 mm", "5.0 inch", "No. 12", "12 Mesh"});
        conversionMap.put(112.0, new String[]{"112 mm", "-", "-", "-"});
        conversionMap.put(106.0, new String[]{"106 mm", "4.24 inch", "No. 14", "14 Mesh"});
        conversionMap.put(100.0, new String[]{"100 mm", "4.0 inch", "-", "-"});
        conversionMap.put(90.0, new String[]{"90 mm", "3 1/2 inch", "No. 16", "16 Mesh"});

        // Add remaining conversion data based on the provided list...
        dinToAstmMap.put(125.0, 5.0);
        dinToTylerMap.put(125.0, 12.0);
        astmToDinMap.put(5.0, 125.0);
        astmToTylerMap.put(5.0, 12.0);
        tylerToDinMap.put(12.0, 125.0);
        tylerToAstmMap.put(12.0, 5.0);
    }


    public double convertDinToAstm(double dinValue) {
        return dinToAstmMap.getOrDefault(dinValue, -1.0);
    }

    public double convertDinToTyler(double dinValue) {
        return dinToTylerMap.getOrDefault(dinValue, -1.0);
    }

    public double convertAstmToDin(double astmValue) {
        return astmToDinMap.getOrDefault(astmValue, -1.0);
    }

    public double convertAstmToTyler(double astmValue) {
        return astmToTylerMap.getOrDefault(astmValue, -1.0);
    }

    public double convertTylerToDin(double tylerValue) {
        return tylerToDinMap.getOrDefault(tylerValue, -1.0);
    }

    public double convertTylerToAstm(double tylerValue) {
        return tylerToAstmMap.getOrDefault(tylerValue, -1.0);
    }
    public String[] convertDinToOthers(double dinValue) {
        return conversionMap.getOrDefault(dinValue, new String[]{"-", "-", "-", "-"});
    }
}

