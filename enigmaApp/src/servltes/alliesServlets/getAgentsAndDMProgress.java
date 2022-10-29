package servltes.alliesServlets;

import DTOS.AllieInformationDTO.AgentAndDMProgressDTO;
import DTOS.agentInformationDTO.AgentProgressDTO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import registerManagers.Managers.RegisterManager;
import servltes.utils.ServletUtils;
import servltes.utils.SessionUtils;

import java.io.*;
import java.util.List;

@WebServlet(name = "get agents progress", urlPatterns = "/getAgentsAndDMProgress")
public class getAgentsAndDMProgress extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (PrintWriter out = resp.getWriter()) {
            String allieUsername = SessionUtils.getUsername(req);
            RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
            AgentAndDMProgressDTO agentsAndDMProgressFromAllie = registerManager.getAgentsAndDMProgressFromAllie(allieUsername);
            Gson gson = new Gson();
            String agentProgressDTOListJson = gson.toJson(agentsAndDMProgressFromAllie);
            out.println(agentProgressDTOListJson);
        }
    }
}
