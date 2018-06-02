package test;

import com.shilko.ru.Client;
import org.junit.*;
import java.util.logging.Level;

import static org.junit.Assert.*;

public class ClientTest {
    /*Client client;
    @BeforeClass
    public void initClient() {
        client = new Client();
    }
    @AfterClass
    public void deleteClient() {
        client = null;
    }

    /*@Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        assertNotNull(Client.getLogger());
        assertEquals(Client.getLogger().getHandlers()[0].getLevel(),Level.INFO);
    }*/

    @Test(timeout = 1000)
    public void main() {
        Client.main();
        assertNotNull(Client.getLogger());
        assertEquals(Client.getLogger().getHandlers()[0].getLevel(),Level.INFO);
    }

}