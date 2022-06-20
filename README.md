## Micronaut Lambda API Gateway Proxy Authorizer example
Given project is to illustrate a situation where Micronaut Lambda proxy handler does not populate properly the AwsProxyRequest → AwsProxyRequestContext → RequestContext → ApiGatewayAuthorizerContext → CognitoAuthorizerClaims  object.

## Steps to reproduce
- Create "Micronaut application" project with "aws-lambda" feature
- Add AwsProxyRequest to the controller method parameter (injected automatically by Micronaut during runtime)
- Build the app 
- Deploy app to AWS Lambda with Java 11 (Coretto) runtime and `io.micronaut.function.aws.proxy.MicronautLambdaHandler` as the function handler
- Test the Lambda method with "apigateway-aws-proxy" template:
  - Change the request path to be empty string
  - Change the HTTP method to `GET` 
  - Add `requestContex.authorizer` payload
- Expected result:
  - The method `request.getRequestContext().getAuthorizer().getClaims().getSubject()` returns the `requestContext.authorizer.claims.sub` value from the payload
- Actual result: The `CognitoAuthorizerClaims` object `request.getRequestContext().getAuthorizer().getClaims()` has only the username populated

## Example payload
Given example is taken from `apigateway-aws-proxy` template directly from Lambda console with modifications mentioned in previous step (authorizer sensitive info has been redacted)
```json
{
  "body": "eyJ0ZXN0IjoiYm9keSJ9",
  "resource": "/{proxy+}",
  "path": "",
  "httpMethod": "GET",
  "isBase64Encoded": true,
  "queryStringParameters": {
    "foo": "bar"
  },
  "multiValueQueryStringParameters": {
    "foo": [
      "bar"
    ]
  },
  "pathParameters": {
    "proxy": ""
  },
  "stageVariables": {
    "baz": "qux"
  },
  "headers": {
    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
    "Accept-Encoding": "gzip, deflate, sdch",
    "Accept-Language": "en-US,en;q=0.8",
    "Cache-Control": "max-age=0",
    "CloudFront-Forwarded-Proto": "https",
    "CloudFront-Is-Desktop-Viewer": "true",
    "CloudFront-Is-Mobile-Viewer": "false",
    "CloudFront-Is-SmartTV-Viewer": "false",
    "CloudFront-Is-Tablet-Viewer": "false",
    "CloudFront-Viewer-Country": "US",
    "Host": "1234567890.execute-api.us-east-1.amazonaws.com",
    "Upgrade-Insecure-Requests": "1",
    "User-Agent": "Custom User Agent String",
    "Via": "1.1 08f323deadbeefa7af34d5feb414ce27.cloudfront.net (CloudFront)",
    "X-Amz-Cf-Id": "cDehVQoZnx43VYQb9j2-nvCh-9z396Uhbp027Y2JvkCPNLmGJHqlaA==",
    "X-Forwarded-For": "127.0.0.1, 127.0.0.2",
    "X-Forwarded-Port": "443",
    "X-Forwarded-Proto": "https"
  },
  "multiValueHeaders": {
    "Accept": [
      "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
    ],
    "Accept-Encoding": [
      "gzip, deflate, sdch"
    ],
    "Accept-Language": [
      "en-US,en;q=0.8"
    ],
    "Cache-Control": [
      "max-age=0"
    ],
    "CloudFront-Forwarded-Proto": [
      "https"
    ],
    "CloudFront-Is-Desktop-Viewer": [
      "true"
    ],
    "CloudFront-Is-Mobile-Viewer": [
      "false"
    ],
    "CloudFront-Is-SmartTV-Viewer": [
      "false"
    ],
    "CloudFront-Is-Tablet-Viewer": [
      "false"
    ],
    "CloudFront-Viewer-Country": [
      "US"
    ],
    "Host": [
      "0123456789.execute-api.us-east-1.amazonaws.com"
    ],
    "Upgrade-Insecure-Requests": [
      "1"
    ],
    "User-Agent": [
      "Custom User Agent String"
    ],
    "Via": [
      "1.1 08f323deadbeefa7af34d5feb414ce27.cloudfront.net (CloudFront)"
    ],
    "X-Amz-Cf-Id": [
      "cDehVQoZnx43VYQb9j2-nvCh-9z396Uhbp027Y2JvkCPNLmGJHqlaA=="
    ],
    "X-Forwarded-For": [
      "127.0.0.1, 127.0.0.2"
    ],
    "X-Forwarded-Port": [
      "443"
    ],
    "X-Forwarded-Proto": [
      "https"
    ]
  },
  "requestContext": {
    "accountId": "123456789012",
    "resourceId": "123456",
    "stage": "prod",
    "requestId": "c6af9ac6-7b61-11e6-9a41-93e8deadbeef",
    "requestTime": "09/Apr/2015:12:34:56 +0000",
    "requestTimeEpoch": 1428582896000,
    "identity": {
      "cognitoIdentityPoolId": null,
      "accountId": null,
      "cognitoIdentityId": null,
      "caller": null,
      "accessKey": null,
      "sourceIp": "127.0.0.1",
      "cognitoAuthenticationType": null,
      "cognitoAuthenticationProvider": null,
      "userArn": null,
      "userAgent": "Custom User Agent String",
      "user": null
    },
    "path": "",
    "resourcePath": "/{proxy+}",
    "httpMethod": "GET",
    "apiId": "1234567890",
    "protocol": "HTTP/1.1",
    "authorizer": {
      "claims": {
        "sub": "removedjustincase",
        "cognito:groups": "eu-central-removedjustincase_Google",
        "token_use": "access",
        "scope": "openid removedjustincase email",
        "auth_time": "1655568239",
        "iss": "https://cognito-idp.eu-central-1.amazonaws.com/eu-central-removedjustincase",
        "exp": "Sat Jun 18 17:03:59 UTC 2022",
        "version": "2",
        "iat": "Sat Jun 18 16:03:59 UTC 2022",
        "client_id": "removedjustincase",
        "jti": "removedjustincase",
        "username": "google_removedjustincase"
      }
    }
  }
}
```

## Real test case reproduction via API Gateway
In order to create a real execution via API Gateway:
- Deploy the app in same way as in previous step
- Create API Gateway with a proxy resource integrated to the Lambda app
- Create a Cognito user pools with relevant configuration to be used via API Gateway
- Attach the Cognito user pool as the authorizer to the proxy resource
- Deploy the API Gateway
- Generate a valid JWT via Cognito API
- Send a request to the API Gateway with the JWT token

Official documentation: https://docs.aws.amazon.com/apigateway/latest/developerguide/apigateway-integrate-with-cognito.html
