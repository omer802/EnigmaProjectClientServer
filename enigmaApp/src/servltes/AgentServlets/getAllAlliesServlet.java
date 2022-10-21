package servltes.AgentServlets;

import DTOS.AllieInformationDTO.AlliesDetailDTO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import registerManagers.Managers.RegisterManager;
import servltes.utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
@WebServlet(name = "get all allies request", urlPatterns = "/getAllAllies")
public class getAllAlliesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Gson gson = new Gson();
            RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
            List<AlliesDetailDTO> AllAllies = registerManager.getAllAllies();
            String json = gson.toJson(AllAllies);
            out.println(json);
            out.flush();
        }
    }

}
