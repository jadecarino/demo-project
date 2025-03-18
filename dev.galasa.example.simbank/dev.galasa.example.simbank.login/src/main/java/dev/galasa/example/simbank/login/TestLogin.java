package dev.galasa.example.simbank.login;

import static org.assertj.core.api.Assertions.*;

import dev.galasa.Test;
import dev.galasa.zos.IZosImage;
import dev.galasa.zos.ZosImage;
import dev.galasa.zos3270.ITerminal;
import dev.galasa.zos3270.Zos3270Terminal;
import dev.galasa.core.manager.*;

@Test
public class TestLogin {

	// 1. Inject Managers into the test with annotations.

	@ZosImage(imageTag = "SIMBANK")
	public IZosImage image;

	@Zos3270Terminal(imageTag = "SIMBANK")
	public ITerminal terminal;

	@CoreManager
	public ICoreManager coreManager;

	@Test
	public void checkBankIsAvailable() throws Exception {

		// 2. Register password as confidential text so it's not shown in any run logs.
		coreManager.registerConfidentialText("SYS1", "IBMUSER password");

		// 3. Enter user ID and password with 3270 terminal.
		terminal.waitForKeyboard()
			.positionCursorToFieldContaining("Userid").tab().type("IBMUSER")
			.positionCursorToFieldContaining("Password").tab().type("SYS1")
			.enter().waitForKeyboard();

		// 4. Check that the screen after login is displayed on the screen.
		assertThat(terminal.retrieveScreen()).containsOnlyOnce("SIMPLATFORM MAIN MENU");
		assertThat(terminal.retrieveScreen()).containsOnlyOnce("BANKTEST");

		terminal.pf1().waitForKeyboard().clear().waitForKeyboard();
		
		// 5. Open the banking application.
		terminal.type("bank").enter().waitForKeyboard();

		// 6. Check that the banking application menu is displayed on the screen.
		assertThat(terminal.retrieveScreen()).containsOnlyOnce("Options     Description        PFKey ");
		assertThat(terminal.retrieveScreen()).containsOnlyOnce("BROWSE      Browse Accounts    PF1");
		assertThat(terminal.retrieveScreen()).containsOnlyOnce("TRANSF      Transfer Money     PF4");
		
	}
}
