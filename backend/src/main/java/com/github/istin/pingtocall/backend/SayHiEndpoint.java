package com.github.istin.pingtocall.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(
        name = "myApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "com.github.istin.pingtocall.backend",
                ownerName = "com.github.istin.pingtocall.backend",
                packagePath="com.github.istin.pingtocall.backend"
        )
)
public class SayHiEndpoint {

    public class HiBean {
        String data;
    }
    /** A simple endpoint method that takes a name and says Hi back */
    @ApiMethod(name = "sayHi")
    public HiBean sayHi(@Named("name") String name) {
        HiBean response = new HiBean();
        response.data = "Hi, " + name;
        return response;
    }


}
