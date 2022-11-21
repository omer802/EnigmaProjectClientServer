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
import java.util.List;

@WebServlet(name = "get candidate", urlPatterns = "/getCandidate")
public class getCandidatesFromAgent extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Gson gson = new Gson();
            RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
            String username = SessionUtils.getUsername(req);
            RegisterManager.ClientType clientType = ServletUtils.getTypeByName(username, getServletContext());
            if (clientType.equals(RegisterManager.ClientType.ALLIE)) {
                List<CandidateDTO> candidates = registerManager.getAllieCandidates(username);
                String json = gson.toJson(candidates);
                out.println(json);


            } else if(clientType.equals(RegisterManager.ClientType.UBOAT)) {
                List<CandidateDTO> candidates = registerManager.getUBoatCandidates(username);
                String json = gson.toJson(candidates);
                out.println(json);
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                out.println("cannot acess candidates with client that is not allie or uboat");
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        }
    }
}


