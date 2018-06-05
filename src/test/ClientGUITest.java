package test;

import com.shilko.ru.Client;
import org.junit.*;

import java.lang.reflect.*;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ClientGUITest {
    private static Client.ClientGUI clientGUI;

    @BeforeClass
    public static void init() throws Exception {
        Constructor constructor = Client.ClientGUI.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        clientGUI = (Client.ClientGUI)constructor.newInstance();
    }

    @AfterClass
    public static void delete() {
        clientGUI = null;
    }

    @Test(timeout = 1000)
    public void main() {
        Arrays.stream(clientGUI.getClass().getDeclaredFields()).forEach(e->{
            try {
                e.setAccessible(true);
                assertNotNull(e.get(clientGUI));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
