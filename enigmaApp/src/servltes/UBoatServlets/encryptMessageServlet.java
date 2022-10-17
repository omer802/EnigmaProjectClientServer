package servltes.UBoatServlets;

import DTOS.Configuration.UserConfigurationDTO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import registerManagers.clients.UBoat;
import servltes.utils.ServletUtils;
import servltes.utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

@WebServlet(name = "Encrypt message", urlPatterns = "/encryptMessage")
public class encryptMessageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Properties prop = new Properties();
            prop.load(req.getInputStream());
            String toEncrypt = prop.getProperty("messageToEncrypt");
            toEncrypt = toEncrypt.replaceAll("%20"," ");
            String usernameFromSession = SessionUtils.getUsername(req);
            UBoat uBoat = ServletUtils.getUBoatByName(getServletContext(),usernameFromSession);
            UserConfigurationDTO userConfigurationDTO = uBoat.encryptString(toEncrypt);
            Gson gson = new Gson();
            String jsonUserConfigurationDTO = gson.toJson(userConfigurationDTO);
            out.println(jsonUserConfigurationDTO);
            out.flush();
        }

    }
}

