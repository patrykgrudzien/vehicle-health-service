package me.grudzien.patryk.unit.oauth2.authentication.chain.steps;

import io.vavr.CheckedFunction1;
import io.vavr.Function1;
import io.vavr.control.Try;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class FifthStepTest {

    @Test
    void performSingleAuthOperation() {
        // when
        final Function1<String, Try<Map<String, String>>> liftTry = CheckedFunction1.liftTry(
                input -> new ObjectMapper().readValue(input, new TypeReference<Map<String, String>>() {})
        );
        final Try<Map<String, String>> apply = liftTry.apply(JSON_GOOGLE_JWT_CLAIMS);


        final Function1<String, Try<Map<Object, Object>>> liftTryObject = CheckedFunction1.liftTry(
                input -> new ObjectMapper().readValue(input, new TypeReference<Map<Object, Object>>() {})
        );
        final Try<Map<Object, Object>> applyObject = liftTryObject.apply(JSON_GOOGLE_JWT_CLAIMS);
    }

    @Test
    void updateAuthenticationItemsOnSuccessOperation() {
    }

    @Test
    void handleFailureDuringAuthOperation() {
    }

    private static final String JSON_GOOGLE_JWT_CLAIMS = "{\n" +
            "  \"iss\": \"https://accounts.google.com\",\n" +
            "  \"azp\": \"289764469472-jnvmuvelohvieo6bn1362p89qkt6cjrk.apps.googleusercontent.com\",\n" +
            "  \"aud\": \"289764469472-jnvmuvelohvieo6bn1362p89qkt6cjrk.apps.googleusercontent.com\",\n" +
            "  \"sub\": \"111447851331853329027\",\n" +
            "  \"email\": \"jurik99.pg@gmail.com\",\n" +
            "  \"email_verified\": true,\n" +
            "  \"at_hash\": \"66EjTLQM2-dMqEzP-lPR8A\",\n" +
            "  \"google\": {\n" +
            "    \"gic\": \"ALaw3bTmw7d38ODU94EsnP8FdSdHSNmMZoIct_t7bEsVg4MZvA\"\n" +
            "  },\n" +
            "  \"name\": \"Patryk Grudzień\",\n" +
            "  \"picture\": \"https://lh5.googleusercontent.com/-gO47D_2iSCw/AAAAAAAAAAI/AAAAAAAACvc/kpLYvBfPLY0/s96-c/photo.jpg\",\n" +
            "  \"given_name\": \"Patryk\",\n" +
            "  \"family_name\": \"Grudzień\",\n" +
            "  \"locale\": \"pl\",\n" +
            "  \"iat\": 1550007083,\n" +
            "  \"exp\": 1550010683\n" +
            "}";
}