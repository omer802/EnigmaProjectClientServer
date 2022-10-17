package servltes.utils;


import engine.api.ApiEnigma;
import registerManagers.Managers.RegisterManager;
import registerManagers.clients.UBoat;
import registerManagers.battlefieldManager.BattlefieldManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import registerManagers.Managers.GenericManager;
import registerManagers.Managers.ClientUser;

import static servltes.constants.Constants.INT_PARAMETER_ERROR;


public class ServletUtils {

	private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final String API_ENGINE_ATTRIBUTE_NAME = "apiEngine";
	private static final String BATTLEFIELD_MANAGER_ATTRIBUTE_NAME = "battleFieldManager";

	private static final String REGISTER_MANAGER_ATTRIBUTE_NAME = "registerManager";
	/*
	Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
	the actual fetch of them is remained un-synchronized for performance POV
	 */
	private static final Object enigmaApiLock = new Object();

	private static final Object BattlefieldManagerLock = new Object();
	private static final Object RegisterManagerLock = new Object();
	private static final Object UBoatLock = new Object();


	public static RegisterManager getRegisterManager(ServletContext servletContext) {

		synchronized (RegisterManagerLock) {
			if (servletContext.getAttribute(REGISTER_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(REGISTER_MANAGER_ATTRIBUTE_NAME, new RegisterManager());
			}
		}
		return (RegisterManager) servletContext.getAttribute(REGISTER_MANAGER_ATTRIBUTE_NAME);
	}
	public static GenericManager<ClientUser> getUserManager(ServletContext servletContext) {

		RegisterManager registerManager = getRegisterManager(servletContext);

		return (GenericManager<ClientUser>) registerManager.getUserManager();
	}


	public static BattlefieldManager getBattleFieldManager(ServletContext servletContext) {

		synchronized (BattlefieldManagerLock) {
			if (servletContext.getAttribute(BATTLEFIELD_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(BATTLEFIELD_MANAGER_ATTRIBUTE_NAME, new BattlefieldManager());
			}
		}
		return (BattlefieldManager) servletContext.getAttribute(BATTLEFIELD_MANAGER_ATTRIBUTE_NAME);
	}

	public static ApiEnigma getEnigmaApi(ServletContext servletContext,String username) {
		RegisterManager registerManager = getRegisterManager(servletContext);
		ApiEnigma api = registerManager.getApiFromUBoat(username);
		return api;

	}
	public static UBoat getUBoatByName(ServletContext servletContext,String username){
		RegisterManager registerManager = getRegisterManager(servletContext);
		return registerManager.getUBoatByName(username);
	}

	/*public static ChatManager getChatManager(ServletContext servletContext) {
		synchronized (chatManagerLock) {
			if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
			}
		}
		return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
	}*/

	public static int getIntParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException numberFormatException) {
			}
		}
		return INT_PARAMETER_ERROR;
	}

	public static RegisterManager.ClientType getTypeByName(String username, ServletContext servletContext) {
		RegisterManager registerManager =  getRegisterManager(servletContext);
		return registerManager.getTypeByName(username);
	}
}
