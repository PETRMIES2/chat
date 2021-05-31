package utils.restmock;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.Filter;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import utils.database.RequestParameter;

@Component
public class HttpRequestMock {

    private static final String HTTP_HEADER_X_AUTH_TOKEN = "X-Auth-Token";

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    private Filter springSecurityFilterChain;

    @Inject
    private WebApplicationContext webApplicationContext;

    @Inject
    private RequestMappingHandlerMapping handlerMapping;

    @PostConstruct
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        System.out.println("********************** API ***************************");

        for (Entry<RequestMappingInfo, HandlerMethod> p : handlerMapping.getHandlerMethods().entrySet()) {
            System.out.println(p.getKey() + " " + p.getValue());

        }

        System.out.println("**********************     ***************************");

    }

    public <H> H post(String url, Object messageBody, ResultMatcher resultMatcher, String authToken) throws Exception {
        MockHttpServletRequestBuilder httpPostBuilder = builder(MockMvcRequestBuilders.post(url), messageBody, authToken);
        MvcResult response = handleRequest(httpPostBuilder, contentType, resultMatcher);
        return getResponsePojo(response, messageBody.getClass());
    }

    public <H> H post(String url, Object messageBody, Class<?> returnType, ResultMatcher resultMatcher, String authToken) throws Exception {
        MockHttpServletRequestBuilder httpPostBuilder = builder(MockMvcRequestBuilders.post(url), messageBody, authToken);
        MvcResult response = handleRequest(httpPostBuilder, contentType, resultMatcher);
        return getResponsePojo(response, returnType);
    }

    public void failingPost(String url, Object messageBody, ResultMatcher resultMatcher, String authToken) throws Exception {
        MockHttpServletRequestBuilder httpPostBuilder = builder(MockMvcRequestBuilders.post(url), messageBody, authToken);
        mockMvc.perform(httpPostBuilder).andExpect(resultMatcher);
    }

    public void postWithoutReturnObject(String url, Object messageBody, String authToken) throws Exception {
        successfulRequestNoContent(builder(MockMvcRequestBuilders.post(url), messageBody, authToken));
    }

    public void postWithoutReturnObjectAndStatus(String url, Object messageBody, String authToken, ResultMatcher resultMatcher) throws Exception {
        mockMvc.perform(builder(MockMvcRequestBuilders.post(url), messageBody, authToken)).andExpect(resultMatcher).andReturn();
    }

    public void postWithOutContent(String url, List<RequestParameter> requestParameters, String authToken) throws Exception {
        MockHttpServletRequestBuilder httpPostBuilder = builder(MockMvcRequestBuilders.post(url), authToken);
        addParametersToRequest(requestParameters, httpPostBuilder);
        mockMvc.perform(httpPostBuilder).andExpect(status().is2xxSuccessful());
    }

    public void put(String url, List<RequestParameter> requestParameters, String authToken) throws Exception {
        MockHttpServletRequestBuilder putBuilder = builder(MockMvcRequestBuilders.put(url), authToken);
        addParametersToRequest(requestParameters, putBuilder);
        mockMvc.perform(putBuilder).andExpect(status().is2xxSuccessful());
    }

    public Object put(String url, Object messageBody, Class<?> returnType, ResultMatcher resultMatcher, String authToken) throws Exception {

        MockHttpServletRequestBuilder getBuilder = builder(MockMvcRequestBuilders.put(url), messageBody, authToken);
        MvcResult response = handleRequest(getBuilder, contentType, resultMatcher);
        return getResponsePojo(response, returnType);
    }

    public <H> H get(String url, Object messageBody, Class<?> returnType, ResultMatcher resultMatcher, String authToken) throws Exception {
        MockHttpServletRequestBuilder getBuilder = builder(MockMvcRequestBuilders.get(url), messageBody, authToken);
        MvcResult response = handleRequest(getBuilder, contentType, resultMatcher);
        return getResponsePojo(response, returnType);
    }

    public <H> H get(String url, Class<?> responsePojo, String authToken) throws Exception {
        MockHttpServletRequestBuilder getBuilder = builder(MockMvcRequestBuilders.get(url), authToken);

        MvcResult response = handleRequest(getBuilder, contentType, status().is2xxSuccessful());
        return getResponsePojo(response, responsePojo);
    }

    private MvcResult handleRequest(MockHttpServletRequestBuilder requestBuilder, MediaType mediaType, ResultMatcher resultMatcher) throws Exception {
        return mockMvc.perform(requestBuilder).andExpect(resultMatcher).andExpect(content().contentType(mediaType)).andReturn();
    }

    private MvcResult successfulRequestNoContent(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        return mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful()).andReturn();
    }

    private MockHttpServletRequestBuilder builder(MockHttpServletRequestBuilder httpRestType, Object MessageBody, String authToken) throws JsonProcessingException {
        return httpRestType.header(HTTP_HEADER_X_AUTH_TOKEN, authToken).contentType(contentType).content(objectMapper.writeValueAsString(MessageBody));
    }

    private MockHttpServletRequestBuilder builder(MockHttpServletRequestBuilder builder, String authToken) {
        return builder.header(HTTP_HEADER_X_AUTH_TOKEN, authToken).contentType(contentType);
    }

    @SuppressWarnings("unchecked")
    private <H> H getResponsePojo(MvcResult mvcResult, Class<?> clazz) throws IOException {
        MockHttpServletResponse response = mvcResult.getResponse();
        return (H) objectMapper.readValue(response.getContentAsString(), clazz);
    }

    private void addParametersToRequest(List<RequestParameter> requestParameters, MockHttpServletRequestBuilder httpPostBuilder) {
        for (RequestParameter requestParameter : requestParameters) {
            httpPostBuilder.param(requestParameter.name, requestParameter.value);
        }
    }
}
