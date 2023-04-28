package com.bachlinh.order.security.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Secret token handler for handle client secret authenticated in this project.
 *
 * @author Hoang Minh Quan
 */
public final class ClientSecretHandler {
    private static final List<ClientSecret> CLIENT_SECRETS = Collections.synchronizedList(new ArrayList<>());

    /**
     * Return client secretly authenticated. If the token is not authenticated, the
     * token will return null.
     *
     * @param clientSecret token for checking.
     * @return authenticated token or null.
     */
    public String getClientSecret(String clientSecret) {
        int index = findSecretWithClientSecret(clientSecret);
        if (index < 0) {
            return null;
        }
        return CLIENT_SECRETS.get(index).secret();
    }

    /**
     * Set client secret and refresh token authenticated.
     */
    public void setClientSecret(String clientSecret, String refreshToken) {
        CLIENT_SECRETS.add(new ClientSecret(refreshToken, clientSecret));
        CLIENT_SECRETS.sort(Comparator.comparing(ClientSecret::secret));
    }

    /**
     * Remove client secret with client secret or refresh token.
     * Handler will use refresh token when the client's secret is null.
     *
     * @param clientSecret secret token for removal, can be null.
     * @param refreshToken refresh token for remove secret can be null.
     * @apiNote use client secret for better performance.
     */
    public void removeClientSecret(String refreshToken, String clientSecret) {
        int index = findSecretWithClientSecret(clientSecret);
        if (index < 0) {
            index = findSecretWithRefreshToken(refreshToken);
        }
        if (index < 0) {
            return;
        }
        CLIENT_SECRETS.remove(index);
    }

    private int findSecretWithClientSecret(String clientSecret) {
        if (clientSecret == null) {
            return -1;
        }
        ClientSecret secret = new ClientSecret(null, clientSecret);
        return Collections.binarySearch(CLIENT_SECRETS, secret, Comparator.comparing(comparator -> comparator.secret));
    }

    private int findSecretWithRefreshToken(String refreshToken) {
        for (int i = 0; i < CLIENT_SECRETS.size() - 1; i++) {
            if (CLIENT_SECRETS.get(i).refreshToken.equals(refreshToken)) {
                return i;
            }
        }
        return -1;
    }

    private record ClientSecret(String refreshToken, String secret) {
    }
}
