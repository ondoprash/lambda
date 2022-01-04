package com.ondo.lambda;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisFirehoseEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisFirehoseEvent.Record;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondo.utils.pn.PushNotification;
import com.ondo.utils.pn.PushNotifier;
import com.ondo.utils.pn.PushSenderUtil;
//import com.sun.org.slf4j.internal.Logger;

import redis.clients.jedis.Jedis;

public class FirehoseEventHandler implements RequestHandler<KinesisFirehoseEvent, List<TxRecord>> {
	private final Jedis jedis;
	private final AmazonDynamoDB ddb;
	private final ObjectMapper objectMapper;
	static final Long sixhours = 1000L * 60 * 60 * 6;

	static final Long fivemins = 1000L * 60 * 5;
	
	static final Long deduptime = 1000L*119;
	
	static final int SixMonthsTTLForTempLookup = 5000; //more than 6 months hours.(> 6*30*24)
	
	static final int smalllerTTLForDashboard=49;

	public FirehoseEventHandler() {
		this.jedis = new Jedis("prod-redis-o1.qtjwxv.ng.0001.use1.cache.amazonaws.com");
		this.ddb = AmazonDynamoDBClientBuilder.defaultClient();
		this.objectMapper = new ObjectMapper();
		objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
	}

	private Long currentTime() {
		return new Date().getTime();
	}
	/*
	 * Function Name: handleRequest
	 * 
	 * Input Parameters: Context of Kinesis Firehose and a list of records
	 * 
	 * A typical msg record looks like -
	 * "$GPRP,FEE4224035F6,EC62B75C3898,-63,0DFF590001000084AF2482573B500A094F4E444F5F42414E44,1608271015";
	 * 
	 * - 5th record (4th indexed) is the BLE Adv payload Returns a list of records
	 */

