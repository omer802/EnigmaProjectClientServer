package servltes;

import DTOS.Configuration.FileConfigurationDTO;
import DTOS.Validators.xmlFileValidatorDTO;
import com.google.gson.Gson;
import engine.api.ApiEnigma;
import engine.battleField.Battlefield;
import engine.registerManagers.BattlefieldManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servltes.utils.ServletUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/upload-file")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        System.out.println("entering post methopd");
        InputStream xmlFile = req.getParts().stream().findFirst().get().getInputStream();
        xmlFileValidatorDTO validator = validateAndUploadFile(xmlFile);
        if(validator.getListOfExceptions().size() == 0){
            res.setStatus(HttpServletResponse.SC_OK);
            sendDTOConfigurationToClient(res);
        }
        else{
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            List<Exception> exceptionList = validator.getListOfExceptions();
            for (Exception e: exceptionList) {
                res.getWriter().println(e.getMessage());
            }
            res.getWriter().flush();
        }

    }

    private void sendDTOConfigurationToClient(HttpServletResponse response) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            System.out.println(" send to client ");
            ApiEnigma api = ServletUtils.getEnigmaApi(getServletContext());
            Gson gson = new Gson();
            FileConfigurationDTO fileConfigurationDTO = api.getDataReceivedFromFile();
            String json = gson.toJson(fileConfigurationDTO);
            out.println(json);
            out.flush();
        }
    }

    // TODO: 10/10/2022 think how to make the enigma whitout sending battlefieldManager
    private xmlFileValidatorDTO validateAndUploadFile(InputStream xmlFileInputStream) {
        ApiEnigma api = ServletUtils.getEnigmaApi(getServletContext());
        BattlefieldManager battlefieldManager = ServletUtils.getBattleFieldManager(getServletContext());
        xmlFileValidatorDTO validator = api.readDataJavaFx(xmlFileInputStream,battlefieldManager);
        if (validator.getListOfExceptions().size() == 0) {
            Battlefield battlefield = api.getBattleField();
            battlefieldManager.addBattlefield(battlefield);
        }
        return validator;
    }
}
