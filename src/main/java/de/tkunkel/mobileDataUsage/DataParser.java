package de.tkunkel.mobileDataUsage;

public class DataParser {

    public int extractContractVolume(String input) {
        int from = input.indexOf("von");
        int to = input.indexOf("verbraucht");
        String size = input.substring(from+3, to);
        if (size.contains("TB")) {
            size = size.replace("TB", "");
            size=size.trim();
            size=size.replace(",",".");
            return Math.round(Float.parseFloat(size) * 1024 * 1024);
        } else if (size.contains("GB")) {
            size = size.replace("GB", "");
            size=size.trim();
            size=size.replace(",",".");
            return Math.round(Float.parseFloat(size) * 1024);
        } else if (size.contains("MB")) {
            size = size.replace("MB", "");
            size=size.trim();
            size=size.replace(",",".");
            return Math.round(Float.parseFloat(size));
        }
        return -1;
    }

    public int extractUsedMemoryInMB(String input) {
        int from = "Datenverbrauch".length();
        int to = input.indexOf("von");
        String size = input.substring(from, to);
        if (size.contains("TB")) {
            size = size.replace("TB", "");
            size=size.trim();
            size=size.replace(",",".");
            return Math.round(Float.parseFloat(size) * 1024 * 1024);
        } else if (size.contains("GB")) {
            size = size.replace("GB", "");
            size=size.trim();
            size=size.replace(",",".");
            return Math.round(Float.parseFloat(size) * 1024);
        } else if (size.contains("MB")) {
            size = size.replace("MB", "");
            size=size.trim();
            size=size.replace(",",".");
            return Math.round(Float.parseFloat(size));
        }
        return -1;
    }
}
