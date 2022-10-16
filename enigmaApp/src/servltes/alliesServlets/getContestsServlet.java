package servltes.alliesServlets;

import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import registerManagers.RegisterManager;
import registerManagers.genericManager.GenericManager;
import servltes.utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

@WebServlet(name = "contest data request", urlPatterns = "/contestsDataRequest")
public class getContestsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Gson gson = new Gson();
            RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
            List<ContestInformationDTO> ContestsList = registerManager.getContestInformation();
            String json = gson.toJson(ContestsList);
            out.println(json);
            out.flush();
    }
}

}
