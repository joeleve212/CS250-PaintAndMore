import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import paintv0.PaintV0;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureTest {
//    @Test
//    public void testStart() {
//        System.out.println("start");
//        Stage primaryStage = new Stage();
//        paintv0.PaintV0 instance = new paintv0.PaintV0();
//        instance.start(primaryStage);
//        // TODO review the generated test code and remove the default call to fail.
//        //fail("The test case is a prototype.");
//    }
    private PaintV0 paint;
    public FeatureTest(){
        paint = new PaintV0();

    }

    /**
     * Test of main method, of class LayoutTestingApp.
     */
//    @Test
//    public void testMain() {
//        paintv0.PaintV0 testObj = new PaintV0();
//        String[] args = null;
//        paintv0.PaintV0.main(args);
//        assertNotNull(testObj); //make sure that top menus got initialized
//        //assertNull(testObj.getCanv());
//
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    @org.junit.Test
    public void lineColorTest(){
        assertEquals(paint.fillColor.getValue(),paint.menus.getLineColor());
        System.out.println("Line Color Test Passed");
    }
    @org.junit.Test
    public void fillColorTest(){
        assertEquals(paint.fillColor.getValue(),paint.menus.getFillColor());
        System.out.println("Fill Color Test Passed");
    }
}