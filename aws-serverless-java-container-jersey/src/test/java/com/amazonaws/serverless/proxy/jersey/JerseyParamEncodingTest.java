package com.amazonaws.serverless.proxy.jersey;


import com.amazonaws.serverless.proxy.internal.LambdaContainerHandler;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.jersey.model.MapResponseModel;
import com.amazonaws.serverless.proxy.jersey.model.SingleValueModel;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequest;
import com.amazonaws.services.lambda.runtime.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;


public class JerseyParamEncodingTest {

    private static final String SIMPLE_ENCODED_PARAM = "p/z+3";
    private static final String JSON_ENCODED_PARAM = "{\"name\":\"faisal\"}";
    private static final String QUERY_STRING_KEY = "identifier";
    private static final String QUERY_STRING_NON_ENCODED_VALUE = "Space Test";
    private static final String QUERY_STRING_ENCODED_VALUE = "Space%20Test";
    private static final byte[] FILE_CONTENTS = new byte[]{
    (byte)47, (byte)85, (byte)135, (byte)12, (byte)53, (byte)7, (byte)158, (byte)212, (byte)55, (byte)193, (byte)149, (byte)3, (byte)166, (byte)181,
    (byte)151, (byte)84, (byte)122, (byte)200, (byte)244, (byte)5, (byte)115, (byte)159, (byte)66, (byte)64, (byte)143, (byte)211, (byte)13, (byte)63,
    (byte)235, (byte)184, (byte)51, (byte)49, (byte)143, (byte)167, (byte)231, (byte)31, (byte)78, (byte)234, (byte)145, (byte)105, (byte)190, (byte)170,
    (byte)49, (byte)135, (byte)175, (byte)106, (byte)25, (byte)86, (byte)145, (byte)181, (byte)156, (byte)23, (byte)153, (byte)99, (byte)175, (byte)63,
    (byte)43, (byte)208, (byte)5, (byte)16, (byte)140, (byte)103, (byte)146, (byte)254, (byte)155, (byte)97, (byte)53, (byte)100, (byte)137, (byte)6,
    (byte)62, (byte)101, (byte)189, (byte)137, (byte)140, (byte)5, (byte)110, (byte)218, (byte)113, (byte)132, (byte)36, (byte)188, (byte)19, (byte)168,
    (byte)93, (byte)169, (byte)124, (byte)253, (byte)201, (byte)233, (byte)21, (byte)80, (byte)4, (byte)56, (byte)0, (byte)204, (byte)205, (byte)232,
    (byte)213, (byte)253, (byte)232, (byte)91, (byte)153, (byte)169, (byte)82, (byte)247, (byte)78, (byte)71, (byte)188, (byte)71, (byte)23, (byte)171,
    (byte)232, (byte)26, (byte)146, (byte)189, (byte)145, (byte)82, (byte)79, (byte)148, (byte)1, (byte)201, (byte)243, (byte)73, (byte)98, (byte)65,
    (byte)236, (byte)177, (byte)211, (byte)106, (byte)105, (byte)46, (byte)204, (byte)214, (byte)55, (byte)182, (byte)55, (byte)149, (byte)221, (byte)52,
    (byte)186, (byte)122, (byte)255, (byte)195, (byte)60, (byte)146, (byte)21, (byte)212, (byte)139, (byte)38, (byte)146, (byte)166, (byte)14, (byte)174,
    (byte)242, (byte)145, (byte)16, (byte)44, (byte)68, (byte)89, (byte)25, (byte)219, (byte)62, (byte)227, (byte)6, (byte)89, (byte)194, (byte)146, (byte)93,
    (byte)167, (byte)230, (byte)90, (byte)59, (byte)35, (byte)136, (byte)37, (byte)196, (byte)118, (byte)16, (byte)28, (byte)107, (byte)105, (byte)87,
    (byte)195, (byte)86, (byte)87, (byte)180, (byte)176, (byte)118, (byte)6, (byte)29, (byte)26, (byte)51, (byte)94, (byte)21, (byte)23, (byte)32, (byte)156,
    (byte)150, (byte)204, (byte)53, (byte)110, (byte)134, (byte)153, (byte)138, (byte)247, (byte)98, (byte)135, (byte)249, (byte)119, (byte)121, (byte)2,
    (byte)42, (byte)62, (byte)198, (byte)197, (byte)112, (byte)153, (byte)244, (byte)174, (byte)145, (byte)54, (byte)246, (byte)44, (byte)198, (byte)50,
    (byte)2, (byte)37, (byte)102, (byte)50, (byte)103, (byte)207, (byte)81, (byte)62, (byte)138, (byte)164, (byte)140, (byte)64, (byte)247, (byte)115,
    (byte)40, (byte)41, (byte)252, (byte)54, (byte)189, (byte)207, (byte)124, (byte)147, (byte)122, (byte)243, (byte)83, (byte)34, (byte)160, (byte)64, (byte)189, (byte)226, (byte)202, (byte)181, (byte)55, (byte)158, (byte)121, (byte)78, (byte)143, (byte)41, (byte)58, (byte)27, (byte)77, (byte)186, (byte)214, (byte)23, (byte)132, (byte)100, (byte)180, (byte)26, (byte)37, (byte)247, (byte)254, (byte)97, (byte)214, (byte)57, (byte)30, (byte)46, (byte)96, (byte)44, (byte)138, (byte)15, (byte)162, (byte)93, (byte)222, (byte)239, (byte)189, (byte)72, (byte)15, (byte)79, (byte)136, (byte)210, (byte)44, (byte)233, (byte)99, (byte)72, (byte)234, (byte)225, (byte)245, (byte)27, (byte)111, (byte)175, (byte)132, (byte)112, (byte)135, (byte)253, (byte)66, (byte)215, (byte)168, (byte)156, (byte)168, (byte)79, (byte)78, (byte)140, (byte)14, (byte)129, (byte)37, (byte)238, (byte)196, (byte)34, (byte)245, (byte)141, (byte)148, (byte)161, (byte)29, (byte)110, (byte)32, (byte)255, (byte)247, (byte)52, (byte)48, (byte)102, (byte)42, (byte)54, (byte)97, (byte)185, (byte)10, (byte)114, (byte)225, (byte)247, (byte)254, (byte)108, (byte)116, (byte)73, (byte)84, (byte)242, (byte)86, (byte)15, (byte)72, (byte)68, (byte)172, (byte)74, (byte)107, (byte)103, (byte)222, (byte)246, (byte)152, (byte)67, (byte)12, (byte)104, (byte)245, (byte)20, (byte)112, (byte)94, (byte)197, (byte)201, (byte)89, (byte)182, (byte)214, (byte)6, (byte)182, (byte)165, (byte)209, (byte)79, (byte)192, (byte)211, (byte)163, (byte)208, (byte)12, (byte)73, (byte)53, (byte)99, (byte)59, (byte)182, (byte)186, (byte)48, (byte)184, (byte)215, (byte)22, (byte)24, (byte)233, (byte)109, (byte)206, (byte)59, (byte)0, (byte)118, (byte)141, (byte)25, (byte)50, (byte)242, (byte)247, (byte)240, (byte)238, (byte)127, (byte)236, (byte)241, (byte)224, (byte)20, (byte)61, (byte)65, (byte)148, (byte)120, (byte)192, (byte)99, (byte)172, (byte)194, (byte)135, (byte)61, (byte)147, (byte)251, (byte)161, (byte)219, (byte)252, (byte)187, (byte)154, (byte)115, (byte)193, (byte)118, (byte)167, (byte)130, (byte)174, (byte)211, (byte)236, (byte)141, (byte)14, (byte)8, (byte)244, (byte)110, (byte)66, (byte)210, (byte)110, (byte)236, (byte)255, (byte)25, (byte)16, (byte)134, (byte)70, (byte)196, (byte)163, (byte)30, (byte)177, (byte)238, (byte)225, (byte)237, (byte)12, (byte)14, (byte)215, (byte)40, (byte)77, (byte)206, (byte)76, (byte)122, (byte)205, (byte)20, (byte)183, (byte)106, (byte)230, (byte)230, (byte)123, (byte)209, (byte)77, (byte)102, (byte)65, (byte)241, (byte)41, (byte)213, (byte)219, (byte)79, (byte)37, (byte)61, (byte)10, (byte)154, (byte)19, (byte)93, (byte)33, (byte)72, (byte)105, (byte)247, (byte)221, (byte)145, (byte)179, (byte)69, (byte)38, (byte)234, (byte)163, (byte)218, (byte)131, (byte)179, (byte)30, (byte)114, (byte)150, (byte)106, (byte)17, (byte)187, (byte)229, (byte)106, (byte)7, (byte)112
    };


