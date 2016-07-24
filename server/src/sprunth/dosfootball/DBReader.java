package sprunth.dosfootball;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

import java.util.HashMap;
import java.util.Map;


public class DBReader {
    AmazonDynamoDBClient dynamoDB;

    private void Init() throws Exception {
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        dynamoDB = new AmazonDynamoDBClient(credentials);
        Region usVirginia = Region.getRegion(Regions.US_EAST_1);
        dynamoDB.setRegion(usVirginia);
    }

    public DBReader() throws Exception {
        Init();
    }

    public Map<String, Map<String, String>> GetData() throws Exception {

    }

    public void AddDataTest() throws Exception {
        try {
            String tableName = "FootballDosTest";

            // wait for the table to move into ACTIVE state
            TableUtils.waitUntilActive(dynamoDB, tableName);

            // Describe our new table
            DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
            TableDescription tableDescription = dynamoDB.describeTable(describeTableRequest).getTable();
            System.out.println("Table Description: " + tableDescription);

            Map<String, AttributeValue> item = createDBItem("TestPlayerName", "p2", "p2team", "p3", "p3team");
            PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
            PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
            System.out.println("PutItemResult: " + putItemResult);


        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to AWS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with AWS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    private static Map<String, AttributeValue> createDBItem(String playerName, String... alternatingPlayerTeamStrings) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("PlayerName", new AttributeValue(playerName));

        Map<String, AttributeValue> playerTeamConnections = new HashMap<>();
        boolean isPlayerName = true;
        String altPlayerNameTmp = "";
        for (String s : alternatingPlayerTeamStrings) {
            if (isPlayerName)
                altPlayerNameTmp = s;
            else
                playerTeamConnections.put(altPlayerNameTmp, new AttributeValue(s));

            isPlayerName = !isPlayerName;
        }

        item.put("PlayerTeamConnections", new AttributeValue().withM(playerTeamConnections));

        return item;
    }


}
