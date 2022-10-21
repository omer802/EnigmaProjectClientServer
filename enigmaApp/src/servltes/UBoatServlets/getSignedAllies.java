package servltes.UBoatServlets;

import DTOS.AllieInformationDTO.AlliesDetailDTO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import registerManagers.Managers.RegisterManager;
import registerManagers.clients.Client;
import servltes.utils.ServletUtils;
import servltes.utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "get signed allies", urlPatterns = "/getSignedAllies")

public class getSignedAllies extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Gson gson = new Gson();
            String usernameFromSession = SessionUtils.getUsername(req);
            RegisterManager.ClientType clientType = ServletUtils.getTypeByName(usernameFromSession,getServletContext());
            RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
            List<AlliesDetailDTO> SignedAlliesDTOList;
            if(clientType.equals(RegisterManager.ClientType.UBOAT)) {
                SignedAlliesDTOList = registerManager.getSignedAllies(usernameFromSession);
            }
            else if(clientType.equals(RegisterManager.ClientType.ALLIE)){
                SignedAlliesDTOList = registerManager.getParticipantAlliesInContest(usernameFromSession);
            }
            else{
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("Unknown client");
                return;
            }
            String json = gson.toJson(SignedAlliesDTOList);
            out.println(json);
            out.flush();
        }
    }
}



