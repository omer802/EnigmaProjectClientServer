package servltes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import registerManagers.Managers.RegisterManager;
import servltes.utils.ServletUtils;
import servltes.utils.SessionUtils;

@WebServlet(name = "Finish contest", urlPatterns = "/finishContestAndRest")

public class finishContestAndRest extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(req);
        RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
        RegisterManager.ClientType clientType = ServletUtils.getTypeByName(usernameFromSession, getServletContext());
        switch (clientType){
            case ALLIE:
                registerManager.signoutAllieFromContestAndRestDM(usernameFromSession);
                resp.setStatus(HttpServletResponse.SC_OK);
                break;
            case UBOAT:
                registerManager.restUBoatAfterContest(usernameFromSession);
                resp.setStatus(HttpServletResponse.SC_OK);

        }
       /* if(clientType.equals(RegisterManager.ClientType.ALLIE)){
            registerManager.signoutAllieFromContest(usernameFromSession);
            resp.setStatus(HttpServletResponse.SC_OK);
        }*/

    }

}

