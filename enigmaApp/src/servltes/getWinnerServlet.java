package servltes;

import DTOS.agentInformationDTO.CandidateDTO;
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

@WebServlet(name = "Get Winner Servlet", urlPatterns = "/getWinnerServlet")
public class getWinnerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Gson gson = new Gson();
            RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
            String username = SessionUtils.getUsername(req);
            RegisterManager.ClientType clientType = ServletUtils.getTypeByName(username, getServletContext());
            CandidateDTO candidateDTO = registerManager.getWinnerByClientType(username,clientType);
            String json = gson.toJson(candidateDTO);
            out.print(json);
            out.flush();
        }
    }
}

