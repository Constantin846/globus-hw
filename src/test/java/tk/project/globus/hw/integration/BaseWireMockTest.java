package tk.project.globus.hw.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

@WireMockTest
public interface BaseWireMockTest {

  WireMockExtension getWireMockExtension();

  @SneakyThrows
  default void addWireMockExtensionStubGet(String url, String responseBody, String contentType) {

    addWireMockExtensionStubGet(HttpStatus.OK, url, responseBody, contentType);
  }

  @SneakyThrows
  default void addWireMockExtensionStubGet(
      HttpStatus status, String url, String responseBody, String contentType) {

    getWireMockExtension()
        .stubFor(
            WireMock.get(url)
                // .withQueryParams(params)
                .willReturn(
                    aResponse()
                        .withStatus(status.value())
                        .withHeader("Content-Type", contentType)
                        .withBody(responseBody)));
  }
}
