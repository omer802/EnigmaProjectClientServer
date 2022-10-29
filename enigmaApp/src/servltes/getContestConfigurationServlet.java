package servltes;

import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import registerManagers.Managers.RegisterManager;
import servltes.utils.ServletUtils;
import servltes.utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "get status Servlet", urlPatterns = "/getContestConfiguration")
public class getContestConfigurationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Gson gson = new Gson();
            RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
            String username = SessionUtils.getUsername(req);
            RegisterManager.ClientType clientType =  ServletUtils.getTypeByName(username,getServletContext());
            ContestInformationDTO contestInformationDTO = null;
            switch (clientType){
                case ALLIE:
                    //
                    break;
                case AGENT:
                    try{
                    contestInformationDTO = registerManager.getContestFromAllieByAgentName(username);
                    } catch (Exception e) {
                        out.println("something went wrong "+ e.getMessage());
                        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }

            }
            String json = gson.toJson(contestInformationDTO);
            out.println(json);
            out.flush();
        }
    }
}
