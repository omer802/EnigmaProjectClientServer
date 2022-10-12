package servltes;

import DTOS.Configuration.UserConfigurationDTO;
import com.google.gson.Gson;
import engine.api.ApiEnigma;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servltes.utils.ServletUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;


@WebServlet(name = "Set code configuration", urlPatterns = "/setCodeConfiguration")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class setCodeConfigurationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (PrintWriter out = resp.getWriter()) {
            ApiEnigma api = ServletUtils.getEnigmaApi(getServletContext());
            sendRequestToApi(req,api);
            sendResponseToClient(resp,api,out);

        }
    }
    private void sendResponseToClient(HttpServletResponse resp, ApiEnigma api, PrintWriter out){
        resp.setContentType("application/json");
        UserConfigurationDTO configurationDTO = api.getOriginalConfiguration();
        Gson gson = new Gson();
        String jsonConfiguration = gson.toJson(configurationDTO);
        out.println(jsonConfiguration);
        System.out.println(jsonConfiguration);
        out.flush();

    }
        private void sendRequestToApi(HttpServletRequest req, ApiEnigma api) throws ServletException, IOException {
            InputStream jsonConfigInputStream = req.getParts().stream().findFirst().get().getInputStream();
            BufferedReader ConfigstreamReader = new BufferedReader(new InputStreamReader(jsonConfigInputStream,StandardCharsets.UTF_8));
            Gson gson = new Gson();
            UserConfigurationDTO userConfiguration = gson.fromJson(ConfigstreamReader, UserConfigurationDTO.class);
            api.selectInitialCodeConfiguration(userConfiguration);
        }


    }

