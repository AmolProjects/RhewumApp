package com.rhewumapp.Utils;

import java.util.ArrayList;
import java.util.List;

public class MaterialList {
    public List<Material> getMaterials() {
        List<Material> materialList = new ArrayList<>();

        materialList.add(new Material("Ammonium nitrate", "900 - 1400", "56 - 87"));
        materialList.add(new Material("Ash", "400 - 1300", "25 - 81"));
        materialList.add(new Material("Bentonite", "700 - 950", "44 - 59"));
        materialList.add(new Material("Bone powder", "500 - 700", "31 - 44"));
        materialList.add(new Material("Cellulose", "100 - 800", "6 - 50"));
        materialList.add(new Material("Coffee", "400 - 600", "25 - 37"));
        materialList.add(new Material("Coke", "900 - 1100", "56 - 69"));
        materialList.add(new Material("Glass fibers", "100 - 400", "6 - 25"));
        materialList.add(new Material("Graphite", "800 - 1000", "50 - 62"));
        materialList.add(new Material("Gravel", "1200 - 1800", "75 - 112"));
        materialList.add(new Material("Gypsum", "800 - 1000", "50 - 62"));
        materialList.add(new Material("NPK-Fertilizer", "800 - 1200", "50 - 75"));
        materialList.add(new Material("Plastic granules", "100 - 700", "6 - 44"));
        materialList.add(new Material("Potash", "1000 - 1400", "62 - 87"));
        materialList.add(new Material("Rubber granules", "250 - 600", "16 - 37"));
        materialList.add(new Material("Salt", "1000 - 1500", "62 - 94"));
        materialList.add(new Material("Sand", "1200 - 1800", "75 - 112"));
        materialList.add(new Material("Seeds", "600 - 1100", "37 - 69"));
        materialList.add(new Material("Silica", "500 - 900", "31 - 56"));
        materialList.add(new Material("Silicone", "1200 - 1400", "75 - 87"));
        materialList.add(new Material("Slag", "1200 - 4000", "75 - 250"));
        materialList.add(new Material("Soda ash", "700 - 1100", "44 - 69"));
        materialList.add(new Material("Sugar", "700 - 900", "44 - 56"));
        materialList.add(new Material("Urea (prilled)", "700 - 1000", "44 - 62"));
        materialList.add(new Material("Wood chips", "100 - 300", "6 - 19"));

        return materialList;
    }
}

