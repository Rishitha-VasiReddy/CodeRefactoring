/*
 * This project is licensed under the MIT license. Module model-view-viewmodel is using ZK framework licensed under LGPL (see lgpl-3.0.txt).
 *
 * The MIT License
 * Copyright © 2014-2022 Ilkka Seppälä
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.api.gateway;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * An adapter to communicate with the Image microservice.
 */
@Slf4j
@Component
public class ImageClientImpl implements ImageClient {

  /**
   * Makes a simple HTTP Get request to the Image microservice.
   *
   * @return The path to the image
   */
  @Override
  public String getImagePath() {
    private static final String MICROSERVICE_URL = "http://localhost:50005/image-path";

    var httpClient = HttpClient.newHttpClient();
    var httpGet = HttpRequest.newBuilder()
        .GET()
        .uri(URI.create(MICROSERVICE_URL))
        .build();

    try {
      LOGGER.info("Sending request to fetch image path");
      var httpResponse = httpClient.send(httpGet, BodyHandlers.ofString());
      logResponse(httpResponse);
      if(isSuccessResponse(httpResponse.statusCode())) {
        return httpResponse.body();
      } else {
        // Consider throwing a custom exception for specific error scenarios.
        throw new ImageRequestFailedException("Image path request failed");
      } 
    } catch (IOException | InterruptedException ie) {
      LOGGER.error("Failure occurred while getting image path", ie);
      // Consider throwing a custom exception for better error reporting
      throw new ImageRequestFailedException("Failed to retrieve image path", ie);
    } catch (Exception exception) {
      // Catching anyother exception which might be thrown
      // Printing the entire stack trace to understand the root cause of the issue.
      exception.printStackTrace();
       throw exception; 
    }

    return new ErrorPage();
  }

  private void logResponse(HttpResponse<String> httpResponse) {
    // Log detailed information about the response, e.g., header, status code etc.
    LOGGER.info("Response Headers: {}", httpResponse.headers().toString());
    LOGGER.info("Response Status Code: {}", httpResponse.statusCode());
  }

  private boolean isSuccessResponse(int responseCode) {
    return responseCode >= 200 && responseCode <= 299;
  }
}