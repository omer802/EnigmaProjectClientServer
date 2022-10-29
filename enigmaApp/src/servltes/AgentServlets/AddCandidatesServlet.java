package servltes.AgentServlets;

import DTOS.agentInformationDTO.CandidateDTO;
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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "update candidate", urlPatterns = "/updateCandidate")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class AddCandidatesServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        InputStream jsonCandidatesDTOStream = req.getParts().stream().findFirst().get().getInputStream();
        BufferedReader candidatesDTOBuffer = new BufferedReader(new InputStreamReader(jsonCandidatesDTOStream, StandardCharsets.UTF_8));
        Gson gson = new Gson();
        CandidateDTO[] candidatesDTO = gson.fromJson(candidatesDTOBuffer, CandidateDTO[].class);
        List<CandidateDTO> candidateList = Arrays.asList(candidatesDTO);
        String agentUser = SessionUtils.getUsername(req);
        RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
        registerManager.addCandidateFromAgent(agentUser,candidateList);

    }
}
