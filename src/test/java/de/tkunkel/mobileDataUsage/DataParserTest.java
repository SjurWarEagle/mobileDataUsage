package de.tkunkel.mobileDataUsage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataParserTest {

    @Test
    public void testExtractContractVolume_simple() {
        DataParser dataParser = new DataParser();
        int memory = dataParser.extractContractVolume("Datenverbrauch 1,07 GB von 5,00 GB verbraucht");
        Assertions.assertEquals(5120, memory);

        memory = dataParser.extractContractVolume("Datenverbrauch 4,5 GB von 4,50 GB verbraucht");
        Assertions.assertEquals(4608, memory);

        memory = dataParser.extractContractVolume("Datenverbrauch 107 MB von 500 MB verbraucht");
        Assertions.assertEquals(500, memory);
    }

    @Test
    public void testExtractUsedMemoryInMB_simple() {
        DataParser dataParser = new DataParser();
        int memory = dataParser.extractUsedMemoryInMB("Datenverbrauch 1,07 GB von 5,00 GB verbraucht");
        Assertions.assertEquals(1096, memory);

        memory = dataParser.extractUsedMemoryInMB("Datenverbrauch 4,5 GB von 5,00 GB verbraucht");
        Assertions.assertEquals(4608, memory);

        memory = dataParser.extractUsedMemoryInMB("Datenverbrauch 107 MB von 5,00 GB verbraucht");
        Assertions.assertEquals(107, memory);
    }
}
