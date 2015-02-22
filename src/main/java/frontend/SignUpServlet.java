package frontend;

import main.AccountService;
import main.UserProfile;
import templater.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by v.chibrikov on 13.09.2014.
 */
public class SignUpServlet extends HttpServlet {
    private AccountService accountService;

    public SignUpServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserProfile profile = accountService.getSessions(session.getId());

        String pageTml;

        Map<String, Object> pageVariables = new HashMap<>();
        if (profile == null) {
            pageTml = "signUpForm.html";
        } else {
            pageTml = "signupstatus.html";
            pageVariables.put("signUpStatus", "You have to logout before signing up.");
        }
        response.getWriter().println(PageGenerator.getPage(pageTml, pageVariables));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public void doPost(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String server = request.getParameter("server");
        String email = request.getParameter("email");

        UserProfile user = new UserProfile(login, password, email, server);
        HttpSession session = request.getSession();

        String pageToReturn = "signupstatus.html";

        Map<String, Object> pageVariables = new HashMap<>();

        if (accountService.addUser(login, user)) {
            accountService.addSessions(session.getId(), user);
            pageVariables.put("signUpStatus", "New user created");
        } else {
            pageVariables.put("signUpStatus", "User with login: " +
                    "" + login + " already exists. Try again.");
            pageToReturn = "signUpForm.html";
        }
        response.getWriter().println(PageGenerator.getPage(pageToReturn, pageVariables));
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