    private static ObjectMapper objectMapper = new ObjectMapper();
    private static ResourceConfig app = new ResourceConfig().packages("com.amazonaws.serverless.proxy.jersey")
    .register(MultiPartFeature.class)
    .register(new ResourceBinder())
    .property("jersey.config.server.tracing.type", "ALL")
    .property("jersey.config.server.tracing.threshold", "VERBOSE");
    private static JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    private static ResourceConfig httpApiApp = new ResourceConfig().packages("com.amazonaws.serverless.proxy.jersey")
    .register(MultiPartFeature.class)
    .register(new ResourceBinder())
    .property("jersey.config.server.tracing.type", "ALL")
    .property("jersey.config.server.tracing.threshold", "VERBOSE");
    private static JerseyLambdaContainerHandler<HttpApiV2ProxyRequest, AwsProxyResponse> httpApiHandler;

    private static Context lambdaContext = new MockLambdaContext();

    private String type;

    public void initJerseyParamEncodingTest(String reqType) {
        type = reqType;
        LambdaContainerHandler.getContainerConfig().addBinaryContentTypes(MediaType.MULTIPART_FORM_DATA);
    }

    public static Collection<Object> data() {
        return Arrays.asList(new Object[]{"API_GW", "ALB", "HTTP_API"});
    }

