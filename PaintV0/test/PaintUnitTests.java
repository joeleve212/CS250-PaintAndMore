package paintv0;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.junit.Test;
import static org.junit.Assert.*;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

import java.util.Stack;

public class PaintUnitTests {
    @Test
    public void checkLineColor(){
        paintv0.TopMenus menu = new paintv0.TopMenus(null,null,null, null);
        assertEquals(null,menu.getLineColor());
        System.out.println("Line Color Test Passed");
    }
    @Test
    public void checkFillColor(){
        paintv0.TopMenus menu = new paintv0.TopMenus(null,null,null, null);
        assertEquals(null,menu.getFillColor());
        System.out.println("Fill Color Test Passed");
    }
    @Test
    public void checkUndoVersion(){
        paintv0.PaintV0 program = new paintv0.PaintV0();
        assertNotSame(null, program.getCurrVersion());
        System.out.println("Undo Test Passed");
    }

}
