import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.katanya04.anotherguiplugin.AnotherGUIPlugin;
import mx.towers.pato14.AmazingTowers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Main {
    private ServerMock server;
    private AmazingTowers plugin;
    private AnotherGUIPlugin guiPlugin;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        guiPlugin = MockBukkit.load(AnotherGUIPlugin.class);
        plugin = MockBukkit.load(AmazingTowers.class);
    }

    @Test
    public void test() {
        System.out.println("AAAAA");
    }
    @After
    public void tearDown() {
        MockBukkit.unload();
    }
}
