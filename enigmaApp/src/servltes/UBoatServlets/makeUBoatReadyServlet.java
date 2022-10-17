package servltes.UBoatServlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import registerManagers.Managers.RegisterManager;
import servltes.utils.ServletUtils;
import servltes.utils.SessionUtils;

import java.io.IOException;

@WebServlet(name = "Make UBoat ready", urlPatterns = "/makeUBoatReady")
public class makeUBoatReadyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getUsername(req);
        RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
        registerManager.makeClientReady(usernameFromSession);
        System.out.println("just falling here");
    }





}
