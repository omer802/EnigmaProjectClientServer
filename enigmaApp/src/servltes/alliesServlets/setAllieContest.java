package servltes.alliesServlets;

import DTOS.Configuration.UserConfigurationDTO;
import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import com.google.gson.Gson;
import engine.api.ApiEnigma;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import registerManagers.Managers.RegisterManager;
import registerManagers.clients.Allie;
import servltes.utils.ServletUtils;
import servltes.utils.SessionUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "Update allie contest", urlPatterns = "/updateAllieContest")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class setAllieContest extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            String usernameFromSession = SessionUtils.getUsername(req);
            RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
            try {
                sendRequestToRegisterManager(req, registerManager, usernameFromSession);
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (RuntimeException e) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.println(e.getMessage());
            }


        }
    }

    private void sendRequestToRegisterManager(HttpServletRequest req, RegisterManager registerManager, String usernameFromSession) throws ServletException, IOException {
        InputStream jsonChosenContestDTO = req.getParts().stream().findFirst().get().getInputStream();
        BufferedReader ChosenContestStreamReader = new BufferedReader(new InputStreamReader(jsonChosenContestDTO, StandardCharsets.UTF_8));
        Gson gson = new Gson();
        ContestInformationDTO chosenContestDTO = gson.fromJson(ChosenContestStreamReader, ContestInformationDTO.class);
        registerManager.setChosenAllieContest(chosenContestDTO,usernameFromSession);
    }
}
