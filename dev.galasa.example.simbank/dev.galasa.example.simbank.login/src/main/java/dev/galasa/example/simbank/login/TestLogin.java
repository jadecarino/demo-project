package dev.galasa.example.simbank.login;

import static org.assertj.core.api.Assertions.*;

import dev.galasa.ICredentialsUsernamePassword;
import dev.galasa.Test;
import dev.galasa.core.manager.CoreManager;
import dev.galasa.core.manager.ICoreManager;
import dev.galasa.zos.IZosImage;
import dev.galasa.zos.ZosImage;
import dev.galasa.zos3270.ITerminal;
import dev.galasa.zos3270.Zos3270Terminal;

@Test
public class TestLogin {

	// 1. Inject Managers into the test with annotations.

	@ZosImage(imageTag = "SIMBANK")
	public IZosImage image;

	@Zos3270Terminal(imageTag = "SIMBANK")
	public ITerminal terminal;

	@CoreManager
	public ICoreManager coreManager;

	private static final String CREDENTIALS_ID = "SIMBANK";

	@Test
	public void checkBankIsAvailable() throws Exception {
		// 2. Get the credentials that will be used to log in to SimBank using the Core Manager
		ICredentialsUsernamePassword credentials = (ICredentialsUsernamePassword) coreManager.getCredentials(CREDENTIALS_ID);

		// 3. Register password as confidential text so it's not shown in any run logs.
		String password = credentials.getPassword();
		coreManager.registerConfidentialText(password, "The password to access SimBank");

		// 4. Enter user ID and password with 3270 terminal.
		terminal.waitForKeyboard()
			.positionCursorToFieldContaining("Userid").tab().type(credentials.getUsername())
			.positionCursorToFieldContaining("Password").tab().type(credentials.getPassword())
			.enter().waitForKeyboard();

		// 5. Check that the screen after login is displayed on the screen.
		assertThat(terminal.retrieveScreen()).containsOnlyOnce("SIMPLATFORM MAIN MENU");
		assertThat(terminal.retrieveScreen()).containsOnlyOnce("BANKTEST");

		terminal.pf1().waitForKeyboard().clear().waitForKeyboard();
		
		// 6. Open the banking application.
		terminal.type("bank").enter().waitForKeyboard();

		// 7. Check that the banking application menu is displayed on the screen.
		assertThat(terminal.retrieveScreen()).containsOnlyOnce("Options     Description        PFKey ");
		assertThat(terminal.retrieveScreen()).containsOnlyOnce("BROWSE      Browse Accounts    PF1");
		assertThat(terminal.retrieveScreen()).containsOnlyOnce("TRANSF      Transfer Money     PF4");
		
	}
}
