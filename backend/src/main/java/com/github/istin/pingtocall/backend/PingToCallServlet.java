package com.github.istin.pingtocall.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.github.istin.pingtocall.backend.OfyService.ofy;

public class PingToCallServlet extends HttpServlet {

    /** Api Keys can be obtained from the google cloud console */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String email = URLDecoder.decode(req.getParameter("email"), "utf-8");
        String pin = URLDecoder.decode(req.getParameter("pin"), "utf-8");
        resp.setContentType("text/json");
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", email+"|"+pin).build();
        RegistrationDevice record = ofy().load().type(RegistrationDevice.class)
                .filter("email", email)
                .filter("pin", pin)
                .first().now();
        String resultMessage = record == null ? "INVALID" : null;
        if (record != null) {
            Result result = sender.send(msg, record.getRegId(), 5);
            if (result.getMessageId() != null) {
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    record.setRegId(canonicalRegId);
                    ofy().save().entity(record).now();
                }
                resultMessage = "MESSAGE_SENT";
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    // if the device is no longer registered with Gcm, remove it from the datastore
                    ofy().delete().entity(record).now();
                    resultMessage = "REGISTRATION_ID_REMOVED";
                } else {
                    resultMessage = "Error when sending message : " + error;
                }
            }
        }
        resp.getWriter().println(resultMessage);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        throw new UnsupportedOperationException();
    }

}
