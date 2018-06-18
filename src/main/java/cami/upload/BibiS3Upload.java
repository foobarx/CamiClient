package cami.upload;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.unibi.cebitec.aws.s3.transfer.BiBiS3;

public class BibiS3Upload implements IUpload {
    public static final String ACCESS_KEY = "accessKey";
    public static final String SECRET_KEY = "secretAccessKey";
    public static final String PATH = "path";
    public static final String SESSION_TOKEN = "sessionToken";
    public static final String FINGERPRINT = "fingerprint";
    private static final String MALFORMED_JSON = "malformed json file";

    public void upload(String sourcePath, String credentialsPath) throws IOException {
        System.out.println("upload start with credentials: " + credentialsPath);
        File file = new File(credentialsPath);
        if (!file.exists()) {
            throw new IOException("file:" + credentialsPath + " does not exist.");
        }
        Map<String, String> credentials = this.getCredentials(file);
        String accessKey = credentials.get(ACCESS_KEY);
        String secretAccessKey = credentials.get(SECRET_KEY);
        String targetPath = credentials.get(PATH);
        String sessionToken = credentials.get(SESSION_TOKEN);
        String fingerprint = credentials.get(FINGERPRINT);

        startUpload(accessKey, secretAccessKey, sessionToken, fingerprint, sourcePath, targetPath);
    }

    public Map<String, String> getCredentials(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> credentials;
        try {
            credentials = mapper.readValue(file, Map.class);
        } catch (IOException e) {
            throw new IOException(MALFORMED_JSON);
        }
        return credentials;
    }

    public void startUpload(String accessKey, String secretKey, String sessionToken, String fingerprint,
                            String source, String target) {
        String[] args = {"--metadata", "fingerprint", fingerprint, "--access-key", accessKey,
                "--secret-key", secretKey, "--session-token", sessionToken, "-u", source, target};
        BiBiS3.main(args);
    }
}
