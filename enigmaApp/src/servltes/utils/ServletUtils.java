package servltes.utils;


import engine.api.ApiEnigma;
import engine.api.ApiEnigmaImp;
import engine.registerManagers.BattlefieldManager;
import engine.registerManagers.UserManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import static servltes.login.constants.Constants.INT_PARAMETER_ERROR;


public class ServletUtils {

	private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final String API_ENGINE_ATTRIBUTE_NAME = "apiEngine";
	private static final String BATTLEFIELD_MANAGER_ATTRIBUTE_NAME = "battleFieldManager";
	/*
	Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
	the actual fetch of them is remained un-synchronized for performance POV
	 */
	private static final Object userManagerLock = new Object();
	private static final Object enigmaApiLock = new Object();

	private static final Object BattlefieldManagerLock = new Object();
	public static UserManager getUserManager(ServletContext servletContext) {

		synchronized (userManagerLock) {
			if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
			}
		}
		return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
	}

	public static BattlefieldManager getBattleFieldManager(ServletContext servletContext) {

		synchronized (BattlefieldManagerLock) {
			if (servletContext.getAttribute(BATTLEFIELD_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(BATTLEFIELD_MANAGER_ATTRIBUTE_NAME, new BattlefieldManager());
			}
		}
		return (BattlefieldManager) servletContext.getAttribute(BATTLEFIELD_MANAGER_ATTRIBUTE_NAME);
	}

	public static ApiEnigma getEnigmaApi(ServletContext servletContext) {

		synchronized (enigmaApiLock) {
			if (servletContext.getAttribute(API_ENGINE_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(API_ENGINE_ATTRIBUTE_NAME, new ApiEnigmaImp());
			}
		}
		return (ApiEnigma) servletContext.getAttribute(API_ENGINE_ATTRIBUTE_NAME);
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
}
