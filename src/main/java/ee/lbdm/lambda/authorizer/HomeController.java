package ee.lbdm.lambda.authorizer;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller
public class HomeController {

    @Get
    public AwsProxyRequest index(AwsProxyRequest request) {
        return request;
    }
}
