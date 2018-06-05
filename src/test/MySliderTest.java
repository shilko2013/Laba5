package test;

import com.shilko.ru.Client;
import org.junit.*;
import com.shilko.ru.Client.ClientGUI;
import javax.swing.*;
import java.lang.reflect.*;
import java.util.*;

import static org.junit.Assert.*;

public class MySliderTest {
    private static Object mySlider;
    private static ClientGUI clientGUI;
    private static Constructor constructor;
    private static Class mySliderClass;
    private static int min = 1, max = 2;

    @BeforeClass
    public static void init() throws Exception {
        Constructor constructor = Client.ClientGUI.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ClientGUI superClass = (Client.ClientGUI)constructor.newInstance();
        Arrays.stream(superClass.getClass().getDeclaredClasses()).forEach(e -> {
                if (e.toString().contains("com.shilko.ru.Client$ClientGUI$MySlider")) {
                    mySliderClass = e;
                    Constructor constructor1 = null;
                    try {
                        constructor1 = e.getDeclaredConstructor(superClass.getClass(),String.class,int.class,int.class,int.class,int.class,int.class);
                    } catch (NoSuchMethodException e1) {
                        e1.printStackTrace();
                    }
                    constructor1.setAccessible(true);
                    clientGUI = superClass;
                    setConstructor(constructor1);
                }
        });
    }

    private static void setConstructor(Constructor constructor) {
        MySliderTest.constructor = constructor;
    }

    @Before
    public void addAll() throws Throwable {
        mySlider = (constructor.newInstance(clientGUI,"",-5,5,5,4,5));
        mySlider = (constructor.newInstance(clientGUI,"",-5,-2,-3,10,20));
        mySlider = (constructor.newInstance(clientGUI,"dfdfdf",min,max,max,2,1));
        try {
            mySlider = (constructor.newInstance(clientGUI, null, null, null, null, null, null));
            throw new Exception();
        } catch (ReflectiveOperationException | IllegalArgumentException ignored) { }
        try {
            mySlider = (constructor.newInstance(clientGUI,"2",1,2,3,5,5));
            throw new Exception();
        } catch (ReflectiveOperationException | IllegalArgumentException ignored) { }
        try {
            mySlider = (constructor.newInstance(clientGUI,"2",2,1,2,4,4));
            throw new Exception();
        } catch (ReflectiveOperationException | IllegalArgumentException ignored) { }
        try {
            mySlider = (constructor.newInstance(clientGUI,"wcffca",2,2,2,-1,-2));
            throw new Exception();
        } catch (ReflectiveOperationException | IllegalArgumentException ignored) { }
    }

    @AfterClass
    public static void delete() {
        mySlider = null;
    }

    @Test(timeout = 1000)
    public void setAndGetMyValueTest() throws Exception {
        Field myValue = mySliderClass.getDeclaredField("myValue");
        myValue.setAccessible(true);
        Method setMyValue = mySliderClass.getDeclaredMethod("setMyValue",int.class);
        setMyValue.setAccessible(true);
        Method getMyValue = mySliderClass.getDeclaredMethod("getMyValue");
        getMyValue.setAccessible(true);
        setMyValue.invoke(mySlider,min);
        assertEquals(min,getMyValue.invoke(mySlider));
        setMyValue.invoke(mySlider,max);
        assertEquals(max,getMyValue.invoke(mySlider));
        try {
            setMyValue.invoke(mySlider,max+1);
            throw new Exception();
        } catch (ReflectiveOperationException | IllegalArgumentException ignored) { }
        assertEquals(max,getMyValue.invoke(mySlider));
        try {
            setMyValue.invoke(mySlider,min-1);
            throw new Exception();
        } catch (ReflectiveOperationException | IllegalArgumentException ignored) { }
        assertEquals(max,getMyValue.invoke(mySlider));
        try {
            setMyValue.invoke(mySlider,new Object[]{null});
            throw new Exception();
        } catch (ReflectiveOperationException | IllegalArgumentException ignored) { }
        assertEquals(max,getMyValue.invoke(mySlider));
    }

    @Test(timeout = 1000)
    public void getPanelTest() throws Exception {
        Method getPanel = mySliderClass.getDeclaredMethod("getPanel");
        getPanel.setAccessible(true);
        Method setText = mySliderClass.getDeclaredMethod("setText",String.class);
        setText.setAccessible(true);
        assertNotNull(getPanel.invoke(mySlider));
        setText.invoke(mySlider,"string");
        assertEquals(((JLabel)((JPanel)getPanel.invoke(mySlider)).getComponent(0)).getText(),"string");
        try {
            setText.invoke(mySlider, new Object[]{null});
            throw new Exception();
        } catch (ReflectiveOperationException | IllegalArgumentException ignored) { }
    }
}