    private AwsProxyRequestBuilder getRequestBuilder(String path, String method) {
        return new AwsProxyRequestBuilder(path, method);
    }

    private AwsProxyResponse executeRequest(AwsProxyRequestBuilder requestBuilder, Context lambdaContext) {
        switch (type) {
            case "API_GW":
                if (handler == null) {
                    handler = JerseyLambdaContainerHandler.getAwsProxyHandler(app);
                }
                return handler.proxy(requestBuilder.build(), lambdaContext);
            case "ALB":
                if (handler == null) {
                    handler = JerseyLambdaContainerHandler.getAwsProxyHandler(app);
                }
                return handler.proxy(requestBuilder.alb().build(), lambdaContext);
            case "HTTP_API":
                if (httpApiHandler == null) {
                    httpApiHandler = JerseyLambdaContainerHandler.getHttpApiV2ProxyHandler(httpApiApp);
                }
                return httpApiHandler.proxy(requestBuilder.toHttpApiV2Request(), lambdaContext);
            default:
                throw new RuntimeException("Unknown request type: " + type);
        }
    }

    @MethodSource("data")
    @ParameterizedTest
    void queryString_uriInfo_echo(String reqType) {
        initJerseyParamEncodingTest(reqType);
        AwsProxyRequestBuilder request = getRequestBuilder("/echo/query-string", "GET")
        .json()
        .queryString(QUERY_STRING_KEY, QUERY_STRING_NON_ENCODED_VALUE);

        AwsProxyResponse output = executeRequest(request, lambdaContext);
        assertEquals(200, output.getStatusCode());
        assertEquals("application/json", output.getMultiValueHeaders().getFirst("Content-Type"));

        validateMapResponseModel(output, QUERY_STRING_KEY, QUERY_STRING_NON_ENCODED_VALUE);
    }

