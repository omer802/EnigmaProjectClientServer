package servltes.alliesServlets;

import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import DTOS.agentInformationDTO.AgentInfoDTO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import registerManagers.Managers.RegisterManager;
import servltes.utils.ServletUtils;
import servltes.utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "Get all allies", urlPatterns = "/getAllAgentsSignedToAllie")
public class getAllAgentsRegisteredToAllieServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Gson gson = new Gson();
            RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
            String username = SessionUtils.getUsername(req);
            RegisterManager.ClientType clientType = ServletUtils.getTypeByName(username,getServletContext());
            if(clientType.equals(RegisterManager.ClientType.ALLIE)) {

                    List<AgentInfoDTO> SignedAgentInfoDTOList = registerManager.getSingedAgentToAllieByAllieName(username);
                    String json = gson.toJson(SignedAgentInfoDTOList);
                    out.println(json);



            }
            else{
                out.println("client not supported. only Allie client can access this resource");
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.flush();
            }
        }
    }
    }

