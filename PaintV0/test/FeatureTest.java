import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeatureTest {
    /**
     * Test of start method, of class LayoutTestingApp.
     */
    @Test
    public void testStart() {
        System.out.println("start");
        Stage primaryStage = null;
        paintv0.PaintV0 instance = new paintv0.PaintV0();
        instance.start(primaryStage);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class LayoutTestingApp.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        paintv0.PaintV0.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}