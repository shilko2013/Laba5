package test;

import com.shilko.ru.Client;
import org.junit.*;
import com.shilko.ru.Client.ClientGUI;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ComparingValuesTest {
    private static Object comparingValues;

    @BeforeClass
    public static void init() throws Exception {
        Constructor constructor = Client.ClientGUI.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ClientGUI clientGUI = (Client.ClientGUI)constructor.newInstance();
        Arrays.stream(clientGUI.getClass().getDeclaredClasses()).forEach(e -> {
            System.out.println(e);
            try {
                if (e.toString().contains("com.shilko.ru.Client$ClientGUI$ComparingValues")) {
                    Constructor constructor1 = e.getDeclaredConstructors()[0];
                    constructor1.setAccessible(true);
                    System.out.println(constructor1.getParameterTypes()[0]);
                    comparingValues = constructor1.newInstance(clientGUI);
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @AfterClass
    public static void delete() {
        comparingValues = null;
    }

    @Test(timeout = 1000)
    public void main() {
        Arrays.stream(comparingValues.getClass().getDeclaredFields()).forEach(e->{
            try {
                e.setAccessible(true);
                assertNotNull(e.get(comparingValues));
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }
}
