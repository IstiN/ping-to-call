package com.github.istin.pingtocall.backend;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.github.istin.pingtocall.backend.OfyService.ofy;

public class RegistrationServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String email = URLDecoder.decode(req.getParameter("email"), "utf-8");
        String pin = URLDecoder.decode(req.getParameter("pin"), "utf-8");
        String regId = URLDecoder.decode(req.getParameter("regId"), "utf-8");
        resp.setContentType("text/json");

        String result;
        if (email == null || pin == null || regId == null) {
            result = "email == null || pin == null || regId == null";
            resp.getWriter().println(result);
            return;
        }
        final RegistrationDevice oldRecord = findRecord(regId);
        if(oldRecord != null && pin.equals(oldRecord.getPin()) && email.equals(oldRecord.getEmail())) {
            result = "EXISTS";
            resp.getWriter().println(result);
            return;
        }
        RegistrationDevice record = new RegistrationDevice();
        record.setRegId(regId);
        record.setEmail(email);
        record.setPin(pin);
        ofy().save().entity(record).now();
        if (oldRecord != null) {
            result = "UPDATED";
        } else {
            result = "ADDED";
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
