package servltes.UBoatServlets;

import DTOS.Configuration.FileConfigurationDTO;
import DTOS.Validators.xmlFileValidatorDTO;
import com.google.gson.Gson;
import engine.api.ApiEnigma;
import registerManagers.Managers.RegisterManager;
import registerManagers.clients.UBoat;
import registerManagers.battlefieldManager.Battlefield;
import registerManagers.battlefieldManager.BattlefieldManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servltes.utils.ServletUtils;
import servltes.utils.SessionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/upload-file")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getUsername(req);
        RegisterManager.ClientType client = ServletUtils.getTypeByName(usernameFromSession, getServletContext());

        if (!client.equals(RegisterManager.ClientType.UBOAT)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().println("Only UBoat can upload configuration");
        } else {
            InputStream xmlFile = req.getParts().stream().findFirst().get().getInputStream();
            xmlFileValidatorDTO validator = validateAndUploadFile(xmlFile,usernameFromSession);

            if (validator.getListOfExceptions().size() == 0) {
                res.setStatus(HttpServletResponse.SC_OK);
                UBoat uBoat = ServletUtils.getUBoatByName(getServletContext(),usernameFromSession);
                uBoat.makeUBoatActive();
                RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
                System.out.println(registerManager.getContestInformation());
                sendDTOConfigurationToClient(res,usernameFromSession);

            } else {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                List<Exception> exceptionList = validator.getListOfExceptions();
                for (Exception e : exceptionList) {
                    res.getWriter().println(e.getMessage());
                }
                res.getWriter().flush();
            }

        }
    }

    private void sendDTOConfigurationToClient(HttpServletResponse response, String usernameFromSession) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            ApiEnigma api = ServletUtils.getEnigmaApi(getServletContext(),usernameFromSession);
            Gson gson = new Gson();
            FileConfigurationDTO fileConfigurationDTO = api.getDataReceivedFromFile();
            String json = gson.toJson(fileConfigurationDTO);
            out.println(json);
            out.flush();
        }
    }

    // TODO: 10/10/2022 think how to make the enigma whitout sending battlefieldManager
    private xmlFileValidatorDTO validateAndUploadFile(InputStream xmlFileInputStream, String username) {
        UBoat uBoat = ServletUtils.getUBoatByName(getServletContext(),username);
        BattlefieldManager battlefieldManager = ServletUtils.getBattleFieldManager(getServletContext());
        xmlFileValidatorDTO validator = uBoat.getApi().readDataJavaFx(xmlFileInputStream,battlefieldManager);
        if (validator.getListOfExceptions().size() == 0) {
            // if there is no error update battlefield
            Battlefield battlefield = uBoat.getApi().getBattleField();
            battlefieldManager.addBattlefield(battlefield);
            uBoat.setBattlefield(battlefield);

        }
        return validator;
    }
}
