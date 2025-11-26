package tk.project.globus.hw.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.http.HttpStatus;

@WireMockTest
public interface BaseWireMockTest {

  @RegisterExtension
  WireMockExtension WIRE_MOCK_EXTENSION =
      WireMockExtension.newInstance()
          .options(WireMockConfiguration.wireMockConfig().dynamicPort().dynamicPort())
          .build();

  default void addWireMockExtensionStubGet(String url, String responseBody, String contentType) {
    addWireMockExtensionStubGet(HttpStatus.OK, url, responseBody, contentType);
  }

  default void addWireMockExtensionStubGet(
      HttpStatus status, String url, String responseBody, String contentType) {

    WIRE_MOCK_EXTENSION.stubFor(
        WireMock.get(url)
            // .withQueryParams(params)
            .willReturn(
                aResponse()
                    .withStatus(status.value())
                    .withHeader("Content-Type", contentType)
                    .withBody(responseBody)));
  }
}
