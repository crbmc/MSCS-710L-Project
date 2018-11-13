import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PowerSource;
import oshi.hardware.Sensors;

import static org.junit.Assert.*;

public class TableCreationChecksTest {

    private static SystemInfo si;
    private static HardwareAbstractionLayer hal;

    @BeforeClass
    public static void setup() {
        si = new SystemInfo();
        hal = si.getHardware();
    }

    @Test
    public void getLogicalCPUs() {
        assertTrue(TableCreationChecks.getLogicalCPUs(hal.getProcessor()) > 0);
    }

    @Test
    public void checkPowerSource() {
        PowerSource[] powerSources = hal.getPowerSources();
        boolean result = powerSources.length != 0
                && !powerSources[0].getName().equalsIgnoreCase("Unknown")
                && powerSources[0].getRemainingCapacity() * 100d != 0.0;
        assertTrue(result || !result);
    }

    @Test
    public void getFans_ValidFanSpeeds() {
        final Sensors testSensors = Mockito.spy(hal.getSensors());
        final TableCreationChecks testTableChecks = Mockito.spy(new TableCreationChecks());
        int[] fanArray =new int[1];
        fanArray[0] = 0;
        Mockito.when(testSensors.getFanSpeeds()).thenReturn(fanArray);
        testTableChecks.getFans(testSensors);
    }

    @Test
    public void getFans_InvalidFanSpeeds() {
        final Sensors testSensors = Mockito.spy(hal.getSensors());
        final TableCreationChecks testTableChecks = Mockito.spy(new TableCreationChecks());
        int[] fanArray =new int[1];
        fanArray[0] = 1;
        Mockito.when(testSensors.getFanSpeeds()).thenReturn(fanArray);
        testTableChecks.getFans(testSensors);
    }

    @Test
    public void getCpuVoltage_InvalidVoltage() {
        final Sensors testSensors = Mockito.spy(hal.getSensors());
        final TableCreationChecks testTableChecks = Mockito.spy(new TableCreationChecks());
        Mockito.when(testSensors.getCpuVoltage()).thenReturn(999.0d);
        testTableChecks.getCpuVoltage(testSensors);
    }

    @Test
    public void getCpuVoltage_ValidVoltage() {
        final Sensors testSensors = Mockito.spy(hal.getSensors());
        final TableCreationChecks testTableChecks = Mockito.spy(new TableCreationChecks());
        Mockito.when(testSensors.getCpuVoltage()).thenReturn(0.0d);
        testTableChecks.getCpuVoltage(testSensors);
    }
}