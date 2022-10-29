package servltes;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import registerManagers.Managers.RegisterManager;
import registerManagers.clients.Allie;
import registerManagers.clients.UBoat;
import servltes.utils.ServletUtils;
import servltes.utils.SessionUtils;
import util.CommonConstants;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "get contest Status", urlPatterns = "/getContestStatus")
public class ContestStatusServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            String usernameFromSession = SessionUtils.getUsername(req);
            RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
            RegisterManager.ClientType clientType = ServletUtils.getTypeByName(usernameFromSession, getServletContext());
            switch (clientType) {
                case AGENT:
                    Allie.AllieStatus allieStatusFromAgent = registerManager.getContestStatusByAgent(usernameFromSession);
                    //System.out.println(allieStatus.name());
                    out.print(allieStatusFromAgent.name());
                    resp.setStatus(HttpServletResponse.SC_OK);
                    break;
                case ALLIE:
                    Allie.AllieStatus allieStatus = registerManager.getContestStatusByAllie(usernameFromSession);
                    //System.out.println(allieStatus.name());
                    out.print(allieStatus.name());
                    resp.setStatus(HttpServletResponse.SC_OK);
                    break;
                case UBOAT:
                   UBoat.GameStatus gameStatus = registerManager.getContestStatusByUBoat(usernameFromSession);
                   out.print(gameStatus.name());
                   resp.setStatus(HttpServletResponse.SC_OK);
                    break;
            }

           /* resp.setStatus(HttpServletResponse.SC_OK);

            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println(e.getMessage());*/

        }
    }
}

