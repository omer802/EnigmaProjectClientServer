package servltes.UBoatServlets;

import dictionary.Dictionary;
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

@WebServlet(name = "Fetch dictionary", urlPatterns = "/fetchDictionary")
public class fetchDictionaryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            String usernameFromSession = SessionUtils.getUsername(req);
            ApiEnigma api = ServletUtils.getEnigmaApi(getServletContext(),usernameFromSession);
            Dictionary dictionary = api.getDictionary();
            Gson gson = new Gson();
            String jsonDictionary = gson.toJson(dictionary);
            out.println(jsonDictionary);
            out.flush();
        }

    }
}
