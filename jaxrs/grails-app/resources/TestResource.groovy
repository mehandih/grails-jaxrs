import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces

import grails.converters.JSON
import grails.converters.XML
@Path('/api/test')
class TestResource {

    @GET 