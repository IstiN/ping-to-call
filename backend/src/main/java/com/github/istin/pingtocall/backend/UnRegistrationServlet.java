package com.github.istin.pingtocall.backend;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.github.istin.pingtocall.backend.OfyService.ofy;

public class UnRegistrationServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String regId = URLDecoder.decode(req.getParameter("regId"), "utf-8");
        resp.setContentType("text/json");

        String result;
        RegistrationDevice record = findRecord(regId);
        if(record != null) {
            ofy().delete().entity(record).now();
            result = "REMOVED";
        } else {
            result = "DOES_NOT_EXIST";
        }
        resp.getWriter().println(result);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        throw new UnsupportedOperationException();
    }

    private RegistrationDevice findRecord(String regId) {
        return ofy().load().type(RegistrationDevice.class)
                .filter("regId", regId)
                .first().now();
    }

}
