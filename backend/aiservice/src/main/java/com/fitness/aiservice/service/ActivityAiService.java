package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
@Data
public class ActivityAiService {
    private final GeminiService geminiService;

    //This methode is being called int the activity message listener
    public Recommendation generateRecommendation(Activity activity)
    {
        String prompt= createPromptForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt); //Response comming from ai models
        log.info("Response from ai {}",aiResponse);
        return processAiResponse(activity,aiResponse); //This method will return object of recommendation
    }

//    private Recommendation processAiResponse(Activity activity, String aiResponse) {
//        try{
//            //Using ObjectMapper to work with Json like reading writing
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode rootNode = mapper.readTree(aiResponse); //converting the response to a jason tree
//            JsonNode textnode = rootNode.path("candidates")
//                    .get(0)
//                    .path("content")
//                    .get("parts")
//                    .get(0)
//                    .path("text");
//
//            //clean the ai output
//            String jsonContent = textnode.asText()
//                    .replaceAll("```json\\n","")
//                    .replaceAll("\\n```","")
//                    .trim();
//            JsonNode analysisJson = mapper.readTree(jsonContent);
//            JsonNode analysisNode = analysisJson.path("analysis"); //will provide full analysis node
//            //Now extract all data from analysis
//            //Convert it to human-readable format
//            StringBuilder fullAnalysis = new StringBuilder();
//            addAnalysisSection(fullAnalysis, analysisNode,"overall","Overall: ");
//            addAnalysisSection(fullAnalysis, analysisNode,"pace","Pace: ");
//            addAnalysisSection(fullAnalysis, analysisNode,"heartRate","Heart Rate: ");
//            addAnalysisSection(fullAnalysis, analysisNode,"caloriesBurned","CaloriesBurned: ");
//            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
//            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
//            List<String> safety = extractSafety(analysisJson.path("safety"));
//            return Recommendation.builder()     // These are the object of recommendation
//                    .activityId(activity.getId())
//                    .userId(activity.getUserId())
//                    .type(activity.getType().toString())
//                    .recommendation(fullAnalysis.toString().trim())
//                    .improvements(improvements)
//                    .suggestions(suggestions)
//                    .safety(safety)
//                    .createdAt(LocalDateTime.now())
//                    .build();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return createDefaultRecommendation(activity); //this method will work if any error ocuur on passing recommendation through ai model
//        }
//    }
private Recommendation processAiResponse(Activity activity, String aiResponse) {
    try {
        ObjectMapper mapper = new ObjectMapper();

        // GeminiService already returns the generated JSON
        String jsonContent = aiResponse
                .replace("```json", "")
                .replace("```", "")
                .trim();

        JsonNode analysisJson = mapper.readTree(jsonContent);

        JsonNode analysisNode = analysisJson.path("analysis");

        StringBuilder fullAnalysis = new StringBuilder();

        addAnalysisSection(
                fullAnalysis,
                analysisNode,
                "overall",
                "Overall: "
        );

        addAnalysisSection(
                fullAnalysis,
                analysisNode,
                "pace",
                "Pace: "
        );

        addAnalysisSection(
                fullAnalysis,
                analysisNode,
                "heartRate",
                "Heart Rate: "
        );

        addAnalysisSection(
                fullAnalysis,
                analysisNode,
                "caloriesBurned",
                "CaloriesBurned: "
        );

        List<String> improvements =
                extractImprovements(analysisJson.path("improvements"));

        List<String> suggestions =
                extractSuggestions(analysisJson.path("suggestions"));

        List<String> safety =
                extractSafety(analysisJson.path("safety"));

        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getType().toString())
                .recommendation(fullAnalysis.toString().trim())
                .improvements(improvements)
                .suggestions(suggestions)
                .safety(safety)
                .createdAt(LocalDateTime.now())
                .build();

    } catch (Exception e) {
        log.error("Error processing AI response: {}", aiResponse, e);
        return createDefaultRecommendation(activity);
    }
}
    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()     // These are the object of recommendation
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getType().toString())
                .recommendation("Unable to generate detail analysis")
                .improvements(Collections.singletonList("Continue wth your current routine"))
                .suggestions(Collections.singletonList("Consider consulting a fitness consultant"))
                .safety(Arrays.asList("Always warmup before exercise"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafety(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach(item -> safety.add(item.asText()));
        }
        return safety.isEmpty() ?
                Collections.singletonList("No specific safety to provided") :
                safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if(suggestionsNode.isArray()){
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s: %s", workout, description));
            });
        }
        return suggestions.isEmpty() ?
                Collections.singletonList("No specific suggestions to provided") :
                suggestions;
    }

    // Now we are extracting improvements through this method
    private List<String> extractImprovements(JsonNode improvementsNode) {
    List<String> improvements = new ArrayList<>(); // All objects of improvements will be iterate to this list
    if(improvementsNode.isArray()){
        improvementsNode.forEach(improvement -> {
            String area = improvement.path("area").asText();
            String recomendation = improvement.path("recommendation").asText();
            improvements.add(String.format("%s: %s", area, recomendation));
        });
    }
    return improvements.isEmpty() ?
            Collections.singletonList("No specific improvements to provided") :
            improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix)
    {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix)
                .append(analysisNode.path(key).asText())
                    .append("\n\n");

        }
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Workout name",
              "description": "Detailed workout description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

        Analyze this activity:
        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }
}
