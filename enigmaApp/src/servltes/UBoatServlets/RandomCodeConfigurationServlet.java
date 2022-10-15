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

import java.io.*;

@WebServlet(name = "random code configuration", urlPatterns = "/randomCode")
public class RandomCodeConfigurationServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            try (PrintWriter out = resp.getWriter()) {
                String usernameFromSession = SessionUtils.getUsername(req);
                ApiEnigma api = ServletUtils.getEnigmaApi(getServletContext(),usernameFromSession);
                sendRequestToApi(api);
                sendResponseToClient(resp,api,out);

            }
        }
        private void sendResponseToClient(HttpServletResponse resp, ApiEnigma api, PrintWriter out){
            resp.setContentType("application/json");
            UserConfigurationDTO configurationDTO = api.getOriginalConfiguration();
            Gson gson = new Gson();
            String jsonConfiguration = gson.toJson(configurationDTO);
            out.println(jsonConfiguration);
            out.flush();

        }
        private void sendRequestToApi(ApiEnigma api) throws ServletException, IOException {
            api.automaticallyInitialCodeConfiguration();
        }


    }


