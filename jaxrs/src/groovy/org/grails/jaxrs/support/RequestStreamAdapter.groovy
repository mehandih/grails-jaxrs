package org.grails.jaxrs.support

import java.io.InputStream;
import javax.servlet.ServletInputStream

import org.springframework.mock.web.MockHttpServletRequest;

class RequestStreamAdapter extends MockHttpServletRequest {

    InputStream stream
    
    String format
    
    RequestStreamAdapter(InputStream stream) {
        this.stream = stream
    }
    
    @Override
    ServletInputStream getInputStream() {
        if (stream) {
            return stream
        }
        return super.getInputStream()
    }
    
}
