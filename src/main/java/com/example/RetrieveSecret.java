package com.example;

import com.google.cloud.secretmanager.v1.*;

import java.nio.charset.StandardCharsets;

public class RetrieveSecret {
    public static void main(String[] args) throws Exception {
        String projectId = "pmkubproject";
        Secret secret =
                Secret.newBuilder()
                        .setReplication(
                                Replication.newBuilder()
                                        .setAutomatic(Replication.Automatic.newBuilder().build())
                                        .build())
                        .build();
        ProjectName projectName = ProjectName.of(projectId);
        System.out.println(projectName);

        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            SecretManagerServiceClient.ListSecretsPagedResponse pagedResponse = client.listSecrets(projectName);
            System.out.println(pagedResponse);            String secretName = "projects/pmkubproject/secrets/mysecret/versions/1";

            for (Secret secret1 : pagedResponse.iterateAll()) {
                String otherSecretId = extractSecretId(secret1);
                System.out.println("The secret value is: " + otherSecretId);
                SecretPayload secretPayload = client.accessSecretVersion(secretName).getPayload();
                System.out.println("secretPayload "+secretPayload);

            }
            SecretVersion secretVersion = client.getSecretVersion(secretName);
            printSecretVersion(client, secretVersion);
        }
    }

    private static String extractSecretId(Secret secret) {
        String[] secretNameTokens = secret.getName().split("/");
        return secretNameTokens[secretNameTokens.length - 1];
    }

    static void printSecretVersion(SecretManagerServiceClient client, SecretVersion version) {
        AccessSecretVersionResponse response = client.accessSecretVersion(version.getName());
        String payload = response.getPayload().getData().toStringUtf8();
        System.out.println("Reading secret value: " + payload);
        System.out.println("(Note: Don't print secret values in prod!)");
    }
}
