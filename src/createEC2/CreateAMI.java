package createEC2;

import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;

public class CreateAMI {

	static AmazonEC2Client client;
	static String keyName = "mykeypair";
	static void createEC2Client()
	{
		//AWSCredentials credentials = new PropertiesCredentials(CreateAMI.class.getResourceAsStream("AWSCredentials.properties"));
				AWSCredentials credentials = new ProfileCredentialsProvider("default").getCredentials();
				client = new AmazonEC2Client(credentials);
				client.setEndpoint("ec2.us-west-2.amazonaws.com");
	}
	static void createSecurityGroup()
	{

		CreateSecurityGroupRequest csgr = new CreateSecurityGroupRequest();
		csgr.withGroupName("JavaSecurityGroup").withDescription("My Security Group");
		
		CreateSecurityGroupResult csgResult =client.createSecurityGroup(csgr);
		IpPermission ipPermission =
			    new IpPermission();

			ipPermission.withIpRanges("0.0.0.0/0")
			            .withIpProtocol("tcp")
			            .withFromPort(22)
			            .withToPort(22);
			AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest =
				    new AuthorizeSecurityGroupIngressRequest();

				authorizeSecurityGroupIngressRequest.withGroupName("JavaSecurityGroup")
				                                    .withIpPermissions(ipPermission);
				client.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);
	}
	static void createKeyPair()
	{
		
		CreateKeyPairRequest kpRequest = new CreateKeyPairRequest();
		kpRequest.withKeyName(keyName);
		
		CreateKeyPairResult kpResult = client.createKeyPair(kpRequest);
		
		KeyPair kp = new KeyPair();
		kp = kpResult.getKeyPair();
		String privateKey = kp.getKeyMaterial();
	}
	static void runInstance()
	{
		RunInstancesRequest runInstanceReq = new RunInstancesRequest();
		runInstanceReq.withImageId("ami-5189a661")
						.withInstanceType("t2.micro")
						.withMinCount(1)
						.withMaxCount(1)
						.withKeyName(keyName)
						.withSecurityGroups("JavaSecurityGroup");
		RunInstancesResult runInstanceResult = client.runInstances(runInstanceReq);
	}
	public static void main(String args[]) throws IOException
	{
		try{
	createEC2Client();
	//createSecurityGroup();
	//createKeyPair();
	runInstance();
		
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}
