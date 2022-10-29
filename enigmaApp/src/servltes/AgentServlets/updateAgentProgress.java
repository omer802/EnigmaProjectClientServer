package servltes.AgentServlets;

import DTOS.agentInformationDTO.AgentProgressDTO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import registerManagers.Managers.RegisterManager;
import servltes.utils.ServletUtils;
import servltes.utils.SessionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "update agent progress", urlPatterns = "/updateAgentProgress")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class updateAgentProgress extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        InputStream jsonCandidatesDTOStream = req.getParts().stream().findFirst().get().getInputStream();
        BufferedReader agentProgressDTOBuffer = new BufferedReader(new InputStreamReader(jsonCandidatesDTOStream, StandardCharsets.UTF_8));
        Gson gson = new Gson();
        AgentProgressDTO agentProgressDTO = gson.fromJson(agentProgressDTOBuffer, AgentProgressDTO.class);
        String agentUsername = SessionUtils.getUsername(req);
        RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
        //System.out.println(agentProgressDTO.getAgentName());
        //System.out.println(agentProgressDTO.getAcceptedMissions());
        //System.out.println(agentProgressDTO.getWaitingMissions());
        //System.out.println(agentProgressDTO.getCandidatesAmount());

        registerManager.updateAgentProgress(agentUsername,agentProgressDTO);
    }
}