    @MethodSource("data")
    @ParameterizedTest
    void queryString_notEncoded_echo(String reqType) {
        initJerseyParamEncodingTest(reqType);
        AwsProxyRequestBuilder request = getRequestBuilder("/echo/query-string", "GET")
        .json()
        .queryString(QUERY_STRING_KEY, QUERY_STRING_NON_ENCODED_VALUE);

        AwsProxyResponse output = executeRequest(request, lambdaContext);
        assertEquals(200, output.getStatusCode());
        assertEquals("application/json", output.getMultiValueHeaders().getFirst("Content-Type"));

        validateMapResponseModel(output, QUERY_STRING_KEY, QUERY_STRING_NON_ENCODED_VALUE);
    }

    @ParameterizedTest
    @Disabled("We expect to only receive decoded values from API Gateway")
    @MethodSource("data")
    void queryString_encoded_echo(String reqType) {
        initJerseyParamEncodingTest(reqType);
        AwsProxyRequestBuilder request = getRequestBuilder("/echo/query-string", "GET")
        .json()
        .queryString(QUERY_STRING_KEY, QUERY_STRING_ENCODED_VALUE);

        AwsProxyResponse output = executeRequest(request, lambdaContext);
        assertEquals(200, output.getStatusCode());
        assertEquals("application/json", output.getMultiValueHeaders().getFirst("Content-Type"));

        validateMapResponseModel(output, QUERY_STRING_KEY, QUERY_STRING_NON_ENCODED_VALUE);
    }

    @MethodSource("data")
    @ParameterizedTest
    void simpleQueryParam_encoding_expectDecodedParam(String reqType) {
        initJerseyParamEncodingTest(reqType);
        AwsProxyRequestBuilder request = getRequestBuilder("/echo/decoded-param", "GET").queryString("param", SIMPLE_ENCODED_PARAM);

        AwsProxyResponse resp = executeRequest(request, lambdaContext);
        assertEquals(200, resp.getStatusCode());
        validateSingleValueModel(resp, SIMPLE_ENCODED_PARAM);
    }

    @MethodSource("data")
    @ParameterizedTest
    void jsonQueryParam_encoding_expectDecodedParam(String reqType) {
        initJerseyParamEncodingTest(reqType);
        AwsProxyRequestBuilder request = getRequestBuilder("/echo/decoded-param", "GET").queryString("param", JSON_ENCODED_PARAM);

        AwsProxyResponse resp = executeRequest(request, lambdaContext);
        assertEquals(200, resp.getStatusCode());
        validateSingleValueModel(resp, JSON_ENCODED_PARAM);
    }

