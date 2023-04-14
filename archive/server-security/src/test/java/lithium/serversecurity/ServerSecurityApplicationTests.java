package lithium.serversecurity;

import lithium.server.security.ServerSecurityApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ServerSecurityApplicationTests {

    @Mock ServerSecurityApplication serverSecurityApplication;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(serverSecurityApplication);
    }
}
