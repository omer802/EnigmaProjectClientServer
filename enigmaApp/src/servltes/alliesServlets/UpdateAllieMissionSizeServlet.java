package servltes.alliesServlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import registerManagers.Managers.RegisterManager;
import servltes.utils.ServletUtils;
import servltes.utils.SessionUtils;

import java.io.IOException;

import static servltes.constants.Constants.MISSION_SIZE_PARAMETER;

@WebServlet(name = "Update mission size", urlPatterns = "/updateAllieMissionSize")
public class UpdateAllieMissionSizeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(req);
        RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
        if(!(ServletUtils.getTypeByName(usernameFromSession, getServletContext()).equals(RegisterManager.ClientType.ALLIE))){
            resp.getWriter().println("client have to be allie");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
       try {
           int missionSize = Integer.parseInt(req.getParameter(MISSION_SIZE_PARAMETER));
           registerManager.updateMissionSizeByAllieName(usernameFromSession,missionSize);
           resp.setStatus(HttpServletResponse.SC_OK);

       } catch (NumberFormatException e) {
           resp.getWriter().println("mission size parameter need to be positive number");
           resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
       }
    }
}
