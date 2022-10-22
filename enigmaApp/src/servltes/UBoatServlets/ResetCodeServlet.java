package servltes.UBoatServlets;

import DTOS.Configuration.UserConfigurationDTO;
import com.google.gson.Gson;
import engine.api.ApiEnigma;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servltes.utils.ServletUtils;
import servltes.utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Reset code", urlPatterns = "/resetCode")
public class ResetCodeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (PrintWriter out = resp.getWriter()) {
            String usernameFromSession = SessionUtils.getUsername(req);
            ApiEnigma api = ServletUtils.getEnigmaApi(getServletContext(),"usernameFromSession");
            api.resetPositions();
            UserConfigurationDTO userConfiguration = api.getOriginalConfiguration();
            Gson gson = new Gson();
            String jsonConfiguration = gson.toJson(userConfiguration);
            out.println(jsonConfiguration);
            out.flush();

        }
    }
}
