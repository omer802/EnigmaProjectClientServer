package servltes.login;


import registerManagers.Managers.RegisterManager;
import registerManagers.Managers.GenericManager;
import registerManagers.Managers.ClientUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servltes.utils.ServletUtils;
import servltes.utils.SessionUtils;

import java.io.IOException;

import static servltes.constants.Constants.CLIENT_TYPE;
import static servltes.constants.Constants.USERNAME;

@WebServlet(name = "login request", urlPatterns = "/loginRequest" )
public class LoginServlet extends HttpServlet {

    // urls that starts with forward slash '/' are considered absolute
    // urls that doesn't start with forward slash '/' are considered relative to the place where this servlet request comes from
    // you can use absolute paths, but then you need to build them from scratch, starting from the context path
    // ( can be fetched from request.getContextPath() ) and then the 'absolute' path from it.
    // Each method with it's pros and cons...
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);
        RegisterManager registerManager = ServletUtils.getRegisterManager(getServletContext());
        GenericManager<ClientUser> userManager = registerManager.getUserManager();
        if (usernameFromSession == null) {
            //user is not logged in yet
            String usernameFromParameter = request.getParameter(USERNAME);
            String userType = request.getParameter(CLIENT_TYPE);
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("You cannot login without username");
            }
            if(userType == null || ((!userType.equals("UBoat"))&&
                    (!userType.equals("Allie"))&& (!userType.equals("Agent")))) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("You cannot login without right client type");

            }
             else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();
                synchronized (this) {
                    RegisterManager.ClientType client = RegisterManager.ClientType.valueOf(userType.toUpperCase());
                    ClientUser clientUser = new ClientUser(client,usernameFromParameter);
                    if (userManager.isClientExists(clientUser)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";

                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().println("The username already exists in the system");
                    }
                    else {
                        //add the new user to the users list

                        registerManager.addUserByType(clientUser);
                        //set the username in a session so it will be available on each request
                        //the true parameter means that if a session object does not exists yet
                        //create a new one
                        request.getSession(true).setAttribute(USERNAME, usernameFromParameter);
                    }
                }
            }
        } else {
            //user is already logged in
            response.getWriter().println(" its only beacuse you alerady entered here");
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
        }
    }




// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