    @MethodSource("data")
    @ParameterizedTest
    void simpleQueryParam_encoding_expectEncodedParam(String reqType) {
        initJerseyParamEncodingTest(reqType);
        AwsProxyRequestBuilder request = getRequestBuilder("/echo/encoded-param", "GET").queryString("param", SIMPLE_ENCODED_PARAM);
        String encodedVal = "";
        try {
            encodedVal = URLEncoder.encode(SIMPLE_ENCODED_PARAM, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            fail("Could not encode parameter value");
        }
        AwsProxyResponse resp = executeRequest(request, lambdaContext);
        assertEquals(200, resp.getStatusCode());
        validateSingleValueModel(resp, encodedVal);
    }

    @MethodSource("data")
    @ParameterizedTest
    void jsonQueryParam_encoding_expectEncodedParam(String reqType) {
        initJerseyParamEncodingTest(reqType);
        AwsProxyRequestBuilder request = getRequestBuilder("/echo/encoded-param", "GET").queryString("param", JSON_ENCODED_PARAM);
        String encodedVal = "";
        try {
            encodedVal = URLEncoder.encode(JSON_ENCODED_PARAM, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            fail("Could not encode parameter value");
        }
        AwsProxyResponse resp = executeRequest(request, lambdaContext);
        assertEquals(200, resp.getStatusCode());
        validateSingleValueModel(resp, encodedVal);
    }

    @MethodSource("data")
    @ParameterizedTest
    void queryParam_encoding_expectFullyEncodedUrl(String reqType) {
        initJerseyParamEncodingTest(reqType);
        String paramValue = "/+=";
        AwsProxyRequestBuilder request = getRequestBuilder("/echo/encoded-param", "GET").queryString("param", paramValue);
        AwsProxyResponse resp = executeRequest(request, lambdaContext);
        assertNotNull(resp);
        assertEquals(200, resp.getStatusCode());
        validateSingleValueModel(resp, "%2F%2B%3D");
    }

    @MethodSource("data")
    @ParameterizedTest
    void pathParam_encoded_routesToCorrectPath(String reqType) {
        initJerseyParamEncodingTest(reqType);
        String encodedParam = "http%3A%2F%2Fhelloresource.com";
        String path = "/echo/encoded-path/" + encodedParam;
        AwsProxyRequestBuilder request = getRequestBuilder(path, "GET");
        AwsProxyResponse resp = executeRequest(request, lambdaContext);
        assertNotNull(resp);
        assertEquals(200, resp.getStatusCode());
        validateSingleValueModel(resp, encodedParam);
    }

    @MethodSource("data")
    @ParameterizedTest
    void pathParam_encoded_returns404(String reqType) {
        initJerseyParamEncodingTest(reqType);
        String encodedParam = "http://helloresource.com";
        String path = "/echo/encoded-path/" + encodedParam;
        AwsProxyRequestBuilder request = getRequestBuilder(path, "GET");
        AwsProxyResponse resp = executeRequest(request, lambdaContext);
        assertNotNull(resp);
        assertEquals(404, resp.getStatusCode());
    }

    @ParameterizedTest
    @Disabled
    @MethodSource("data")
    void queryParam_listOfString_expectCorrectLength(String reqType) {
        initJerseyParamEncodingTest(reqType);
        AwsProxyRequestBuilder request = getRequestBuilder("/echo/list-query-string", "GET").queryString("list", "v1,v2,v3");
        AwsProxyResponse resp = executeRequest(request, lambdaContext);
        assertNotNull(resp);
        assertEquals(200, resp.getStatusCode());
        validateSingleValueModel(resp, "3");
    }

    @MethodSource("data")
    @ParameterizedTest
    void multipart_getFileSize_expectCorrectLength(String reqType)
    throws IOException {
        initJerseyParamEncodingTest(reqType);
        AwsProxyRequestBuilder request = getRequestBuilder("/echo/file-size", "POST")
        .formFilePart("file", "myfile.jpg", FILE_CONTENTS);
        AwsProxyResponse resp = executeRequest(request, lambdaContext);
        assertNotNull(resp);
        assertEquals(200, resp.getStatusCode());
        validateSingleValueModel(resp, "" + FILE_CONTENTS.length);
    }

    private void validateSingleValueModel(AwsProxyResponse output, String value) {
        try {
            SingleValueModel response = objectMapper.readValue(output.getBody(), SingleValueModel.class);
            assertNotNull(response.getValue());
            assertEquals(value, response.getValue());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception while parsing response body: " + e.getMessage());
        }
    }

    private void validateMapResponseModel(AwsProxyResponse output, String key, String value) {
        try {
            MapResponseModel response = objectMapper.readValue(output.getBody(), MapResponseModel.class);
            assertNotNull(response.getValues().get(key));
            assertEquals(value, response.getValues().get(key));
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception while parsing response body: " + e.getMessage());
        }
    }
}
