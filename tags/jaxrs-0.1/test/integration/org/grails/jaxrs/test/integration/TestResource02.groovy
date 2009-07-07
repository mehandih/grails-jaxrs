/*
 * Copyright 2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grails.jaxrs.test.integration

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.POSTimport javax.ws.rs.Consumes
import org.springframework.stereotype.Component
/**
 * @author Martin Krasser
 */
@Component
@Path('/test/02')
public class TestResource02 {

    @POST
    @Consumes('text/plain')
    @Produces('text/plain')
    CustomResponseEntity test(CustomRequestEntity requestEntity) {
        new CustomResponseEntity(content:'response:' + requestEntity.content)
    }
    
}
