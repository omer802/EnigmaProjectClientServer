package servltes.AgentServlets;

import DTOS.enigmaComponentContainers.ConfigurationForAgentBruteForceDTO;
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

@WebServlet(name = "fetch", urlPatterns = "/fetchDictionaryAndMachine")
public class FetchMachineAndDictionaryFromAllie extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Gson gson = new Gson();
            RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
            String agentUserName = SessionUtils.getUsername(req);
            ConfigurationForAgentBruteForceDTO machineAndDictionaryContainerDTO;
            try {
                machineAndDictionaryContainerDTO = registerManager.fetchDictionaryAndMachineByAgentName(agentUserName);
            } catch (Exception e) {
                out.println(e.getMessage());
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            String json = gson.toJson(machineAndDictionaryContainerDTO);
            resp.setStatus(HttpServletResponse.SC_OK);
            out.println(json);
            out.flush();
        }
    }

}
