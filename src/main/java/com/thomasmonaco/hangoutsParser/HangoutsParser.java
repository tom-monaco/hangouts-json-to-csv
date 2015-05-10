package com.thomasmonaco.hangoutsParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.text.WordUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tom on 5/9/15.
 */
public class HangoutsParser {

    private ObjectMapper objectMapper = new ObjectMapper();
    private DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

    public static void main(String args[]) throws Exception {
        new HangoutsParser().parseJson();
    }

    private void parseJson() throws Exception {
        JsonNode rootNode = objectMapper.readTree(getClass().getResourceAsStream("/Hangouts.json"));

        HashMap<String,String> participantIdToNameMap = new HashMap<>();

        // assemble the map of chat ids to fallback names
        List<JsonNode> participantDatas = rootNode.findParents("participant_data");
        for( JsonNode participantData : participantDatas) {
            JsonNode realPartData = participantData.get("participant_data");
            for (JsonNode partDataEntry : realPartData) {
                String id = partDataEntry.findPath("gaia_id").asText();
                String name = partDataEntry.findPath("fallback_name").asText();
                participantIdToNameMap.put(id, name);
            }
        }

        System.out.println("conversation id,timestamp,sender,text");

        List<JsonNode> chatMessages = rootNode.findParents("chat_message");
        for( JsonNode chatMessage : chatMessages) {
            HashMap<String,String> row = new HashMap<>();
            String timestamp = chatMessage.findPath("timestamp").asText();
            Long tsAsLong = Long.parseLong(timestamp);
            tsAsLong /= 1000;
            Date tsAsDate = new Date(tsAsLong);

            System.out.print(chatMessage.findPath("id").asText());
            System.out.print(",");
            System.out.print('"'+dateFormat.format(tsAsDate)+'"');
            System.out.print(",");
            System.out.print(participantIdToNameMap.get(chatMessage.findPath("gaia_id").asText()));
            System.out.print(",");
            String originalMessage = chatMessage.findPath("text").asText();
            System.out.print('"' + WordUtils.wrap(originalMessage, 80)  +'"');
            System.out.println();
        }
    }


}
