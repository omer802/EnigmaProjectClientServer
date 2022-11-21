package servltes.AgentServlets;

import DTOS.enigmaComponentContainers.AgentTaskConfigurationDTO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import registerManagers.Managers.RegisterManager;
import registerManagers.clients.UBoat;
import servltes.constants.Constants;
import servltes.utils.ServletUtils;
import servltes.utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

@WebServlet(name = "get Mission Servlet", urlPatterns = "/fetchMissionsRequest")
public class getMissionsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            String missionAmountString = req.getParameter(Constants.MISSION_AMOUNT_PARAMETER);
            int missionAmount;
            try {
                missionAmount = Integer.parseInt(missionAmountString);
                if (missionAmount < 0) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                return;
            }
            Gson gson = new Gson();
            RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
            String agentUserName = SessionUtils.getUsername(req);
            BlockingQueue<AgentTaskConfigurationDTO> blockingQueue = registerManager.getBlockingQueueByAgentName(agentUserName);
            List<AgentTaskConfigurationDTO> missionTaskList = new ArrayList<>();
            AtomicLong atomicMissionAmount = registerManager.getTotalTaskAmount(agentUserName);
            //4 new line of if and else
              UBoat uBoat = registerManager.getUBoatByAgentName(agentUserName);
              if (!uBoat.isActiveContest()) {
                      registerManager.finishContestInAllieByAgentName(agentUserName);
                      resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } else {
                synchronized (registerManager.getBlockingQueueByAgentName(agentUserName)) {
                    for (int i = 0; i < missionAmount; i++) {
                        AgentTaskConfigurationDTO agentTaskConfiguration = blockingQueue.poll();
                        if (agentTaskConfiguration != null) {
                            missionTaskList.add(agentTaskConfiguration);
                            long missionAmountLeft = atomicMissionAmount.decrementAndGet();

                            }
                        }

                    }
                }

                String jsonTasks = gson.toJson(missionTaskList);
                resp.setStatus(HttpServletResponse.SC_OK);
                out.println(jsonTasks);
                out.flush();
           // }
        }

    }
}