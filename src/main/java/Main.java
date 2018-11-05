import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    private static final Logger log = LogManager.getLogger(UI.class);

    public static AtomicBoolean applicationOpen = new AtomicBoolean(true);

    public static void main(String[] args) {

        // Create the database
        Database db = new Database();

        // Start the UI
        UI ui = new UI();
        Thread uiThread = new Thread(ui);
        uiThread.start();

        while (Main.applicationOpen.get()) {
            try {
                SystemInfo si = new SystemInfo();
                final HardwareAbstractionLayer hal = si.getHardware();
                final CountDownLatch mainLatch = new CountDownLatch(5);

                //Starts the getPower method to collect info for Power Table
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MetricCollector.getPower(mainLatch, hal.getPowerSources());
                    }
                }).start();

                //Starts the getCpu method to collect info for CPU Table
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MetricCollector.getCPU(mainLatch, hal.getProcessor());
                    }
                }).start();

                //Starts the getSensors method to collect info for Sensors Table
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MetricCollector.getSensors(mainLatch, hal.getSensors());
                    }
                }).start();

                //Starts the getMemory method to collect info for Memory Table
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MetricCollector.getMemory(mainLatch, hal.getMemory());
                    }
                }).start();

                //Starts the getNetwork method to collect info for Network Table
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MetricCollector.getNetwork(mainLatch, hal.getNetworkIFs());
                    }
                }).start();

                mainLatch.await();
                System.out.println("----->Started to count down 5 seconds"); //Sysout
                Thread.sleep(5000);
                System.out.println("<-----Finished countdown of 5 seconds"); //Sysout
            } catch(Exception e){
                e.printStackTrace();
                log.error("Failed to Setup the latch and concurrent method calls");
            }

        }


        System.out.println("End of Main"); //Sysout
    }
}