	@Override
	public List<TxRecord> handleRequest(KinesisFirehoseEvent input, Context context) {
		LambdaLogger logger = context.getLogger();
		logger.log("Lambda Starts");

		if (input.getRecords() == null || input.getRecords().isEmpty()) {
			logger.log("Its Empty List - No records found!");
			logger.log("Lambda Ends here!");
			return emptyList();
		}

		logger.log("Total Input Records Size " + input.getRecords().size());
		// De-Duplication Logic Starts
		List<TxRecord> transformedRecords = new ArrayList<>();
		
		/*New logic starts*/
		List<BandEvent> bandEvents = new ArrayList<>();
		Set<String> bandIds = new HashSet<>();
		
		List<BridgeEvent> bridgeEvents = new ArrayList<>();
		Set<String> bridgeMacIds = new HashSet<>();		
		
		Set<String> bandBridgeMacIds = new HashSet<>();
		List<BridgeEvent> bandBridgeEvents = new ArrayList<>();
		
		
		Set<String> allUniqueBandRecords = new HashSet<String>();
		Set<String> bleAdvPayloadSet = new HashSet<String>();
		
		List<String> allDuplicateBandMsgs = new ArrayList<String>();
		List<String> allBridgeMsgs = new ArrayList<String>();
		
		int bridgeSetSize=0;
		
		
		for (Record record : input.getRecords()) {
			String rawDataString = new String(record.getData().array(), UTF_8);
			
			if (rawDataString.split(",")[0].equals("{\"data\":[\"$GPRP"))
			{
				String bleAdvPayload = rawDataString.split(",")[4];
				
				if (bleAdvPayloadSet.contains(bleAdvPayload) == false) {
					allUniqueBandRecords.add(rawDataString);
					bleAdvPayloadSet.add(bleAdvPayload);
				}
				else
				{
					allDuplicateBandMsgs.add(rawDataString);
				}
				
			}
			else if(rawDataString.split(",")[0].equals("{\"data\":[\"$HBRP"))
			{
				allBridgeMsgs.add(rawDataString);
				BridgeEvent bridgeEvent = parseBridgeRawDataString(rawDataString);
				bridgeEvents.add(bridgeEvent);		
				bridgeMacIds.add(bridgeEvent.getMacId());
				
				++bridgeSetSize;
				continue;
				
			}
			else
			{
				System.out.println("No-Band-Bridge Message");
				continue;
			}
			
			
			
		}

		// De-Duplication Logic Ends
		
		

		int setSize = allUniqueBandRecords.size();
		

		for (String rawDataString : allUniqueBandRecords) {
			if (rawDataString.split(",")[0].equals("{\"data\":[\"$GPRP")) {
				BandEvent bandEvent = parseRawDataString(rawDataString);
				bandEvents.add(bandEvent);
				bandIds.add(bandEvent.getBandId());
				
				BridgeEvent bandBridgeEvent = bandEvent.getBridgeEvent();
				bandBridgeEvents.add(bandBridgeEvent);
				bandBridgeMacIds.add(bandBridgeEvent.getMacId());				
				
			} 

		}

		
		/*New logic ends*/		
		
		
		
		/*Processing KeepAlive as BridgeEvents Starts*/
	
		Map<String, BridgeInfo> bridgeInfoMap = getBridgeDetails(bridgeMacIds);	
		List<BridgeDBRecord> bridgeDbRecords;
		try {
			bridgeDbRecords = prepareDBRecordForBridgeEvent(bridgeInfoMap, bridgeSetSize, bridgeEvents);
			
			prepareToInsertBridgeIntoDB(bridgeDbRecords);
			
			/*Processing KeepAlive as BridgeEvents Ends*/	
			
			
			/*Processing BandEvent as BridgeEvents Starts*/
			
			Map<String, BridgeInfo> bandBridgeInfoMap = getBridgeDetails(bandBridgeMacIds);	
			List<BridgeDBRecord> bandBridgeDbRecords = prepareDBRecordForBridgeEvent(bandBridgeInfoMap, setSize, bandBridgeEvents);
			
			
			prepareToInsertBridgeIntoDB(bandBridgeDbRecords);
			
			/*Processing BandEvent as BridgeEvents Ends*/	
			
			
			
			/* Processing Band Messages (GPRP) Starts */
			
			
			
			Map<String, WearerInfo> wearerInfoMap = getBandDetails(bandIds);
			
			
			
			List<PNEligible> pnEligibles = new ArrayList<PNEligible>();
			List<String> allSucessBandPNtimesUpdate = new ArrayList<String>();
			
			List<DBRecord> dbRecords = prepareDBRecordAndPNEligibleForBandEvent(wearerInfoMap, setSize, bandEvents,pnEligibles,allSucessBandPNtimesUpdate );
			
			
			prepareToInsertIntoDB(dbRecords);
			
			prepareToInsertIntoTempLookupTable(dbRecords);
			


			sendPNPerWearerPerFacility(pnEligibles,allSucessBandPNtimesUpdate, logger);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	

	
		return transformedRecords;
	}

	
	
	private List<BridgeDBRecord>  prepareDBRecordForBridgeEvent(Map<String, BridgeInfo> bridgeInfoMap, 
			int bridgeSetSize,List<BridgeEvent> bridgeEvents ) throws Exception 
		{
			
		List<BridgeDBRecord> bridgeDBRecords = new ArrayList<>();
		
		for (int i = 0; i < bridgeSetSize; i++) {
			BridgeEvent bridgeEvent = bridgeEvents.get(i);
			// System.out.println("----BEFORE---" + bandEvent.getBandId() );
			if (null != bridgeInfoMap.get(bridgeEvent.getMacId())) {
				BridgeDBRecord bridgeDBRecord = mapToBridgeDBRecord(bridgeInfoMap.get(bridgeEvent.getMacId()), bridgeEvent);

				bridgeDBRecords.add(bridgeDBRecord);

			}
		
		
		}
		return bridgeDBRecords;
		}
	
	
	
	
	
	private List<DBRecord>  prepareDBRecordAndPNEligibleForBandEvent(Map<String, WearerInfo> wearerInfoMap, 
			int setSize,List<BandEvent> bandEvents, List<PNEligible> pnEligibles,	List<String> allSucessBandPNtimesUpdate ) throws Exception {
		
		
		List<DBRecord> dbRecords = new ArrayList<>();
		
	
	
		for (int i = 0; i < setSize; i++) {
			BandEvent bandEvent = bandEvents.get(i);
			
			
			WearerInfo wearerInfo = wearerInfoMap.get(bandEvent.getBandId());
			Long timeDiff = 0L;
			if ((null != wearerInfo) && (null != wearerInfo.getLastTempTime()))
			{
				 timeDiff = currentTime() - wearerInfo.getLastTempTime();
			}
	
			if ((null != wearerInfo) && (timeDiff >= deduptime))
			{
				
				DBRecord dbRecord = mapToDBRecord(wearerInfoMap.get(bandEvent.getBandId()), bandEvent);
				

				dbRecords.add(dbRecord);

				WearerInfo wInfo = wearerInfoMap.get(bandEvent.getBandId());
				
				recordLastTempAndTime(wInfo, bandEvent);
				

				if (bandEvent.getCurTemp() >= wInfo.getAlertThresholdId()
						&& 
					LambdaUtil.isDeterminateTemperatureFloat(bandEvent.getCurTemp(), bandEvent.getAmbTemp())
				   ) 
				{
					//When the warning temperature is reported second time
					
					if (wInfo.getpNEligible() == 1) 
					{
						// When the warning temperature time and current time gap is more than 5 mins
						
						if (wInfo.getLastWarningTempTime() == null
								|| (currentTime() - wInfo.getLastWarningTempTime()) >= fivemins) 
						{
						// When the last PN sent time is null OR  PN sent time is more than 6 hours ago
							if (wInfo.getpNSentTime() == null || (currentTime() - wInfo.getpNSentTime()) >= sixhours) 
							{
								PNEligible newPNEligible = fillInPNDetails(wearerInfoMap.get(bandEvent.getBandId()),
										bandEvent);

								pnEligibles.add(newPNEligible);

		
								allSucessBandPNtimesUpdate.add(wInfo.getBandId());								
								 
							} 
						// When the   PN sent time is LESS than 6 hours ago
							else 
							{
							
							}
						} 
						// When the warning temperature time and current time gap is LESS than 5 mins
						else 
						{
							
						}
					} 
					//When the warning temperature is reported first time
					else if (wInfo.getpNEligible() == 0) 
					{
						resetPNRelatedData(wInfo, 1,bandEvent);						
					} 
					else 
					{
						//Should not come here at all
					}

				} 
				//When temperature is NOT a warning
				else 
				{ 
							
					resetPNRelatedData(wInfo, 0,bandEvent);

				}

				
			}

			
		}
		
		
		return dbRecords;
		
	}
	
	private void resetPNRelatedData(WearerInfo wInfo, Integer pnEligible, BandEvent bandEvent) {
		try {
			WearerInfo wearerInfo = objectMapper.readValue(jedis.get(wInfo.getBandId()), WearerInfo.class);
			
			if (pnEligible == 1) {
				wearerInfo.setpNEligible(pnEligible);
				
				wearerInfo.setLastWarningTempTime(new Date().getTime());
				wearerInfo.setLastWarningTemp(bandEvent.getCurTemp());			
				
			} else if (pnEligible == 0) {
				wearerInfo.setpNEligible(pnEligible);
				
				wearerInfo.setLastWarningTempTime(null);
				wearerInfo.setLastWarningTemp(null);	
			}
			jedis.set(wInfo.getBandId(), objectMapper.writeValueAsString(wearerInfo));
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void recordLastTempAndTime(WearerInfo wInfo, BandEvent bandEvent) {
		try {
			WearerInfo wearerInfo = objectMapper.readValue(jedis.get(wInfo.getBandId()), WearerInfo.class);
			
			
			wearerInfo.setLastTemp(bandEvent.getCurTemp());
			wearerInfo.setLastTempTime(new Date().getTime());

			jedis.set(wInfo.getBandId(), objectMapper.writeValueAsString(wearerInfo));
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	

	private String convertBandId(String batteryVal) {
		return String.valueOf(Integer.parseInt(batteryVal, 16));
	}

	private Float convertBatteryVal(String batteryVal) {
		/*
		 * return Float.valueOf(batteryVal.substring(0, 1)) +
		 * Float.valueOf((Integer.parseInt(batteryVal.substring(1, 2), 16))) / 10;
		 */

		int num = Integer.parseInt(batteryVal, 16);
		// System.out.println((float)num/10);

		return (3 - (float) num / 10);
	}

	private Float convertAmbientTemp(String ambTemp) {
		return Float.valueOf(Integer.parseInt(ambTemp.substring(0, 2), 16))
				+ (Float.valueOf(ambTemp.substring(2, 3)) / 10);
	}

	private Float convertSkinTemp(String skinTemp) {
		return Float.valueOf(Integer.parseInt(skinTemp.substring(0, 2), 16))
				+ (Float.valueOf(skinTemp.substring(2, 3)) / 10);
	}

	private PNEligible fillInPNDetails(WearerInfo wearerInfo, BandEvent bandEvent) throws Exception {
		// System.out.println("---INSIDE " + wearerInfo.getBandId());
		return PNEligible.builder().setWearerId(wearerInfo.getWearerId()).setFcltyId(wearerInfo.getFacilityId())
									.setWarningTemp(bandEvent.getCurTemp()).build();
	}

	private void sendPNPerWearerPerFacility(List<PNEligible> pNEligibles,List<String> updatePNTimeList, LambdaLogger logger) {
		

		if (pNEligibles != null && pNEligibles.size() > 0) {
			Map<String, Set<String>> facilityAndNoOfWearer = new HashMap<>();
			for (PNEligible pnEligible : pNEligibles) {
				Set<String> temp = facilityAndNoOfWearer.get(pnEligible.getFcltyId());
				if (temp == null) {
					temp = new HashSet<>();
				}
				temp.add(pnEligible.getWearerId());
				facilityAndNoOfWearer.put(pnEligible.getFcltyId(), temp);

			}
			

			PushNotifier pushNotifier = new PushSenderUtil();
			for (String fcltyid : facilityAndNoOfWearer.keySet()) {
	
				for (int i = 0; i < facilityAndNoOfWearer.get(fcltyid).size(); i++) {
					//List <String> tempList=new ArrayList<>(facilityAndNoOfWearer.get(fcltyid));
					
					for (UserPNRecord userPNRecord : redisGiveMeListOfUDR(fcltyid)) {
						for (PnData data : userPNRecord.getPnRecord()) {
							PushNotification pushNotification = new PushNotification();
							pushNotification.setPushDeviceType(data.getPlatform());
							pushNotification.setPushMsgContent(
									"Warning temperature reported for an ONDO Band wearer. Tap here to review the dashboard.");
							pushNotification.setPushRegId(data.getPNRegnToken());
							
						
							pushNotifier.sendPushNotification(pushNotification, logger);
						
							
							
						}

					}
				}

			}
			//After sending PN, put the PN logs for each wearer
			for (PNEligible pnEligible : pNEligibles) {
				
						
					Dbconnection.addPNLOGS(Integer.valueOf(pnEligible.getWearerId()),pnEligible.getWarningTemp() );
								
			
				
				}
			
			
			
			
		} else {

		}

		// find out the count of a given facility ID ==> no of wearers eligible ==> no
		// of PNs to be sent

		// find out all PN registration token and related platform (android/iOS) for a
		// given facility
		// Send PN using FCM to all users as per the facility count
		// Send PN using APNS to all users as per the facility count

		
		
		
		
		
		logger.log("Total " + updatePNTimeList.size() + " WearerInfo need to Update In Redis");
		for (String bndid : updatePNTimeList) {

			try {
				WearerInfo wearerInfo = objectMapper.readValue(jedis.get(bndid), WearerInfo.class);
				wearerInfo.setpNSentTime(new Date().getTime());
				wearerInfo.setpNEligible(0);
				wearerInfo.setLastWarningTempTime(null);
			
				jedis.set(bndid, objectMapper.writeValueAsString(wearerInfo));
			} catch (JsonMappingException e) {
				logger.log("Error " + e.getMessage());
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				logger.log("Error " + e.getMessage());
				e.printStackTrace();
			}

		}
		
		
		
	}

	private List<UserPNRecord> redisGiveMeListOfUDR(String facilityId) {
		//
		try {
			
			
			if (null != jedis.get(facilityId)) {
				List<UserPNRecord> cacheList = getUserPNInfoRds(jedis.get("PNRegnFId-" + facilityId)).values().stream()
						.collect(Collectors.toList());
				System.out.println(cacheList);
				return cacheList;
			}
			return null;
		} finally {
			jedis.close();
		}
	}

	private Map<String, UserPNRecord> getUserPNInfoRds(final String rawJson) {
		try {
			return (Map<String, UserPNRecord>) objectMapper.readValue(rawJson,
					new TypeReference<Map<String, UserPNRecord>>() {
					});
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	private void prepareToInsertBridgeIntoDB(List<BridgeDBRecord> dbRecords) {

		int j = 1;
		for (int i = 0; i < dbRecords.size() /* && i < 25 */; i++) {

			List<WriteRequest> writeRequests = new ArrayList<>(dbRecords.size());

			for (j = 1; j <= 25 && i < dbRecords.size(); j++, i++) {
				BridgeDBRecord dbRecord = dbRecords.get(i);
				PutRequest putRequest = new PutRequest();
				putRequest.addItemEntry("id", new AttributeValue().withS(dbRecord.getId()));

				//putRequest.addItemEntry("bridgeId", new AttributeValue().withS(dbRecord.getBandId()));
				putRequest.addItemEntry("facilityIdbridgeId", new AttributeValue().withS(dbRecord.getFacilityId()+"_" + dbRecord.getBridgeId()));
				putRequest.addItemEntry("currentTime", new AttributeValue().withN(String.valueOf(dbRecord.getCurrentTime())));
				putRequest.addItemEntry("ddbTime", new AttributeValue().withN(String.valueOf(dbRecord.getDDBTime())));
				putRequest.addItemEntry("ttl", new AttributeValue().withN("" + EpochTime.epochTTL(smalllerTTLForDashboard)));
			//	putRequest.addItemEntry("bridgeId", new AttributeValue().withN(String.valueOf(dbRecord.getBridgeId())));
				WriteRequest writeRequest = new WriteRequest(putRequest);
				writeRequests.add(writeRequest);
			}

			// Do batching
			BatchWriteItemRequest request = new BatchWriteItemRequest();
			request.addRequestItemsEntry("bridgestatus", writeRequests);

		//	System.out.println("#########BEFORE  Bridge INSERT " + writeRequests.size());

			i--;

			ddb.batchWriteItem(request);

			// Reinitialize j for anothr batch of 25 records or less
			j = 1;
		}

	}
	
	

	private void prepareToInsertIntoTempLookupTable(List<DBRecord> dbRecords) {

		int j = 1;
		for (int i = 0; i < dbRecords.size() /* && i < 25 */; i++) {

			List<WriteRequest> writeRequests = new ArrayList<>(dbRecords.size());

			for (j = 1; j <= 25 && i < dbRecords.size(); j++, i++) {
				DBRecord dbRecord = dbRecords.get(i);
				PutRequest putRequest = new PutRequest();
				putRequest.addItemEntry("id", new AttributeValue().withS(dbRecord.getId()));

				putRequest.addItemEntry("bandId", new AttributeValue().withS(dbRecord.getBandId()));
				putRequest.addItemEntry("batVolt", new AttributeValue().withN(String.valueOf(dbRecord.getBatVolt())));
				putRequest.addItemEntry("curTemp", new AttributeValue().withN(String.valueOf(dbRecord.getCurTemp())));
				putRequest.addItemEntry("ambTemp", new AttributeValue().withN(String.valueOf(dbRecord.getAmbTemp())));
				putRequest.addItemEntry("accVal", new AttributeValue().withN(String.valueOf(dbRecord.getAccValue())));
				putRequest.addItemEntry("displayName",
						new AttributeValue().withS(dbRecord.getfName() + " " + dbRecord.getfName()));
				putRequest.addItemEntry("fcltyId", new AttributeValue().withS(dbRecord.getFcltyId()));
				putRequest.addItemEntry("fName", new AttributeValue().withS(dbRecord.getfName()));
				putRequest.addItemEntry("grpId", new AttributeValue().withS(dbRecord.getGrpId()));
				putRequest.addItemEntry("lName", new AttributeValue().withS(dbRecord.getfName()));
				putRequest.addItemEntry("curTime", new AttributeValue().withN(String.valueOf(dbRecord.getCurTime())));
				putRequest.addItemEntry("ttl", new AttributeValue().withN("" + EpochTime.epochTTL(SixMonthsTTLForTempLookup)));
				putRequest.addItemEntry("wearerId", new AttributeValue().withN(String.valueOf(dbRecord.getWearerId())));
				putRequest.addItemEntry("rssi",new AttributeValue().withN(String.valueOf(dbRecord.getRssi())));
				putRequest.addItemEntry("fwVersion", new AttributeValue().withN(String.valueOf(dbRecord.getFwVersion())));
				putRequest.addItemEntry("gatewayBLEMacId",new AttributeValue().withS(dbRecord.getGatewayBLEMacId()));
				WriteRequest writeRequest = new WriteRequest(putRequest);
				writeRequests.add(writeRequest);
			}

			// Do batching
			BatchWriteItemRequest request = new BatchWriteItemRequest();
			request.addRequestItemsEntry("templookup", writeRequests);

		//	System.out.println("#########JUST BEFORE INSERT " + writeRequests.size());

			i--;

			ddb.batchWriteItem(request);

			// Reinitialize j for anothr batch of 25 records or less
			j = 1;
		}

	}
	
	
	private void prepareToInsertIntoDB(List<DBRecord> dbRecords) {

		int j = 1;
		for (int i = 0; i < dbRecords.size() /* && i < 25 */; i++) {

			List<WriteRequest> writeRequests = new ArrayList<>(dbRecords.size());

			for (j = 1; j <= 25 && i < dbRecords.size(); j++, i++) {
				DBRecord dbRecord = dbRecords.get(i);
				PutRequest putRequest = new PutRequest();
				putRequest.addItemEntry("id", new AttributeValue().withS(dbRecord.getId()));

				putRequest.addItemEntry("bandId", new AttributeValue().withS(dbRecord.getBandId()));
				putRequest.addItemEntry("batVolt", new AttributeValue().withN(String.valueOf(dbRecord.getBatVolt())));
				putRequest.addItemEntry("curTemp", new AttributeValue().withN(String.valueOf(dbRecord.getCurTemp())));
				putRequest.addItemEntry("ambTemp", new AttributeValue().withN(String.valueOf(dbRecord.getAmbTemp())));
				putRequest.addItemEntry("accVal", new AttributeValue().withN(String.valueOf(dbRecord.getAccValue())));
				putRequest.addItemEntry("displayName",
						new AttributeValue().withS(dbRecord.getfName() + " " + dbRecord.getfName()));
				putRequest.addItemEntry("fcltyId", new AttributeValue().withS(dbRecord.getFcltyId()));
				putRequest.addItemEntry("fName", new AttributeValue().withS(dbRecord.getfName()));
				putRequest.addItemEntry("grpId", new AttributeValue().withS(dbRecord.getGrpId()));
				putRequest.addItemEntry("lName", new AttributeValue().withS(dbRecord.getfName()));
				putRequest.addItemEntry("curTime", new AttributeValue().withN(String.valueOf(dbRecord.getCurTime())));
				putRequest.addItemEntry("ttl", new AttributeValue().withN("" + EpochTime.epochTTL(49)));
				putRequest.addItemEntry("wearerId", new AttributeValue().withN(String.valueOf(dbRecord.getWearerId())));
				WriteRequest writeRequest = new WriteRequest(putRequest);
				writeRequests.add(writeRequest);
			}

			// Do batching
			BatchWriteItemRequest request = new BatchWriteItemRequest();
			request.addRequestItemsEntry("dashboard", writeRequests);

		//	System.out.println("#########JUST BEFORE INSERT " + writeRequests.size());

			i--;

			ddb.batchWriteItem(request);

			// Reinitialize j for anothr batch of 25 records or less
			j = 1;
		}

	}
	
	
	

	private DBRecord mapToDBRecord(WearerInfo wearerInfo, BandEvent bandEvent) throws Exception {
		

		return DBRecord.builder()
				.setId(UUID.randomUUID().toString())
				.setBandId(wearerInfo.getBandId())
				.setWearerId(Integer.parseInt(wearerInfo.getWearerId()))
				.setFcltyId(wearerInfo.getFacilityId())
				.setfName(wearerInfo.getFirstName()).
				setlName(wearerInfo.getLastName())
				.setGrpId(wearerInfo.getWearerGroupId())
				.setAltTH(Double.valueOf(String.valueOf(wearerInfo.getAlertThresholdId())))
				.setAmbTemp(bandEvent.getAmbTemp())
				.setBatVolt(Double.valueOf(String.valueOf(bandEvent.getBattery())))
				.setCurTemp(Double.valueOf(String.valueOf(bandEvent.getCurTemp())))
				.setAccValue(bandEvent.getAccValue())
				.setCurTime(new Date().getTime())
				.setFwVersion(bandEvent.getFwVersion())
				.setRssi(bandEvent.getRssi())
				.setGatewayBLEMacId(bandEvent.getGatewayBLEMacId())
				.build();		
	}
	
	private BridgeDBRecord mapToBridgeDBRecord(BridgeInfo bridgeInfo, BridgeEvent bridgeEvent) throws Exception {

		return BridgeDBRecord.builder().setId(UUID.randomUUID().toString())
				.setBridgeId(bridgeInfo.getBridgeId())
				.setFacilityId(bridgeInfo.getFacilityId())
				.setCurrentTime(bridgeEvent.getHeartBeatTime())
				.setDDBTime(new Date().getTime()).build();
				
	}
	
	
	
	

	private Map<String, WearerInfo> getBandDetails(Set<String> bandIds) {
		String[] bandIdArray = bandIds.toArray(new String[] {});

		if (bandIds == null || bandIds.size() == 0) {
			System.out.println("No Band Id");
			List<String> values = new ArrayList<>();
			return values.stream().map(this::getWearerInfo).filter(Optional::isPresent).map(Optional::get)
					.collect(Collectors.toMap(WearerInfo::getBandId, Function.identity()));
		} else {

			List<String> values = jedis.mget(bandIdArray);

			while (values.remove(null)) {
			}

			return values.stream().map(this::getWearerInfo).filter(Optional::isPresent).map(Optional::get)
					.collect(Collectors.toMap(WearerInfo::getBandId, Function.identity()));
		}
	}
	
	
	private Map<String, BridgeInfo> getBridgeDetails(Set<String> macIds) {
		String[] macIdArray = macIds.toArray(new String[] {});

		if (macIds == null || macIds.size() == 0) {
			System.out.println("No Bridge Id");
			List<String> values = new ArrayList<>();
			return values.stream().map(this::getBridgeInfo).filter(Optional::isPresent).map(Optional::get)
					.collect(Collectors.toMap(BridgeInfo::getBleMacId, Function.identity()));
		} else {

			List<String> values = jedis.mget(macIdArray);

			while (values.remove(null)) {
			}

			return values.stream().map(this::getBridgeInfo).filter(Optional::isPresent).map(Optional::get)
					.collect(Collectors.toMap(BridgeInfo::getBleMacId, Function.identity()));
		}
	}
	

	private Optional<WearerInfo> getWearerInfo(final String rawJson) {
		try {
			return Optional.of(objectMapper.readValue(rawJson, WearerInfo.class));
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
	
	private Optional<BridgeInfo> getBridgeInfo(final String rawJson) {
		try {
			return Optional.of(objectMapper.readValue(rawJson, BridgeInfo.class));
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
	

	private BandEvent parseRawDataString(final String rawDataString) {
		BandEvent bandEvent = new BandEvent();
		bandEvent.setRawData(rawDataString);

		String[] fields = rawDataString.split(",");

		bandEvent.setFwVersion(Integer.parseInt(fields[4].substring(8,10),16));
		bandEvent.setRssi(Integer.parseInt((fields[3])));
		bandEvent.setGatewayBLEMacId(fields[2]);		
		
		bandEvent.setBandId(convertBandId(fields[4].substring(10, 18)));
		bandEvent.setCurTemp(convertSkinTemp(fields[4].substring(18, 21)));
		bandEvent.setAmbTemp(convertAmbientTemp(fields[4].substring(21, 24)));
		bandEvent.setBattery(convertBatteryVal(fields[4].substring(24, 25)));
		bandEvent.setAccValue(Integer.parseInt(fields[4].substring(25, 28), 16));
		
		BridgeEvent bridgeEvent = new BridgeEvent();
		
		bridgeEvent.setMacId(fields[2]);
		
		String timePlusSth = fields[5].toString();
		String timeBridge = timePlusSth.substring(0, timePlusSth.length()-4);
		Long bridgeTime = Long.parseLong(timeBridge);
		
		bridgeEvent.setHeartBeatTime(bridgeTime);
		
		bandEvent.setBridgeEvent(bridgeEvent);		

		return bandEvent;
	}
	
	
	
	/*
	 * { "data": [
	 * "$GPRP,FA71BEF0EAB7,C1D8ACFD33AB,-51,0DFF5900060000816F1861621FFF0A094F4E444F5F42414E44,1623741675"
	 * ] } ondo/band June 15, 2021, 12:51:09 (UTC+0530) { "data": [
	 * "$HBRP,EE74EF551F28,EE74EF551F28,-127,00000000,1623741668" ] }
	 * 
	 */
	
	
	private BridgeEvent parseBridgeRawDataString(final String rawDataString) {
		BridgeEvent bridgeEvent = new BridgeEvent();
		bridgeEvent.setRawData(rawDataString);
	//	System.out.println("Before parsing " + rawDataString);
		String[] fields = rawDataString.split(",");	

	//	System.out.println("Bridge fields 5 " + fields[5].toString());
		bridgeEvent.setMacId(fields[2]);
		String timePlusSth = fields[5].toString();
	//	System.out.println("timePlusSth " + timePlusSth);
		String timeBridge = timePlusSth.substring(0, timePlusSth.length()-4);
	//	System.out.println("timeBridge " + timeBridge);
		Long bridgeTime = Long.parseLong(timeBridge);
	//	System.out.println("bridgeTime " + bridgeTime);
		bridgeEvent.setHeartBeatTime(bridgeTime);	

		return bridgeEvent;
	}
}

//cbandi.FirehoseEventHandler::handleRequest